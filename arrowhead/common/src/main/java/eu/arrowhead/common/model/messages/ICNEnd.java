package eu.arrowhead.common.model.messages;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ICNEnd {
	
	OrchestrationResponse instructions;

	public ICNEnd() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ICNEnd(OrchestrationResponse instructions) {
		this.instructions = instructions;
	}

	public OrchestrationResponse getInstructions() {
		return instructions;
	}

	public void setInstructions(OrchestrationResponse instructions) {
		this.instructions = instructions;
	}
}