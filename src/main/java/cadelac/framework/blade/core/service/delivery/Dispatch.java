package cadelac.framework.blade.core.service.delivery;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.Framework;
import cadelac.framework.blade.core.service.object.MessageMap;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.concept.state.StateAware;
import cadelac.lib.primitive.concept.state.StateLess;
import cadelac.lib.primitive.concept.state.StateManager;
import cadelac.lib.primitive.delivery.pull.Pull;
import cadelac.lib.primitive.delivery.push.Push;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.exception.FrameworkException;
import cadelac.lib.primitive.exception.InitializationException;
import cadelac.lib.primitive.exception.RouteException;
import cadelac.lib.primitive.invocation.Response;
import cadelac.lib.primitive.invocation.ResponseSimple;

public class Dispatch {

	
	
	public static <M extends Message, S extends State> void bind(
			final Class<M> protoType_, Push<M,S> proposedPush_) 
					throws Exception {
		pushInternal.checkedPut(protoType_, proposedPush_);
	}
	
	

	public static <R, M extends Message, S extends State> void bind(
			final Class<M> protoType_, Pull<R,M,S> proposedPull_) 
					throws Exception {
		pullInternal._internal.checkedPut(protoType_, proposedPull_);
	}	
	
	
	
	public static <M extends Message, S extends State> void push(final M msg_) 
			throws Exception {
		@SuppressWarnings("unchecked")
		final StateAware<M,S> stateAware = (StateAware<M, S>) pushInternal.get(msg_.getClass());
		push(stateAware, msg_);	
	}

	
	public static <M extends Message, S extends State> void push(StateAware<M,S> stateAware_, final M msg_) 
			throws Exception {

		final CallFrame<M,S> callFrame = populateCallFrame(
				new CallFrame<M,S>()
				, stateAware_
				, msg_);

		EXECUTION_SERVICE.execute(() -> {
			pushInternal.runnableBody(
					callFrame
					, msg_);
		});		
	}
	
	
	public static <R, M extends Message, S extends State> Future<Response<R>> pull(final M msg_) 
			throws Exception {
		final Class<? extends Message> mc = msg_.getClass();
		@SuppressWarnings("unchecked")
		final StateAware<M,S> stateAware = (StateAware<M, S>) pullInternal._internal.get(mc);
		return pull(stateAware, msg_);
	}	
	
	
	public static <R, M extends Message, S extends State> Future<Response<R>> pull(final StateAware<M,S> stateAware_, final M msg_) 
			throws Exception {
		final CallFrame<M,S> callFrame = populateCallFrame(
						new CallFrame<M,S>()
						, stateAware_
						, msg_);

		return executePull(
				() -> PullInternal.callableBody(callFrame, msg_)
		);
	}
	
	public static <R, M extends Message, S extends State> Future<Response<R>> 
	executePull(Callable<Response<R>> callable) throws Exception {
		final List<Future<Response<R>>> futures = EXECUTION_SERVICE.invokeAll(
				Arrays.asList(callable), 
				Framework.getShell().getRack().getQuantum(), 
				TimeUnit.MILLISECONDS);
		return futures.get(0);		
	}
	
	private static final Logger logger = Logger.getLogger(Dispatch.class);
	
	private static final ExecutorService EXECUTION_SERVICE = Executors.newFixedThreadPool(8);


	
	
	public static class CallFrame<M extends Message, S extends State> {
		public StateAware<M,S> stateAware;
		public String stateId;
		public S state;
		public long jobId;
	}	

	
	
	public static <M extends Message, S extends State> CallFrame<M,S> populateCallFrame(
			final CallFrame<M,S> callFrame_, StateAware<M,S> stateAware_, final M msg_) throws Exception {
		
		if (msg_ == null) {
			final String diag = String.format("message is null: invalid argument");
			logger.warn(diag);
			throw new ArgumentException(diag);			
		}
		if (stateAware_==null) {
			final String errorMsg = String.format("Message %s not bound to an agent: call Dispatch.bind", msg_.getClass().getSimpleName());
			logger.error(errorMsg);
			throw new InitializationException(errorMsg);
		}
		callFrame_.stateAware = stateAware_;
		callFrame_.stateId = callFrame_.stateAware.getStateId(msg_);
		callFrame_.state = StateManager.acquireState(callFrame_.stateAware, msg_, callFrame_.stateId); //getState(callFrame_.stateAware, msg_, callFrame_.stateId);	
		callFrame_.jobId = Framework.getShell().getRack().getMonitor().getNextJobId();
		return callFrame_;
	}
	
