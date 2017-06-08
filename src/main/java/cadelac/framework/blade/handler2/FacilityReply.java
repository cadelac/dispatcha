package cadelac.framework.blade.handler2;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.handler.AgentReply;

public interface FacilityReply<R,M extends Message> extends Facility {
	public ChannelInputTypedReply<R,M> getInput();
	public AgentReply<R,M,? extends State> getAgent();
	public void setAgent(final AgentReply<R,M,? extends State> agent_);
}
