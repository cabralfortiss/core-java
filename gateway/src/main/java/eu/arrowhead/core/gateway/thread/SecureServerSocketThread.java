package eu.arrowhead.core.gateway.thread;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import eu.arrowhead.common.exception.AuthenticationException;
import eu.arrowhead.common.messages.ConnectToConsumerRequest;
import eu.arrowhead.common.security.SecurityUtils;
import eu.arrowhead.core.gateway.GatewayMain;
import eu.arrowhead.core.gateway.GatewayService;
import eu.arrowhead.core.gateway.model.GatewaySession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class SecureServerSocketThread extends Thread {

	private int port;
	@Nullable
	private SSLServerSocket sslServerSocket = null;
	private ConnectToConsumerRequest connectionRequest;
	private GatewaySession gatewaySession;
	private static final Logger log = Logger.getLogger(InsecureServerSocketThread.class.getName());

	public SecureServerSocketThread(GatewaySession gatewaySession, int port,
			ConnectToConsumerRequest connectionRequest) {
		this.port = port;
		this.connectionRequest = connectionRequest;
		this.gatewaySession = gatewaySession;
	}

	// TODO narrower try-catches
	public void run() {
		SSLContext sslContext = GatewayService.createSSLContext();
		// Socket for server to listen at.
		SSLServerSocketFactory serverFactory = sslContext.getServerSocketFactory();
		try {
			sslServerSocket = (SSLServerSocket) serverFactory.createServerSocket(port);
			sslServerSocket.setNeedClientAuth(true);
			sslServerSocket.setSoTimeout(connectionRequest.getTimeout());
			System.out.println("Secure serverSocket is now running at port: " + port + "\n");
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Creating secure serverSocket failed.");
		}

		try {
			// Accept a client connection once Server receives one.
			SSLSocket sslConsumerSocket = (SSLSocket) sslServerSocket.accept();
			SSLSession consumerSession = sslConsumerSocket.getSession();
			String consumerIPFromCert = consumerSession.getPeerHost();

			Certificate[] serverCerts = consumerSession.getPeerCertificates();
			X509Certificate cert = (X509Certificate) serverCerts[0];
			String subjectName = cert.getSubjectDN().getName();
			String consumerCNFromCert = SecurityUtils.getCertCNFromSubject(subjectName);

			// FIXME systemName != consumerCN (use new util function in arrowheadsystem)
			if (!connectionRequest.getConsumer().getSystemName().equals(consumerCNFromCert)
					| !connectionRequest.getConsumer().getAddress().equals(consumerIPFromCert)) {
				GatewayMain.portAllocationMap.replace(port, false, true);
				log.error("SecureServerThread: Consumer CNs or IPs not equal");
				throw new AuthenticationException("SecureServerThread: Consumer CNs or IPs not equal");
			}
			
			Channel channel = gatewaySession.getChannel();

			try {
				InputStream inConsumer = sslConsumerSocket.getInputStream();
				OutputStream outConsumer = sslConsumerSocket.getOutputStream();

				// Get the request from the Consumer
				byte[] inputFromConsumer = new byte[1024];
				byte[] inputFromConsumerFinal = new byte[inConsumer.read(inputFromConsumer)];
				System.arraycopy(inputFromConsumer, 0, inputFromConsumerFinal, 0, inputFromConsumerFinal.length);

				channel.basicPublish("", connectionRequest.getQueueName(), null, inputFromConsumerFinal);

				// Get the response and the control messages
				GetResponse controlMessage = channel.basicGet(connectionRequest.getControlQueueName().concat("resp"),
						false);
				while (controlMessage == null || !(new String(controlMessage.getBody()).equals("close"))) {
					GetResponse message = channel.basicGet(connectionRequest.getQueueName().concat("resp"), false);
					if (message == null) {
						System.out.println("No message retrieved");
					} else {
						outConsumer.write(message.getBody());
					}
					controlMessage = channel.basicGet(connectionRequest.getControlQueueName().concat("resp"), false);
				}
			} catch (SocketException e) {
				log.error("Socket closed by remote partner");
			} finally {
				// Close sockets and the connection
				sslConsumerSocket.close();
				sslServerSocket.close();
				channel.close();
				gatewaySession.getConnection().close();
			}

		} catch (IOException e) {
			e.printStackTrace();
			log.error("SecureServerThread: Creating secure clientSocket failed.");
		}
	}

}
