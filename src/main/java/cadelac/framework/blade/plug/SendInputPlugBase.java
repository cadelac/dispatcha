package cadelac.framework.blade.plug;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.service.dispatch.Dispatcher;
import cadelac.framework.blade.handler.SendHandler;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.exception.StateException;
import cadelac.lib.primitive.exception.SystemException;
import cadelac.lib.primitive.plug.PlugBase;

public class SendInputPlugBase<M extends Message, S extends State> 
	extends PlugBase implements SendInputPlug<M,S> {

	public SendInputPlugBase(final String id_, final SendHandler<M,S> owningHandler_) {
		super(id_);
		_owningHandler = owningHandler_;
		logger.debug("created SendInputPlug [" + id_ + "]");
	}

	@Override
	public void send(M message_) throws ArgumentException, StateException, SystemException, Exception {
		logger.debug("SendInputPlug.send: SendInputPlug [" + _owningHandler.getId() + "." + getId() 
				+ "], message ["+ message_.getClass().getSimpleName() +"]");
		Dispatcher.send(this, message_);
	}


	@Override
	public void delayedCall(M message_, long delay_) 
			throws ArgumentException, StateException, SystemException, Exception {
		logger.debug("SendInputPlug.delayedCall: SendInputPlug [" + getId() 
				+ "], delay ["+ delay_ + "]");
		Dispatcher.delayedCall(this, message_, delay_);
	}

	
	@Override
	public void recurringCall(M message_, long delay_, long period_) 
			throws ArgumentException, StateException, SystemException, Exception {
		logger.debug("SendInputPlug.recurringCall: SendInputPlug [" + _owningHandler.getId() + "." + getId() 
				+ "] Handler ["+ _owningHandler.getId() + "], delay ["+ delay_ + "], period [" + period_ + "]");
		Dispatcher.recurringCall(this, message_, delay_, period_);
	}
	
	
	@Override
	public SendHandler<M,S> getOwningHandler() {
		return _owningHandler;
	}

	
	private static final Logger logger = Logger.getLogger(SendInputPlug.class);

	private final SendHandler<M,S> _owningHandler;
}