	// Used to encapsulate internal logic
	private static class Internal {
		
		public Internal() {
			_provider = new HashMap<Class<? extends Message>,StateAware<? extends Message,? extends State>>();
		}
		
		public <M extends Message, S extends State> void checkedPut(final Class<M> protoType_, final StateAware<M,S> proposedStateAware_) throws Exception {
			
			final Class<? extends Message> concreteClass = MessageMap.getMatchingConcreteClass(protoType_);
			@SuppressWarnings("unchecked")
			final StateAware<M,S> entrenchedStateAware = (StateAware<M,S>) _provider.get(concreteClass);
			if (entrenchedStateAware != null) {
				final String diag = String.format("Message [%s] not bound: already bound", 
						// "Message [%s] not bound to push handler [%s]: already bound to [%s]", 
						protoType_.getSimpleName()); //, proposedStateAware_.getId(), entrenchedStateAware.getId());
				logger.warn(diag);
				throw new RouteException(diag);
			}	
			_provider.put(concreteClass, proposedStateAware_);	
		}
		
		@SuppressWarnings("unchecked")
		public <M extends Message> StateAware<M,? extends State> get(final Class<M> msg_) {
			return (StateAware<M,? extends State>) _provider.get(msg_);
		}

		private final Map<Class<? extends Message>,StateAware<? extends Message,? extends State>> _provider;
	}
	
	
	
	private static class PushInternal extends Internal {
		
		public <M extends Message, S extends State> void runnableBody(final CallFrame<M,S> callFrame, final M msg_) {
			
			final Push<M,S> house = (Push<M,S>) callFrame.stateAware;
			try {
				if (callFrame.state == StateLess.STATELESS_STATE) {
					// no synchronization needed
					logger.info(String.format(
							"processing push request: msg [%s] state [%s]"
							, msg_.getClass().getSimpleName()
							, callFrame.state.getId()));
					house.getRoutine().routine(msg_, callFrame.state);
				}
				else {
					// synchronization required 
					synchronized (callFrame.state) {
						logger.info(String.format(
								"processing push request: msg [%s] state [%s]"
								, msg_.getClass().getSimpleName()
								, callFrame.state.getId()));
						house.getRoutine().routine(msg_, callFrame.state);
					}
				}
			} catch (Exception e_) {
				logger.error("exception: jobId [" + callFrame.jobId + "]: "  +
						e_.getMessage() + "\nStacktrace:\n" + FrameworkException.getStringStackTrace(e_));
			}			
		}
	}
	
	public static class PullInternal
	{
		
		public PullInternal() {
			_internal = new Internal();
		}
		
		public static <R,M extends Message, S extends State> Response<R> callableBody(final CallFrame<M,S> callFrame, final M msg_) {
			@SuppressWarnings("unchecked")
			final Pull<R,M,S> pull = (Pull<R, M, S>) callFrame.stateAware;
			try {
				if (callFrame.state == StateLess.STATELESS_STATE) {
					logger.info(String.format(
							"processing pull request: msg [%s] state [%s]"
							, msg_.getClass().getSimpleName()
							, callFrame.state.getId()));
					return wrapResponse(pull.getCalculation().calculate(msg_, callFrame.state));
				}
				else {
					synchronized (callFrame.state) {
						logger.info(String.format(
								"processing pull request: msg [%s] state [%s]"
								, msg_.getClass().getSimpleName()
								, callFrame.state.getId()));
						return wrapResponse(pull.getCalculation().calculate(msg_, callFrame.state));
					}
				}				
			} catch (Exception e_) {
				logger.warn(String.format("jobId [%s:%s:%d] threw exception: caught, wrapping exception for reaping"
						, pull.getId(), callFrame.state.getId(), callFrame.jobId));
				return wrapException(e_);
			}			
		}
		
		private static <R> Response<R> wrapResponse(final R response_) {
			final Response<R> wrapper = new ResponseSimple<R>();
			wrapper.setResponse(response_);
			return wrapper;		
		}
		
		private static <R> Response<R> wrapException(final Exception e_) {
			final Response<R> wrapper = new ResponseSimple<R>();
			wrapper.setResponse(null);
			wrapper.setException(e_);
			return wrapper;		
		}
	
		Internal _internal;
	}

	
	private static final PushInternal pushInternal = new PushInternal();
	private static final PullInternal pullInternal = new PullInternal();
}
