package cadelac.framework.blade.core.service.dispatch;

import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.Framework;
import cadelac.framework.blade.core.component.rack.Rack;
import cadelac.framework.blade.core.component.shell.Shell;
import cadelac.framework.blade.core.component.shell.ShellSimple;
import cadelac.framework.blade.core.invocation.job.RequestJob;
import cadelac.framework.blade.core.invocation.job.RequestJobBase;
import cadelac.framework.blade.core.invocation.job.SendJob;
import cadelac.framework.blade.core.invocation.job.SendJobBase;
import cadelac.framework.blade.core.service.object.MessageMap;
import cadelac.framework.blade.handler.RequestHandler;
import cadelac.framework.blade.handler.SendHandler;
import cadelac.framework.blade.handler2.Executor;
import cadelac.framework.blade.plug.RequestInputPlug;
import cadelac.framework.blade.plug.SendInputPlug;
import cadelac.lib.primitive.Provider;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.concept.state.StateManager;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.exception.FrameworkException;
import cadelac.lib.primitive.exception.InitializationException;
import cadelac.lib.primitive.exception.StateException;
import cadelac.lib.primitive.exception.SystemException;
import cadelac.lib.primitive.invocation.Response;
import cadelac.lib.primitive.monitor.Monitor;
import cadelac.lib.primitive.object.Prototype2ConcreteMap;
import cadelac.lib.primitive.plug.Plug;



public class Dispatcher {


	public static void init() {
		if (!_isInitialized) {
			_isInitialized = true;
			_shell = Framework.getShell();
			_rack = _shell.getRack();
			_monitor = _rack.getMonitor();
			_typedSendHandlerProvider = new Provider<Class<?>,SendHandler<?,?>>();
			_typedRequestHandlerProvider = new Provider<Class<?>,RequestHandler<?,?,?>>();
			Executor.init(getThreadPoolSize());
		}
	}

	
	// implements routing at dispatcher-level
	public static <M extends Message, S extends State>
	void addSendHandlerForType(final Class<? extends Message> protoType_, final SendHandler<M,S> sendHandler_) 
			throws Exception {
		final Class<?> concreteClass = MessageMap.getMatchingConcreteClass(protoType_);
		if (_typedSendHandlerProvider.getElement(concreteClass)==null) // not present
			_typedSendHandlerProvider.safeAdd(concreteClass, sendHandler_);
		else
			logger.warn(
					String.format("send handler [%s] for type [%s] not added: already exists",
							sendHandler_.getId(), protoType_.getSimpleName()));	
	}
	// implements routing at dispatcher-level
	public static <R, M extends Message, S extends State>
	void addRequestHandlerForType(final Class<? extends Message> protoType_, final RequestHandler<R,M,S> requestHandler_) 
			throws Exception {
		final Class<?> concreteClass = MessageMap.getMatchingConcreteClass(protoType_);
		if (_typedRequestHandlerProvider.getElement(concreteClass)==null) // not present
			_typedRequestHandlerProvider.safeAdd(concreteClass, requestHandler_);
		else
			logger.warn(
					String.format("request handler [%s] for type [%s] not added: already exists",
							requestHandler_.getId(), protoType_.getSimpleName()));	
	}
	
	// message dispatching
	
	// route-dispatched
	public static <M extends Message, S extends State> 
	void routeMessage(final M message_) 
			throws ArgumentException, StateException, SystemException, Exception {
		logger.debug(String.format("%s.routeMessage: message [%s]",
				Dispatcher.class.getSimpleName(),
				message_.getClass().getSimpleName()));
		final SendHandler<M,S> sendHandler = acquireSendHandler(message_.getClass());
		final SendJob<M,S> sendJob = createSendJob(sendHandler, message_);
		Executor.execute(sendJob);
		//_executorService.execute(sendJob);
	}
	public static <R, M extends Message, S extends State>
	Future<Response<R>> routeRequest(final M message_, final long quantum_) 
			throws ArgumentException, StateException, SystemException, Exception {
		logger.info(String.format("%s.routeRequest: message [%s]",
				Dispatcher.class.getSimpleName(),
				message_.getClass().getSimpleName()));
		final RequestHandler<R,M,S> requestHandler = acquireRequestHandler(message_.getClass());
		final RequestJob<R,M,S> job = createRequestJob(requestHandler, message_, quantum_);
		return Executor.executeRequestJob(job, quantum_);
	}
	public static <R, M extends Message, S extends State>
	Future<Response<R>> routeRequest(final M message_) 
			throws ArgumentException, StateException, SystemException, Exception {
		return routeRequest(message_, _rack.getQuantum());
	}
	
