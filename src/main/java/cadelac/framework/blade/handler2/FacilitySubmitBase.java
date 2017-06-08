package cadelac.framework.blade.handler2;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.handler.AgentSubmit;

public class FacilitySubmitBase<M extends Message> extends FacilityBase implements FacilitySubmit<M> {

	public FacilitySubmitBase(final String id_) {
		super(id_);
		_input = new ChannelInputTypedSubmitBase<M>("inputChannel");
		_input.setFacility(this);
	}
	
	@Override
	public ChannelInputTypedSubmit<M> getInput() {
		return _input;
	}

	@Override
	public AgentSubmit<M,? extends State> getAgent() {
		return _agent; 
	}

	@Override
	public void setAgent(AgentSubmit<M, ? extends State> agent_) {
		_agent = agent_;
	}

	private final ChannelInputTypedSubmit<M> _input;
	private AgentSubmit<M,? extends State> _agent;
}
