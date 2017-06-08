package cadelac.framework.blade.pack;

import cadelac.framework.blade.core.component.Component;
import cadelac.framework.blade.plug.RequestInputPlug;
import cadelac.framework.blade.plug.RequestOutputPlug;
import cadelac.framework.blade.plug.SendInputPlug;
import cadelac.framework.blade.plug.SendOutputPlug;
import cadelac.lib.primitive.concept.Containable;
import cadelac.lib.primitive.concept.Container;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.exception.ArgumentException;

public interface Pack extends Component, Containable, Container {

	// retrieve plugs by name
	public <M extends Message,S extends State> 
	SendInputPlug<M,S> getSendInputPlug(final String plugName_) 
			throws ArgumentException;
	public <M extends Message,S extends State> 
	SendOutputPlug<M,S> getSendOutputPlug(final String plugName_) 
			throws ArgumentException;
	public <R,M extends Message,S extends State> 
	RequestInputPlug<R,M,S> getRequestInputPlug(final String plugName_) 
			throws ArgumentException;
	public <R,M extends Message,S extends State> 
	RequestOutputPlug<R,M,S> getRequestOutputPlug(final String plugName_) 
			throws ArgumentException;
	
	
	// expose input plugs
	//
	public <M extends Message,S extends State> 
	SendInputPlug<M,S> exposeSendInputPlug(final SendInputPlug<M,S> inputPlug_) 
			throws ArgumentException;
	
	public <M extends Message,S extends State> 
	SendInputPlug<M,S> exposeSendInputPlug(final String inputPlugName_, final SendInputPlug<M,S> inputPlug_) 
			throws ArgumentException;
	
	public <R,M extends Message,S extends State> 
	RequestInputPlug<R,M,S> exposeRequestInputPlug(final RequestInputPlug<R,M,S> inputPlug_) 
			throws ArgumentException;
	
	public <R,M extends Message,S extends State> 
	RequestInputPlug<R,M,S> exposeRequestInputPlug(final String inputPlugName_, final RequestInputPlug<R,M,S> inputPlug_) 
			throws ArgumentException;
	

	
	// expose output plugs
	//
	public <M extends Message,S extends State> 
	SendOutputPlug<M,S> exposeSendOutputPlug(final SendOutputPlug<M,S> outputPlug_) 
			throws ArgumentException;
	
	public <M extends Message,S extends State> 
	SendOutputPlug<M,S> exposeSendOutputPlug(final String outputPlugName_, final SendOutputPlug<M,S> outputPlug_) 
			throws ArgumentException;
	
	public <R,M extends Message,S extends State> 
	RequestOutputPlug<R,M,S> exposeRequestOutputPlug(final RequestOutputPlug<R,M,S> outputPlug_) 
			throws ArgumentException;
	
	public <R,M extends Message,S extends State> 
	RequestOutputPlug<R,M,S> exposeRequestOutputPlug(final String outputPlugName_, final RequestOutputPlug<R,M,S> outputPlug_) 
			throws ArgumentException;
}
