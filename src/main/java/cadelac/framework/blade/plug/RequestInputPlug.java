package cadelac.framework.blade.plug;

import cadelac.framework.blade.handler.RequestHandler;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.invocation.UnderstandsRequest;
import cadelac.lib.primitive.plug.InputPlug;

public interface RequestInputPlug<R,M extends Message,S extends State> 
		extends InputPlug, UnderstandsRequest<R,M>{
	public RequestHandler<R,M,S> getOwningHandler();
}
