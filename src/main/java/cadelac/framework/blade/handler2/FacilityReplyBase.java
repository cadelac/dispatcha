package cadelac.framework.blade.handler2;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.handler.AgentReply;

public class FacilityReplyBase<R,M extends Message> extends FacilityBase implements FacilityReply<R,M> {

	public FacilityReplyBase(final String id_) {
		super(id_);
		_input = new ChannelInputTypedReplyBase<R,M>("inputChannel");
		_input.setFacility(this);
	}
	
	@Override
	public ChannelInputTypedReply<R, M> getInput() {
		return _input;
	}

	@Override
	public AgentReply<R,M,? extends State> getAgent() { 
		return _agent; 
	}
	
	@Override
	public void setAgent(final AgentReply<R,M,? extends State> agent_) {
		_agent = agent_;
	}

	
	private final ChannelInputTypedReply<R,M> _input;
	private AgentReply<R,M,? extends State> _agent;
}