	// plug-dispatched
	public static <M extends Message, S extends State> 
	void send(final SendInputPlug<M,S> inputPlug_, final M message_) 
			throws ArgumentException, StateException, SystemException, Exception {
		logger.debug(String.format("Dispatcher.send: SendInputPlug [%s.%s], message [%s]",
				inputPlug_.getOwningHandler().getId(),
				inputPlug_.getId(),
				message_.getClass().getSimpleName()));
		final SendJob<M,S> sendJob = createSendJob(inputPlug_, message_);
		Executor.execute(sendJob);
		//_executorService.execute(sendJob);
	}
	
	public static <M extends Message, S extends State>
	void delayedCall(final SendInputPlug<M,S> inputPlug_, final M message_, final long delay_) 
			throws ArgumentException, StateException, SystemException, Exception {
		logger.debug(String.format("Dispatcher.delayedCall: SendInputPlug [%s.%s], message [%s]",
				inputPlug_.getOwningHandler().getId(),
				inputPlug_.getId(),
				message_.getClass().getSimpleName()));
		final SendJob<M,S> sendJob = createSendJob(inputPlug_, message_);
		Executor.delayedCall(sendJob, delay_);
	}
	
	public static <M extends Message, S extends State>
	void recurringCall(final SendInputPlug<M,S> inputPlug_, final M message_, final long delay_, final long period_) 
			throws ArgumentException, StateException, SystemException, Exception {
		logger.debug(String.format("Dispatcher.recurringCall: SendInputPlug [%s.%s], message [%s]",
				inputPlug_.getOwningHandler().getId(),
				inputPlug_.getId(),
				message_.getClass().getSimpleName()));
		final SendJob<M,S> sendJob = createSendJob(inputPlug_, message_);
		Executor.recurringCall(sendJob, delay_, period_);
	}
	
	public static <R, M extends Message, S extends State>
	Future<Response<R>> request(final RequestInputPlug<R,M,S> inputPlug_, final M message_, final long quantum_) 
			throws ArgumentException, StateException, SystemException, Exception {
		doSanityChecks(inputPlug_, message_);
		final RequestHandler<R,M,S> handler = acquireRequestHandler(inputPlug_);
		final RequestJob<R,M,S> job = createRequestJob(handler, message_, quantum_);
		return Executor.executeRequestJob(job, quantum_);
	}

	


	// PRIVATE METHODS
	private static <M extends Message, S extends State> 
	SendJob<M,S> createSendJob(final SendInputPlug<M,S> inputPlug_, final M message_) 
			throws ArgumentException, StateException, SystemException, Exception {
		doSanityChecks(inputPlug_, message_);
		final SendHandler<M,S> handler = acquireSendHandler(inputPlug_);
		return createSendJob(handler, message_);
	}

	private static <M extends Message, S extends State> 
	SendJob<M,S> createSendJob(SendHandler<M,S> handler, final M message_) 
			throws ArgumentException, StateException, SystemException, Exception {
		final String stateId = handler.getStateId(message_);
		final S state = StateManager.acquireState(handler, message_, stateId);
		final long jobId = _monitor.getNextJobId();
		final SendJob<M,S> sendJob = new SendJobBase<M,S>(handler, message_, state, jobId);
		return sendJob;
	}	

	private static <R, M extends Message, S extends State>
	RequestJob<R,M,S> createRequestJob(final RequestHandler<R,M,S> handler, final M message_, final long quantum_) throws ArgumentException, StateException, Exception {
		final String stateId = handler.getStateId(message_);
		final S state = StateManager.acquireState(handler, message_, stateId);
		final long jobId = _monitor.getNextJobId();
		final RequestJob<R,M,S> job = new RequestJobBase<R,M,S>(handler, message_, state, jobId, quantum_);
		return job;
	}

	private static <M extends Message, S extends State> 
	SendHandler<M,S> acquireSendHandler(final SendInputPlug<M,S> inputPlug_)
			throws ArgumentException, SystemException {
		if (inputPlug_==null)
			throw new ArgumentException("input plug must not be null");
		final SendHandler<M,S> handler = inputPlug_.getOwningHandler();
		if (handler==null)
			throw new SystemException("input plug must have an owning handler");
		return handler;
	}
	
