package cadelac.framework.blade.plug;

import cadelac.framework.blade.handler.SendHandler;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.invocation.UnderstandsSend;
import cadelac.lib.primitive.plug.InputPlug;

public interface SendInputPlug<M extends Message, S extends State> 
		extends InputPlug, UnderstandsSend<M> {
	public SendHandler<M,S> getOwningHandler();
}
