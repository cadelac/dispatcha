package cadelac.framework.blade.plug;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.invocation.UnderstandsSend;

public interface SendOutputPlug<M extends Message, S extends State> 
		extends OutputPlug, UnderstandsSend<M> {
	public SendInputPlug<M,S> getConnectedInputPlug();
	public void setConnectedInputPlug(final SendInputPlug<M,S> inputPlug_);
}
