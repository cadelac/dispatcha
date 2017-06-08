package cadelac.framework.blade.handler;

import java.util.Collection;

import cadelac.framework.blade.core.component.Component;
import cadelac.framework.blade.plug.OutputPlug;
import cadelac.lib.primitive.concept.Containable;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.concept.state.StateAware;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.plug.InputPlug;

public interface Handler<M extends Message, S extends State> 
		extends Component, Containable, StateAware<M,S> {
	public InputPlug getInputPlug();
	public OutputPlug getOutputPlug(final String outputPlugName_) throws ArgumentException;
	public Collection<OutputPlug> getOutputPlugs();
}