	@SuppressWarnings("unchecked")
	private static <M extends Message, S extends State> 
	SendHandler<M,S> acquireSendHandler(final Class<?> concreteClass_) 
			throws FrameworkException, Exception {
		if (concreteClass_==null)
			throw new ArgumentException("argument class must not be null");
		final SendHandler<M,S> handler = (SendHandler<M,S>) _typedSendHandlerProvider.getElement(concreteClass_);
		if (handler==null) {
			logger.warn(String.format("SendHandler for class [%s] not found", concreteClass_.getSimpleName()));
			return null;
		}
		return handler;
	}

	private static <R, M extends Message, S extends State>
	RequestHandler<R,M,S> acquireRequestHandler(final RequestInputPlug<R,M,S> inputPlug_)
			throws ArgumentException, SystemException {
		if (inputPlug_==null)
			throw new ArgumentException("input plug must not be null");
		final RequestHandler<R,M,S> handler = inputPlug_.getOwningHandler();
		if (handler==null)
			throw new SystemException("input plug must have an owning handler");
		return handler;
	}
	
	@SuppressWarnings("unchecked")
	private static <R, M extends Message, S extends State> 
	RequestHandler<R,M,S> acquireRequestHandler(final Class<?> concreteClass_) throws FrameworkException, Exception {
		if (concreteClass_==null)
			throw new ArgumentException("argument class must not be null");
		final RequestHandler<R,M,S> handler = (RequestHandler<R,M,S>) _typedRequestHandlerProvider.getElement(concreteClass_);
		if (handler==null) {
			logger.warn(String.format("RequestHandler for class [%s] not found", concreteClass_.getSimpleName()));
			return null;
		}
		return handler;
	}
	
	/*
	@SuppressWarnings("unchecked")
	private static <M extends Message, S extends State>
	S acquireState(final StateAware<M,S> stateAware_, final M message_, final String stateId_) throws
			ArgumentException , StateException , Exception 
	{
		if (stateId_ == null)
			throw new ArgumentException("state id must not be null");
		if (message_==null)
			throw new ArgumentException("message must not be null");
		if (stateAware_==null)
			throw new ArgumentException("handler must not be null");

		S state = (S) _states.get(stateId_);
		if (state != null) {
			// found it, exit immediately
			return state;
		}

		if (stateId_==StateLess.STATELESS_STATE_ID)
			// state is StateLess
			state = (S) StateLess.STATELESS_STATE;
		else
			// state does not exist, create a new state
			state = (S) stateAware_.createState(message_);
		
		// state is not allowed to be null.
		if (state == null)
			throw new StateException("unable to create state [" + stateId_ + "]");
		
		//  install newly-created state
		installState(state);
		return state;
	}
	
	private static
	Class<? extends Message> getMatchingConcreteClass(final Class<? extends Message> protoType_) 
			throws Exception {
		final Prototype2ConcreteMap p2c = _rack.getPrototype2ConcreteMap();
		Class<? extends Message> concreteClass = p2c.get(protoType_);
		if (concreteClass == null) {
			// class not yet registered,
			_shell.registerClass(protoType_); //register now
			concreteClass = p2c.get(protoType_); // try again
		}
		return concreteClass;
	}	
	*/
	
	


	
	private static <M extends Message> 
	void doSanityChecks(final Plug inputPlug_, final M message_) 
			throws ArgumentException, InitializationException {
		if (!_isInitialized)
			throw new InitializationException("Dispatcher is not initialized");
		if (inputPlug_==null)
			throw new ArgumentException("input plug must not be null");
		if (message_==null)
			throw new ArgumentException("message must not be null");
	}
	
	private static int getThreadPoolSize() {
		// initialize thread pool
		int tps = Executor.THREADPOOL_SIZE;
		String threadPoolSize = _rack.getArg().getArgument(ShellSimple.THREAD_POOL_SIZE);
		if (threadPoolSize!=null) {
			tps = Integer.parseInt(threadPoolSize);
		}
		else if ((threadPoolSize=_rack.getPropertiesManager().getProperties().getProperty(ShellSimple.THREAD_POOL_SIZE)) != null) {
			tps = Integer.parseInt(threadPoolSize);
		}
		return tps;
	}
	
	
	private static final Logger logger = Logger.getLogger(Dispatcher.class);
	
	private static boolean _isInitialized = false;
	private static Shell _shell;
	private static Rack _rack;
	private static Monitor _monitor;
	private static Provider<Class<?>,SendHandler<?,?>> _typedSendHandlerProvider;
	private static Provider<Class<?>,RequestHandler<?,?,?>> _typedRequestHandlerProvider;
}
