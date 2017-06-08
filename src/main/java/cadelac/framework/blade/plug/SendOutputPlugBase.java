package cadelac.framework.blade.plug;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.component.Component;
import cadelac.framework.blade.core.service.dispatch.Dispatcher;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.exception.FrameworkException;

public class SendOutputPlugBase<M extends Message, S extends State> 
	extends OutputPlugBase implements SendOutputPlug<M,S> {

	public SendOutputPlugBase(final String id_, final Component owner_) {
		super(id_, owner_);
		logger.debug("created sendOutputPlug [" + getOwner().getClass().getSimpleName() + "." + id_ + "]");
	}

	
	@Override
	public void send(final M message_) throws FrameworkException, Exception {
		logger.debug("SendOutputPlug.send: SendOutputPlug [" + getOwner().getId() + "." 
			+ getId() + "], message [" + message_.getClass().getSimpleName() + "]");
		if (this.getConnectedInputPlug()==null)
			throw new ArgumentException("SendOutputPlug.send: SendOutputPlug [" 
				+ getOwner().getId() + "." + getId() + "] must be connected to a SendInputPlug");
		Dispatcher.send(this.getConnectedInputPlug(), message_);
	}

	
	@Override
	public void delayedCall(final M message_, final long delay_) 
			throws FrameworkException, Exception {
		if (this.getConnectedInputPlug() == null) 
			throw new ArgumentException("SendOutputPlug [" + getOwner().getId() 
					+ "." + getId() + "] must be connected to a SendInputPlug");
		logger.debug("SendOutputPlug.delayedCall: SendOutputPlug [" 
			+ getOwner().getId() + "." + getId() + "] via SendInputPlug [" 
			+ this.getConnectedInputPlug().getOwningHandler().getId() + "." + this.getConnectedInputPlug().getId() + "]");
		Dispatcher.delayedCall(this.getConnectedInputPlug(), message_, delay_);
	}

	
	@Override
	public void recurringCall(final M message_, final long delay_, final long period_) 
			throws FrameworkException, Exception {
		if (this.getConnectedInputPlug() == null) 
			throw new ArgumentException("SendOutputPlug.recurringCall: SendOutputPlug [" 
				+ getOwner().getId() + "." + getId() + "] must be connected to a SendInputPlug");
		logger.debug("SendOutputPlug.recurringCall: SendOutputPlug [" + getOwner().getId() + "." + getId() + "] via SendInputPlug [" + 
				this.getConnectedInputPlug().getOwningHandler().getId() + "." + this.getConnectedInputPlug().getId() + "]");
		Dispatcher.recurringCall(this.getConnectedInputPlug(), message_, delay_, period_);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public SendInputPlug<M,S> getConnectedInputPlug() {
		return (SendInputPlug<M, S>) super.getInputPlug();
	}

	
	@Override
	public void setConnectedInputPlug(final SendInputPlug<M,S> inputPlug_) {
		super.setInputPlug(inputPlug_);
	}
	
	
//	@Override
//	public Handler<?, ?> getOwningHandler() {
//		return super.getOwningHandler();
//	}
	
	
	private static final Logger logger = Logger.getLogger(SendOutputPlug.class);
}
