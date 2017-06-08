package cadelac.framework.blade.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.Framework;
import cadelac.framework.blade.core.component.ComponentBase;
import cadelac.framework.blade.plug.OutputPlug;
import cadelac.lib.primitive.concept.Container;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.object.Prototype2ConcreteMap;
import cadelac.lib.primitive.plug.InputPlug;

public abstract class HandlerBase<M extends Message, S extends State> extends ComponentBase implements Handler<M,S> {

	public HandlerBase(final String id_) {
		super(id_, Framework.getShell());
		// lazy initialization of output plug maps; not all handlers have output plugs
		_outputPlugs = null;
		_outputPlugsByType = null;
		logger.debug("created handler [" + getId() + "]");
	}
	
	@Override
	public Container getContainer() {
		return _container;
	}
	
	@Override
	public void setContainer(final Container container_) throws ArgumentException {
		_container = container_;
	}
	
	@Override
	public InputPlug getInputPlug() {
		return _inputPlug;
	}

	@Override
	public OutputPlug getOutputPlug(final String outputPlugName_) throws ArgumentException {
		if (outputPlugName_==null)
			throw new ArgumentException("output plug name must not be null");
		if (_outputPlugs==null)
			return null;
		return _outputPlugs.get(outputPlugName_);
	}
	
	@Override
	public Collection<OutputPlug> getOutputPlugs() {
		final List<OutputPlug> plugs = new ArrayList<OutputPlug>();
		if (_outputPlugs!=null) {
			for (OutputPlug ap : _outputPlugs.values()) {
				plugs.add(ap);
			}
		}
		return plugs;
	}

//	@Override
	protected void addOutputPlug(final OutputPlug outputPlug_) throws ArgumentException {
		addOutputPlug(outputPlug_.getId(), outputPlug_);
	}
	
//	@Override
	protected void addOutputPlug(final String outputPlugName_, final OutputPlug outputPlug_)
			throws ArgumentException {
		if (outputPlugName_ == null)
			throw new ArgumentException("name of output plug to create must not be null");
		if (_outputPlugs==null) // map of output plugs is needed -- create map 
			_outputPlugs = new HashMap<String,OutputPlug>();
		if (_outputPlugs.containsKey(outputPlugName_))
			throw new ArgumentException("output plug [" + outputPlugName_ + "] was not created; exists already");
		_outputPlugs.put(outputPlugName_, outputPlug_);
		logger.debug("added OutputPlug [" + getId() + "." + outputPlug_.getId() + "]");		
	}
	
	
	protected OutputPlug getOutputPlugForClass(Class<? extends Message> protoType_) {
		final Prototype2ConcreteMap p2c = getShell().getRack().getPrototype2ConcreteMap();
		Class<? extends Message> concreteClass = p2c.get(protoType_);
		return _outputPlugsByType.get(concreteClass);
	}
	
	protected void addOutputPlugForClass(Class<? extends Message> protoType_, final OutputPlug outputPlug_) 
			throws Exception {
		final Prototype2ConcreteMap p2c = getShell().getRack().getPrototype2ConcreteMap();
		Class<? extends Message> concreteClass = p2c.get(protoType_);
		if (concreteClass == null) {
			Framework.getShell().registerClass(protoType_);
			// try again
			concreteClass = p2c.get(protoType_);
		}
		if (!_outputPlugsByType.containsKey(concreteClass))
			_outputPlugsByType.put(concreteClass, outputPlug_);
		else
			logger.warn(
					String.format("handler [%s] outputPlug [%s] for type [%s] not added: already exists",
							getId(), outputPlug_.getId(), protoType_.getSimpleName()));
	}

//	@Override
	protected void setInputPlug(final InputPlug inputPlug_) {
		_inputPlug = inputPlug_;
	}
	
	
	
	private static final Logger logger = Logger.getLogger(HandlerBase.class);

	protected /* not final */ Map<String,OutputPlug> _outputPlugs;
	protected /* not final */ Map<Class<?>,OutputPlug> _outputPlugsByType;
	
	private /* not final */ InputPlug _inputPlug;
	private Container _container; /* not final since this is populated after construction */
}
