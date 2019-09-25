/*
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */

package eu.arrowhead.common.misc;

public enum CoreSystemService {
  AUTH_CONTROL_SERVICE("AuthorizationControl", "authorization"),
  TOKEN_GEN_SERVICE("TokenGeneration", "authorization/token"),
  EVENT_PUBLISH("EventPublishing", "eventhandler/publish"),
  EVENT_SUBSCRIPTION("EventSubscription", "eventhandler/subscription"),
  GSD_SERVICE("GlobalServiceDiscovery", "gatekeeper/init_gsd"),
  ICN_SERVICE("InterCloudNegotiations", "gatekeeper/init_icn"),
  GW_CONSUMER_SERVICE("ConnectToConsumer", "gateway/connectToConsumer"),
  GW_PROVIDER_SERVICE("ConnectToProvider", "gateway/connectToProvider"),
  GW_SESSION_MGMT("SessionManagement", "gateway/management"),
  ORCH_SERVICE("OrchestrationService", "orchestrator/orchestration"),
  SERVICE_REGISTRY_SERVICE("ServiceRegistry", "serviceregistry/register"),
  SERVICE_LOOKUP_SERVICE("ServiceLookup", "serviceregistry/query"),
  SYSTEM_REGISTRY_SERVICE("SystemRegistry", "systemregistry/publish"),
  DEVICE_REGISTRY_SERVICE("DeviceRegistry", "deviceregistry/publish"),
  ONBOARDING_SERVICE("Onboarding", "onboarding");

  private final String serviceDef;
  private final String serviceURI;

  CoreSystemService(String serviceDef, String serviceURI) {
    this.serviceDef = serviceDef;
    this.serviceURI = serviceURI;
  }

  public String getServiceDef() {
    return serviceDef;
  }

  public String getServiceURI() {
    return serviceURI;
  }

}
