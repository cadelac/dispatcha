package cadelac.framework.blade.core.wire;

import cadelac.framework.blade.handler.Handler;
import cadelac.framework.blade.plug.OutputPlug;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.plug.InputPlug;

public class Wire {	


	public static <M extends Message, S extends State> 
	void wire(final Handler<M,S> dest_, final Handler<?,?> src_, final String outputPlugName_) 
			throws ArgumentException {
		final InputPlug ip = dest_.getInputPlug();
		final OutputPlug op = src_.getOutputPlug(outputPlugName_);
		op.setInputPlug(ip);
	}
	
	public static
	void wire(final OutputPlug origin_, final InputPlug dest_) {
		origin_.setInputPlug(dest_);
	}
}
