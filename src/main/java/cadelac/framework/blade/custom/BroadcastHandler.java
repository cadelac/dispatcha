package cadelac.framework.blade.custom;

import cadelac.framework.blade.handler.SendHandlerBase;
import cadelac.framework.blade.plug.OutputPlug;
import cadelac.framework.blade.plug.SendOutputPlug;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.StateLess;

public class BroadcastHandler extends SendHandlerBase<Message,StateLess>{

	public BroadcastHandler(final String handlerName_) {
		super(handlerName_);
	}

	@Override
	public void process(final Message msg_, final StateLess state_) throws Exception {
		for (OutputPlug outputPlug : getOutputPlugs()) {
			// we can broadcast only to SendHandlers
			@SuppressWarnings("unchecked")
			SendOutputPlug<Message,?> op = (SendOutputPlug<Message,?>) outputPlug;
			op.send(msg_);
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

}
