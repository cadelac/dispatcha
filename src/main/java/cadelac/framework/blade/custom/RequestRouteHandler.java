package cadelac.framework.blade.custom;

import java.util.HashMap;

import org.apache.log4j.Logger;

import cadelac.framework.blade.handler.RequestHandlerBase;
import cadelac.framework.blade.plug.OutputPlug;
import cadelac.framework.blade.plug.RequestOutputPlug;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.StateLess;
import cadelac.lib.primitive.exception.StateException;
import cadelac.lib.primitive.invocation.Response;


/**
 * Routes request message to appropriate RequestHandler.
 * @author cadelac
 *
 */
public class RequestRouteHandler extends RequestHandlerBase<Response<?>,Message,StateLess> {

	public RequestRouteHandler(final String handlerName_) {
		super(handlerName_);
		_outputPlugsByType = new HashMap<Class<?>,OutputPlug>();
	}


	@Override
	public Response<Response<?>> process(final Message msg_, final StateLess state_) throws Exception {
		Class<?> classType = msg_.getClass();
		if (_outputPlugsByType.containsKey(classType)) {
			@SuppressWarnings("unchecked")
			RequestOutputPlug<Response<?>,Message,?> outputPlug = (RequestOutputPlug<Response<?>,Message, ?>) _outputPlugsByType.get(classType);
			try {
				final Response<?> response = (Response<?>) outputPlug.request(msg_).get();
				return wrapResponse(response);
			}
			catch (StateException e_) {
			}
		}
		else {
			logger.warn(String.format("unable to route message [%s]", msg_.toString()));
		}
		final Response<?> response = null;
		return wrapResponse(response);
	}
	

	@Override
	public String getStateId(final Message message) {
		return StateLess.STATELESS_STATE_ID;
	}

	@Override
	public StateLess createState(final Message message_) throws Exception {
		return StateLess.STATELESS_STATE;
	}
	

	@SuppressWarnings("unchecked")
	public <N extends Message> RequestOutputPlug<?,N,?> getRequestOutputPlugForClass(Class<? extends Message> protoType_) {
		return (RequestOutputPlug<?,N,?>) super.getOutputPlugForClass(protoType_);
	}
	
	public <N extends Message> void addRequestOutputPlugForClass(Class<? extends Message> protoType_, final RequestOutputPlug<?,N,?> outputPlug_) 
			throws Exception {
		super.addOutputPlugForClass(protoType_, outputPlug_);
	}

	
	private static final Logger logger = Logger.getLogger(RequestRouteHandler.class);
}
