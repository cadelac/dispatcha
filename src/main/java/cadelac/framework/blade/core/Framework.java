package cadelac.framework.blade.core;

import java.util.concurrent.Future;

import cadelac.framework.blade.core.component.shell.Shell;
import cadelac.framework.blade.custom.RequestRouteHandler;
import cadelac.framework.blade.custom.SendRouteHandler;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.exception.StateException;
import cadelac.lib.primitive.exception.SystemException;
import cadelac.lib.primitive.invocation.Response;
import cadelac.lib.primitive.object.ObjectFactory;

public class Framework {

	public static Shell getShell() {
		return _shell;
	}
	
	public static void setShell(final Shell shell_) {
		_shell = shell_;
	}

	public static ObjectFactory getObjectFactory() {
		return _objectFactory;
	}

	public static void setObjectFactory(ObjectFactory objectFactory_) {
		_objectFactory = objectFactory_;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getApplication() {
		return (T) Framework.getShell().getApplication(); 
	}
	
	public static SendRouteHandler getSendRouteHandler() {
		return _sendRouteHandler;
	}
	public static void setSendRouteHandler(final SendRouteHandler sendRouteHandler_) {
		_sendRouteHandler = sendRouteHandler_;
	}
	
	public static RequestRouteHandler getRequestRouteHandler() {
		return _requestRouteHandler;
	}
	public static void setRequestRouteHandler(final RequestRouteHandler requestRouteHandler_) {
		_requestRouteHandler = requestRouteHandler_;
	}
	
	public static void send(final Message message_) 
			throws ArgumentException, StateException, SystemException, Exception {
		_sendRouteHandler.getInputPlug().send(message_);
	}
	
	public static void delayedCall(final Message message_, final long delay_) 
			throws ArgumentException, StateException, SystemException, Exception {
		_sendRouteHandler.getInputPlug().delayedCall(message_, delay_);
	}
	
	public static void recurringCall(final Message message_, final long delay_, final long period_) 
			throws ArgumentException, StateException, SystemException, Exception {
		_sendRouteHandler.getInputPlug().recurringCall(message_, delay_, period_);
	}

	
	public static Response<?> request(final Message message_) throws ArgumentException, StateException, SystemException, Exception {
		final Future<Response<Response<?>>> future = _requestRouteHandler.getInputPlug().request(message_);
		final Response<?> response = future.get().getResponse();
		return response;
	}
	
	private static Shell _shell;
	private static ObjectFactory _objectFactory;
	
	private static SendRouteHandler _sendRouteHandler;
	private static RequestRouteHandler _requestRouteHandler;
}
