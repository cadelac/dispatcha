package cadelac.framework.blade.core;

import java.util.concurrent.Future;

import cadelac.framework.blade.core.service.dispatch.Dispatcher;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.exception.StateException;
import cadelac.lib.primitive.exception.SystemException;
import cadelac.lib.primitive.invocation.Response;


public class Helper {
	
	public static <M extends Message, S extends State> 
	void routeMessage(final M message_) throws ArgumentException, StateException, SystemException, Exception {
		Dispatcher.routeMessage(message_);
	}

	public static <R, M extends Message, S extends State>
	Future<Response<R>> routeRequest(final M message_) throws ArgumentException, StateException, SystemException, Exception  {
		return Dispatcher.routeRequest(message_);
	}

}
