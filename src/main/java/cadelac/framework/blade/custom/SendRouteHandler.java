package cadelac.framework.blade.custom;

import java.util.HashMap;

import org.apache.log4j.Logger;

import cadelac.framework.blade.handler.SendHandlerBase;
import cadelac.framework.blade.plug.OutputPlug;
import cadelac.framework.blade.plug.SendOutputPlug;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.StateLess;
import cadelac.lib.primitive.exception.StateException;

/**
 * Routes message to appropriate SendHandler. RequestHandlers not supported.
 * @author cadelac
 *
 */
public class SendRouteHandler extends SendHandlerBase<Message,StateLess> {

	public SendRouteHandler(final String handlerName_) {
		super(handlerName_);
		_outputPlugsByType = new HashMap<Class<?>,OutputPlug>();
	}


	@Override
	public void process(final Message msg_, final StateLess state_) throws Exception {
		Class<?> classType = msg_.getClass();
		if (_outputPlugsByType.containsKey(classType)) {
			@SuppressWarnings("unchecked")
			SendOutputPlug<Message,?> outputPlug = (SendOutputPlug<Message,?>) _outputPlugsByType.get(classType);
			try {
				outputPlug.send(msg_);
			}
			catch (StateException e_) {
			}
		}
		else {
			logger.warn(String.format("unable to route message [%s]", msg_.toString()));
		}
	}
	

	@Override
	public String getStateId(Message message) {
		return StateLess.STATELESS_STATE_ID;
	}

	@Override
	public StateLess createState(Message message_) throws Exception {
		return StateLess.STATELESS_STATE;
	}

	@SuppressWarnings("unchecked")
	public <N extends Message> SendOutputPlug<N,?> getSendOutputPlugForClass(Class<? extends Message> protoType_) {
		return (SendOutputPlug<N, ?>) super.getOutputPlugForClass(protoType_);
	}
	
	public <N extends Message> void addSendOutputPlugForClass(Class<? extends Message> protoType_, final SendOutputPlug<N,?> outputPlug_) 
			throws Exception {
		super.addOutputPlugForClass(protoType_, outputPlug_);
	}
	

	
	private static final Logger logger = Logger.getLogger(SendRouteHandler.class);
}
