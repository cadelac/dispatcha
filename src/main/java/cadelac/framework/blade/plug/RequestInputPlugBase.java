package cadelac.framework.blade.plug;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.Framework;
import cadelac.framework.blade.core.service.dispatch.Dispatcher;
import cadelac.framework.blade.handler.RequestHandler;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.exception.StateException;
import cadelac.lib.primitive.exception.SystemException;
import cadelac.lib.primitive.invocation.Response;
import cadelac.lib.primitive.plug.PlugBase;

public class RequestInputPlugBase<R, M extends Message, S extends State> 
	extends PlugBase implements RequestInputPlug<R,M,S>{

	public RequestInputPlugBase(final String id_, final RequestHandler<R,M,S> owningHandler_) {
		super(id_);
		_owningHandler = owningHandler_;
		logger.debug("created RequestInputPlug [" + id_ + "]");
	}

	
	@Override
	public Future<Response<R>> request(M message_) throws ArgumentException, StateException, SystemException, Exception {
		final long timeOut = Framework.getShell().getRack().getQuantum();
		return request(message_, timeOut);
	}

	@Override
	public Future<Response<R>> request(M message_, long timeOut_)
			throws ArgumentException, StateException, SystemException, Exception {
		logger.debug("RequestInputPlug.request: RequestInputPlug [" + _owningHandler.getId() + "." + getId() 
				+ "], message ["+ message_.getClass().getSimpleName() +"], timeout (ms) [" + timeOut_ + "]");
		return Dispatcher.request(this, message_, timeOut_);
	}

	@Override
	public RequestHandler<R, M, S> getOwningHandler() {
		return _owningHandler;
	}

	private static final Logger logger = Logger.getLogger(RequestInputPlug.class);
	
	private final RequestHandler<R,M,S> _owningHandler;
}
