package cadelac.framework.blade.handler;

import cadelac.framework.blade.plug.SendInputPlug;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;

public interface SendHandler<M extends Message, S extends State> extends Handler<M,S> {
	public SendInputPlug<M,S> getInputPlug();
	public void invoke(final M message_, final S state_) throws Exception;
	public void process(final M message_, final S state_) throws Exception;
}
