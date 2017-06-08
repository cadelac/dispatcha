package cadelac.framework.blade.pack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.Framework;
import cadelac.framework.blade.core.component.Component;
import cadelac.framework.blade.core.component.ComponentBase;
import cadelac.framework.blade.core.service.dispatch.Dispatcher;
import cadelac.framework.blade.handler.Handler;
import cadelac.framework.blade.handler.RequestHandler;
import cadelac.framework.blade.handler.SendHandler;
import cadelac.framework.blade.plug.OutputPlug;
import cadelac.framework.blade.plug.RequestInputPlug;
import cadelac.framework.blade.plug.RequestOutputPlug;
import cadelac.framework.blade.plug.SendInputPlug;
import cadelac.framework.blade.plug.SendOutputPlug;
import cadelac.framework.blade.plug.SendOutputPlugBase;
import cadelac.lib.primitive.concept.Containable;
import cadelac.lib.primitive.concept.Container;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.plug.InputPlug;

public class PackBase extends ComponentBase implements Pack {

	public PackBase(final String id_) {
		this(id_, null);
	}

	public PackBase(final String id_, final Pack container_) {
		super(id_, Framework.getShell());
		_container = container_;
		_containables = new HashMap<String,Containable>();
		_inputPlugs = new HashMap<String,InputPlug>();
		_outputPlugs = new HashMap<String,OutputPlug>();
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
	public Containable getContainable(final String containableId_)
			throws ArgumentException {
		if (containableId_ == null)
			throw new ArgumentException("containable id must not be null");
		return _containables.get(containableId_);
	}

	@Override
	public void addContainable(final Containable containable_)
			throws ArgumentException {
		if (containable_ == null)
			throw new ArgumentException("containable must not be null");
		final String containableId = containable_.getId();
		if (containableId == null || containableId.isEmpty())
			throw new ArgumentException("containable id must not be null or empty");
		if (_containables.containsKey(containableId))
			throw new ArgumentException("containable [" + containableId + "] already exists");
		_containables.put(containableId, containable_);
		containable_.setContainer(this);
	}

	@Override
	public Collection<Containable> getAllContainables() {
		return _containables.values();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends Message, S extends State> 
	SendInputPlug<M, S> getSendInputPlug(final String plugName_) throws ArgumentException {
		if (plugName_ == null)
			throw new ArgumentException("plug name must not be null");
		return (SendInputPlug<M,S>) _inputPlugs.get(plugName_);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends Message, S extends State> 
	SendOutputPlug<M, S> getSendOutputPlug(final String plugName_) throws ArgumentException {
		if (plugName_ == null)
			throw new ArgumentException("plug name must not be null");
		return (SendOutputPlug<M,S>) _outputPlugs.get(plugName_);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R, M extends Message, S extends State> 
	RequestInputPlug<R, M, S> getRequestInputPlug(final String plugName_) throws ArgumentException {
		if (plugName_ == null)
			throw new ArgumentException("plug name must not be null");
		return (RequestInputPlug<R,M,S>) _inputPlugs.get(plugName_);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R, M extends Message, S extends State> 
	RequestOutputPlug<R, M, S> getRequestOutputPlug(final String plugName_) throws ArgumentException {
		if (plugName_ == null)
			throw new ArgumentException("plug name must not be null");
		return (RequestOutputPlug<R,M,S>) _outputPlugs.get(plugName_);
	}

	
	// expose input plugs
	//
	@SuppressWarnings("unchecked")
	@Override
	public <M extends Message, S extends State> 
	SendInputPlug<M, S> exposeSendInputPlug(final SendInputPlug<M, S> inputPlug_) 
			throws ArgumentException {
		final Handler<?,?> owningHandler = inputPlug_.getOwningHandler();
		return (SendInputPlug<M, S>) exposeInputPlug(owningHandler.getId()+"."+inputPlug_.getId(), inputPlug_, owningHandler);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends Message, S extends State> 
	SendInputPlug<M, S> exposeSendInputPlug(final String inputPlugName_, final SendInputPlug<M, S> inputPlug_)
			throws ArgumentException {
		return (SendInputPlug<M, S>) exposeInputPlug(inputPlugName_, inputPlug_, inputPlug_.getOwningHandler());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R, M extends Message, S extends State> 
	RequestInputPlug<R, M, S> exposeRequestInputPlug(final RequestInputPlug<R, M, S> inputPlug_) 
			throws ArgumentException {
		final Handler<?,?> owningHandler = inputPlug_.getOwningHandler();
		return (RequestInputPlug<R,M,S>) exposeInputPlug(owningHandler.getId()+"."+inputPlug_.getId(), inputPlug_, owningHandler);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R, M extends Message, S extends State> 
	RequestInputPlug<R, M, S> exposeRequestInputPlug(final String inputPlugName_, final RequestInputPlug<R, M, S> inputPlug_)
			throws ArgumentException {
		return (RequestInputPlug<R,M,S>) exposeInputPlug(inputPlugName_, inputPlug_, inputPlug_.getOwningHandler());
	}

	private InputPlug exposeInputPlug(final String plugName_, final InputPlug inputPlug_, final Handler<?,?> owningHandler_) 
			throws ArgumentException {
		if (plugName_==null)
			throw new ArgumentException("plug id must not be null");
		if (_inputPlugs.containsKey(plugName_))
			throw new ArgumentException("plug [" + plugName_ + "] already exists");
		if (inputPlug_ == null)
			throw new ArgumentException("input plug must not be null");

		// make sure that the Containable the InputPlug belongs to is contained within this pack
		Pack pack = (Pack) owningHandler_.getContainer();
		while (pack != null && pack != this)
			pack = (Pack) pack.getContainer();	
		final String handlerId = owningHandler_.getId();
		if (pack == null)
			throw new ArgumentException("cannot expose InputPlug as [" + getId() + "." + plugName_ 
					+ "]: Handler [" + handlerId + "] is not contained within this pack [" + getId() + "]");

		_inputPlugs.put(plugName_, inputPlug_);
		
		logger.debug("InputPlug [" + handlerId + "." + inputPlug_.getId() + "] has been exposed as [" + getId() + "." + plugName_ + "]");
		return inputPlug_;		
	}
	
	
	// expose output plugs
	//
	@SuppressWarnings("unchecked")
	@Override
	public <M extends Message, S extends State> 
	SendOutputPlug<M, S> exposeSendOutputPlug(final SendOutputPlug<M, S> outputPlug_) 
			throws ArgumentException {
		final Component owner = outputPlug_.getOwner();
		return (SendOutputPlug<M, S>) exposeOutputPlug(owner.getId()+"."+outputPlug_.getId(), outputPlug_, owner);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <M extends Message, S extends State> 
	SendOutputPlug<M, S> exposeSendOutputPlug(final String outputPlugName_, final SendOutputPlug<M, S> outputPlug_)
			throws ArgumentException {
		return (SendOutputPlug<M, S>) exposeOutputPlug(outputPlugName_, outputPlug_, outputPlug_.getOwner());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R, M extends Message, S extends State> 
	RequestOutputPlug<R, M, S> exposeRequestOutputPlug(final RequestOutputPlug<R, M, S> outputPlug_) 
			throws ArgumentException {
		final Component owner = outputPlug_.getOwner();
		return (RequestOutputPlug<R,M,S>) exposeOutputPlug(owner.getId()+"."+outputPlug_.getId(), outputPlug_, owner);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R, M extends Message, S extends State> 
	RequestOutputPlug<R, M, S> exposeRequestOutputPlug(final String outputPlugName_, final RequestOutputPlug<R, M, S> outputPlug_)
			throws ArgumentException {
		return (RequestOutputPlug<R,M,S>) exposeOutputPlug(outputPlugName_, outputPlug_, outputPlug_.getOwner());
	}

	/**
	 * creates a send output plug for the pack and exposes it
	 * @param outputPlugId_
	 * @return
	 * @throws ArgumentException
	 */
	public <M extends Message,S extends State> SendOutputPlug<M,S> exposeOutputPlug(final String outputPlugId_) throws ArgumentException {
		if (outputPlugId_ == null)
			throw new ArgumentException("plug id must not be null");
		// can't utilize method exposeOutputPlug() below because it assumes that the output plug is contained inside a handler 
		if (_outputPlugs.containsKey(outputPlugId_))
			throw new ArgumentException("plug [" + outputPlugId_ + "] already exists");
		final SendOutputPlug<M,S> outputPlug = new SendOutputPlugBase<M,S>(outputPlugId_, this);
		logger.debug("created pack SendOutputPlug [" + outputPlugId_ + "]");
		_outputPlugs.put(outputPlugId_, outputPlug);
		logger.debug("exposed pack SendOutputPlug [" + outputPlugId_ + "] as [" + getId() + "." + outputPlugId_ + "]");
		return outputPlug;
	}
	
	
	protected  OutputPlug exposeOutputPlug(final String outputPlugName_, final OutputPlug outputPlug_, final Component owner_) 
			throws ArgumentException {
		if (outputPlugName_ == null)
			throw new ArgumentException("plug id must not be null");
		if (_outputPlugs.containsKey(outputPlugName_))
			throw new ArgumentException("plug [" + outputPlugName_ + "] already exists");
		if (outputPlug_ == null)
			throw new ArgumentException("output plug must not be null");
		
		// make sure that the Containable the InputPlug belongs to is contained within this pack
		String handlerId = "orphan";
		if (owner_ != null && owner_ instanceof Containable) { // not orphan plug
			Pack pack = (Pack) ((Containable)owner_).getContainer();
			while (pack != null && pack != this)
				pack = (Pack) pack.getContainer();	
			handlerId = owner_.getId();
			if (pack == null)
				throw new ArgumentException("cannot expose OutputPlug as [" + getId() + "." + outputPlugName_ 
						+ "]: Handler [" + handlerId + "] is not contained within this pack [" + getId() + "]");
		}
		_outputPlugs.put(outputPlugName_, outputPlug_);
		logger.debug("exposed OutputPlug [" + handlerId + "." + outputPlug_.getId() + "] as [" + getId() + "." + outputPlugName_ + "]");
		return outputPlug_;
	}
	
	protected static <M extends Message, S extends State>
	void addSendHandlerForType(final Class<? extends Message> protoType_, final SendHandler<M,S> sendHandler_) 
			throws Exception {
		Dispatcher.addSendHandlerForType(protoType_, sendHandler_);
	}
	
	protected static <R, M extends Message, S extends State>
	void addRequestHandlerForType(final Class<? extends Message> protoType_, final RequestHandler<R,M,S> requestHandler_) 
			throws Exception {
		Dispatcher.addRequestHandlerForType(protoType_, requestHandler_);
	}
	
	
	protected <R, M extends Message, S extends State>
	RequestHandler<R,M,S> addRequestHandler(final Class<? extends RequestHandler<R,M,S>> handlerClass_) 
			throws InstantiationException, IllegalAccessException, ArgumentException {
		final RequestHandler<R,M,S> handler = (RequestHandler<R,M,S>) handlerClass_.newInstance();
		this.addContainable(handler);
		return handler;
	}
	
	protected <M extends Message, S extends State>
	SendHandler<M,S> addSendHandler(final Class<? extends SendHandler<M,S>> handlerClass_) 
			throws InstantiationException, IllegalAccessException, ArgumentException {
		final SendHandler<M,S> handler = (SendHandler<M,S>) handlerClass_.newInstance();
		this.addContainable(handler);
		return handler;
	}
	
	private static final Logger logger = Logger.getLogger(Pack.class);
	
	private /* not final */ Container _container; // may be reset
	private final Map<String,Containable> _containables;
	private final Map<String,InputPlug> _inputPlugs;
	private final Map<String,OutputPlug> _outputPlugs;
}
