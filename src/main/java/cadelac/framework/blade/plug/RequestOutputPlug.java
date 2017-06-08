package cadelac.framework.blade.plug;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.invocation.UnderstandsRequest;

public interface RequestOutputPlug<R,M extends Message, S extends State> 
		extends OutputPlug, UnderstandsRequest<R,M>{
	public RequestInputPlug<R,M,S> getConnectedInputPlug();
	public void setConnectedPlug(final RequestInputPlug<R,M,S> inputPlug_);
}
