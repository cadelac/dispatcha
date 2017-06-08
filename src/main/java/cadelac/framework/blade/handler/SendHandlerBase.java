package cadelac.framework.blade.handler;

import org.apache.log4j.Logger;

import cadelac.framework.blade.plug.SendInputPlug;
import cadelac.framework.blade.plug.SendInputPlugBase;
import cadelac.framework.blade.plug.SendOutputPlug;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.concept.state.StateLess;
import cadelac.lib.primitive.exception.ArgumentException;

public abstract class SendHandlerBase<M extends Message, S extends State>  
	extends HandlerBase<M,S> implements SendHandler<M,S> {

	public SendHandlerBase(final String id_) {
		super(id_);
		setInputPlug(new SendInputPlugBase<M,S>("inputPlug", this));
	}


	@SuppressWarnings("unchecked")
	@Override
	public SendInputPlug<M,S> getInputPlug() {
		return (SendInputPlug<M, S>) super.getInputPlug();
	}

	
	@Override
	public void invoke(final M msg_, final S state_) throws Exception {
		logger.debug(String.format("SendHandler.invoke: message [%s.%s], state [%s]",
				getId(),
				msg_.getClass().getSimpleName(),
				state_.getClass().getSimpleName()));
		if (state_.getId() == StateLess.STATELESS_STATE_ID) {
			// handler is state-less, no need to synchronize access to state
			process(msg_, state_);
		}
		else {
			// handler is not state-less, synchronize access to state
			synchronized (state_) {
				process(msg_, state_);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <N extends Message> SendOutputPlug<N,?> getSendOutputPlug(final String outputPlugName_) throws ArgumentException {
		return (SendOutputPlug<N,?>) super.getOutputPlug(outputPlugName_);
	}
	
	public <N extends Message> void addSendOutputPlug(final SendOutputPlug<N,?> outputPlug_) throws ArgumentException {
		super.addOutputPlug(outputPlug_.getId(), outputPlug_);
	}
	
	public <N extends Message> void addSendOutputPlug(final String outputPlugName_, final SendOutputPlug<N,?> outputPlug_)
			throws ArgumentException {
		super.addOutputPlug(outputPlugName_, outputPlug_);	
	}

	private static final Logger logger = Logger.getLogger(SendHandler.class);
}
