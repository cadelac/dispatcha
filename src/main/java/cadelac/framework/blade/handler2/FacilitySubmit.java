package cadelac.framework.blade.handler2;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.handler.AgentSubmit;

public interface FacilitySubmit<M extends Message> extends Facility {
	public ChannelInputTypedSubmit<M> getInput();
	public AgentSubmit<M,? extends State> getAgent();
	public void setAgent(final AgentSubmit<M,? extends State> agent_);
}
