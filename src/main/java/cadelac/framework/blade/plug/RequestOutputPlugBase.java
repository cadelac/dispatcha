package cadelac.framework.blade.plug;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.Framework;
import cadelac.framework.blade.core.service.dispatch.Dispatcher;
import cadelac.framework.blade.handler.Handler;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.exception.StateException;
import cadelac.lib.primitive.exception.SystemException;
import cadelac.lib.primitive.invocation.Response;


public class RequestOutputPlugBase<R,M extends Message, S extends State> 
	extends OutputPlugBase implements RequestOutputPlug<R,M,S>{

	public RequestOutputPlugBase(final String id_, final Handler<?,?> owningHandler_) {
		super(id_, owningHandler_);
		logger.debug("created RequestOutputPlug [" + owningHandler_.getClass().getSimpleName() + "." + id_ + "]");
	}

	@Override
	public Future<Response<R>> request(M message_) throws ArgumentException, StateException, SystemException, Exception {
		final long timeOut = Framework.getShell().getRack().getQuantum();
		return request(message_, timeOut);
	}


	@Override
	public Future<Response<R>> request(M message_, long timeOut_)
			throws ArgumentException, StateException, SystemException, Exception {
		if (this.getConnectedInputPlug() == null) 
			throw new ArgumentException("RequestOutputPlug [" + getOwner().getId() + "." + getId() + "] must be connected to RequestInputPlug");
		logger.debug("RequestOutputPlug.request: RequestOutputPlug [" + getOwner().getId() + "." + getId() 
			+ "], message ["+ message_.getClass().getSimpleName() 
			+ "], timeout (ms) [" + timeOut_ 
			+ "] via RequestInputPlug [" + this.getConnectedInputPlug().getOwningHandler().getId() + "." + this.getConnectedInputPlug().getId() 
			+ "]");
		return Dispatcher.request(this.getConnectedInputPlug(), message_, timeOut_);
	}


	@SuppressWarnings("unchecked")
	@Override
	public RequestInputPlug<R, M, S> getConnectedInputPlug() {
		return (RequestInputPlug<R, M, S>) super.getInputPlug();
	}


	@Override
	public void setConnectedPlug(RequestInputPlug<R, M, S> inputPlug_) {
		super.setInputPlug(inputPlug_);
	}
	

	private static final Logger logger = Logger.getLogger(RequestOutputPlug.class);
}
