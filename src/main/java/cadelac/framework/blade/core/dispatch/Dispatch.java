package cadelac.framework.blade.core.dispatch;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cadelac.framework.blade.Framework;
import cadelac.framework.blade.comm.rpc.RpcTicket;
import cadelac.framework.blade.core.exception.ArgumentException;
import cadelac.framework.blade.core.exception.FrameworkException;
import cadelac.framework.blade.core.exception.InitializationException;
import cadelac.framework.blade.core.exception.RouteException;
import cadelac.framework.blade.core.exception.SystemException;
import cadelac.framework.blade.core.invocation.Response;
import cadelac.framework.blade.core.invocation.ResponseBase;
import cadelac.framework.blade.core.message.HandlerDescriptorMsg;
import cadelac.framework.blade.core.message.Message;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StateAware;
import cadelac.framework.blade.core.state.StateLess;
import cadelac.framework.blade.core.state.StateManager;


public class Dispatch {

	// push bind calls
	public static <M extends Message, S extends State> void bind(
			final Class<M> protoType_, Push<M,S> proposedPush_) 
					throws Exception {
		// convert prototype class to implementation class because
		// internal mapping is from implementation class to StateAware...
		@SuppressWarnings("unchecked")
		final Class<M> implementation = (Class<M>) Framework.getPrototype2ConcreteMap().get(protoType_);
		msgPush._internal.checkedPut(implementation, proposedPush_);
	}
	
	public static <M extends Message, S extends State> void bind(
			final String hashId_, Push<M,S> proposedPush_) 
					throws Exception {
		hashPush._internal.checkedPut(hashId_, proposedPush_);
	}
	
	
	
	// pull bind calls
	public static <R, M extends Message, S extends State> void bind(
			final Class<M> protoType_, Pull<R,M,S> proposedPull_) 
					throws Exception {
		// convert prototype class to implementation class because
		// internal mapping is from implementation class to StateAware...
		@SuppressWarnings("unchecked")
		final Class<M> implementation = (Class<M>) Framework.getPrototype2ConcreteMap().get(protoType_);
		msgPull._internal.checkedPut(implementation, proposedPull_);
	}
	
	public static <R, M extends Message, S extends State> void bind(
			final String hashId_, Pull<R,M,S> proposedPull_) 
					throws Exception {
		hashPull._internal.checkedPut(hashId_, proposedPull_);
	}
	
	
	
	// push calls
	// dispatch push using message
	public static <M extends Message, S extends State> void push(final M implementation_) 
			throws Exception {
		// in call to this method, argument passed is implementation class
		@SuppressWarnings("unchecked")
		final StateAware<M,S> stateAware = 
			(StateAware<M, S>) msgPush._internal.get(implementation_.getClass());
		push(stateAware, implementation_);	
	}
	public static <M extends Message, S extends State> void push(final M implementation_, final long delay_) 
			throws Exception {
		// in call to this method, argument passed is implementation class
		@SuppressWarnings("unchecked")
		final StateAware<M,S> stateAware = (StateAware<M, S>) msgPush._internal.get(implementation_.getClass());
		push(stateAware, implementation_, delay_);	
	}
	public static <M extends Message, S extends State> void push(final M implementation_, final long delay_, final long period_) 
			throws Exception {
		// in call to this method, argument passed is implementation class
		@SuppressWarnings("unchecked")
		final StateAware<M,S> stateAware = (StateAware<M, S>) msgPush._internal.get(implementation_.getClass());
		push(stateAware, implementation_, delay_, period_);	
	}	
	
	// dispatch push using hash id
	public static <M extends Message, S extends State> void push(final String hashId_, final M implementation_) 
			throws Exception {
		// in call to this method, argument passed is implementation class
		final StateAware<M,S> stateAware = hashPush._internal.get(hashId_);
		push(stateAware, implementation_);	
	}
	public static <M extends Message, S extends State> void push(final String hashId_, final M implementation_, final long delay_) 
			throws Exception {
		// in call to this method, argument passed is implementation class
		final StateAware<M,S> stateAware = hashPush._internal.get(hashId_);
		push(stateAware, implementation_, delay_);	
	}
	public static <M extends Message, S extends State> void push(final String hashId_, final M implementation_, final long delay_, final long period_) 
			throws Exception {
		// in call to this method, argument passed is implementation class
		final StateAware<M,S> stateAware = hashPush._internal.get(hashId_);
		push(stateAware, implementation_, delay_, period_);	
	}	

	// pull calls
	public static <R, M extends Message, S extends State> Future<Response<R>> pull(
			final M implementation_) 
					throws Exception {
		final Class<? extends Message> mc = implementation_.getClass();
		@SuppressWarnings("unchecked")
		final StateAware<M,S> stateAware = (StateAware<M, S>) msgPull._internal.get(mc);
		return pull(stateAware, implementation_);
	}
	
	// pull using hash id
	public static <R, M extends Message, S extends State> Future<Response<R>> pull(
			final String hashId_
			, final M implementation_) 
					throws Exception {
		final StateAware<M,S> stateAware = hashPull._internal.get(hashId_);
		return pull(stateAware, implementation_);
	}
	
	// dispatch pull
	public static <R, M extends Message, S extends State> Future<Response<R>> pull(
			final StateAware<M,S> stateAware_
			, final M implementation_) 
					throws Exception {
		// in call to this method, argument passed is implementation class
		final CallFrame<M,S> callFrame = 
				populateCallFrame(new CallFrame<M,S>(), stateAware_, implementation_);
		return executePull(
				() -> MsgPull.callableBody(callFrame, implementation_)
		);
	}
	

	public static <M extends Message> M extractResponse(
			final Future<Response<M>> future
			, final String operation) 
					throws SystemException, InterruptedException, ExecutionException {
		final Response<M> futureResponse = future.get();
		if (futureResponse.getException() != null) {
			final String diagnostic = 
					String.format("exception encountered on operation: %s", operation);
			logger.warn(diagnostic);
			throw (SystemException) new SystemException(diagnostic)
				.initCause(futureResponse.getException());
		}
		return futureResponse.getResponse();
	}
	
// remove reference to EnvelopeMsg...
//	public static <M extends EnvelopeMsg, S extends State> void setupRemotePush(
//			final HandlerDescriptorMsg handlerDescriptorMsg_
//			, final Routine<M,S> routine
//			, final StateIdMapper<M> stateIdMapper_
//			, final StateCreator<M,S> stateCreator_) 
//					throws NoSuchAlgorithmException, Exception {
//		final Push<M,S> pushHandler =
//				new PushBase<M,S>(
//						handlerDescriptorMsg_.getHandlerName()
//						, routine
//						, stateIdMapper_
//						, stateCreator_);
//		bind(handlerDescriptorMsg_.buildHashId(), pushHandler);
//	}


// remove reference to EnvelopeMsg...
//	public static <R extends Message> R remotePull(
//			final HandlerDescriptorMsg handlerDescriptorMsg_
//			, final EnvelopeMsg envelopeMsg_) 
//					throws NoSuchAlgorithmException, Exception {
//		final RpcTicket<R> rpcTicket =
//				new RpcTicket<R>(envelopeMsg_.getSequenceNo());
//		RpcTicket.sowForLater(rpcTicket);
//		// this transmits websocket message
//		push(handlerDescriptorMsg_.buildHashId(), envelopeMsg_);
//		return rpcTicket.get();
//	}
	
	
	private static <M extends Message, S extends State> void push(
			StateAware<M,S> stateAware_
			, final M implementation_) 
					throws Exception {
		// in call to this method, argument passed is implementation class
		final CallFrame<M,S> callFrame = populateCallFrame(
				new CallFrame<M,S>(), stateAware_, implementation_);

		EXECUTION_SERVICE.execute(() -> {
			msgPush.runnableBody(callFrame, implementation_);
		});		
	}
	
	private static <M extends Message, S extends State> void push(
			StateAware<M,S> stateAware_
			, final M implementation_
			, final long delay_) 
					throws Exception {
		// in call to this method, argument passed is implementation class
		final CallFrame<M,S> callFrame = populateCallFrame(
				new CallFrame<M,S>(), stateAware_, implementation_);

		SCHEDULED_EXECUTION_SERVICE.schedule(
				() -> {
					msgPush.runnableBody(callFrame, implementation_);
				}
				, delay_
				, TimeUnit.MILLISECONDS);		
	}
	
	private static <M extends Message, S extends State> void push(
			StateAware<M,S> stateAware_
			, final M implementation_
			, final long delay_
			, final long period_) 
					throws Exception {
		// in call to this method, argument passed is implementation class
		final CallFrame<M,S> callFrame = populateCallFrame(
				new CallFrame<M,S>(), stateAware_, implementation_);

		SCHEDULED_EXECUTION_SERVICE.scheduleAtFixedRate(
				() -> {
					msgPush.runnableBody(callFrame, implementation_);
				}
				, delay_
				, period_
				, TimeUnit.MILLISECONDS);		
	}	
	
	
	
	private static <R, M extends Message, S extends State> Future<Response<R>> executePull(
			Callable<Response<R>> callable) 
					throws Exception {
		final List<Future<Response<R>>> futures = EXECUTION_SERVICE.invokeAll(
				Arrays.asList(callable)
				, Framework.getQuantum()
				, TimeUnit.MILLISECONDS);
		return futures.get(0);		
	}

	// activation record
	private static class CallFrame<M extends Message, S extends State> {
		public StateAware<M,S> stateAware;
		public String stateId;
		public S state;
		public long jobId;
	}
	
	private static <M extends Message, S extends State> CallFrame<M,S> populateCallFrame(
			final CallFrame<M,S> callFrame_
			, StateAware<M,S> stateAware_
			, final M msg_)
					throws Exception {
		
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
		callFrame_.state = StateManager.acquireState(callFrame_.stateAware, msg_, callFrame_.stateId);
		callFrame_.jobId = Framework.getMonitor().getNextJobId();
		return callFrame_;
	}
	
	// Push by routing using class of Message
	private static class MsgPush extends PushDispatch<Class<? extends Message>> {
	}
	
	// Pull by routing using class of Message
	private static class MsgPull extends PullDispatch<Class<? extends Message>> {
	}

	// Push by routing using Hash of Handler
	private static class HashPush extends PushDispatch<String> {
		
	} 
	
	// Pull by routing using Hash of Handler
	private static class HashPull extends PullDispatch<String> {
		
	}
	
	
	// will dispatch using push
	private static class PushDispatch<K> {
		
		public PushDispatch() {
			_internal = new InternalLookupBase<K>();
		}
		
		public <M extends Message, S extends State>
		void runnableBody(final CallFrame<M,S> callFrame, final M msg_) {
			
			final Push<M,S> push = (Push<M,S>) callFrame.stateAware;
			try {
				if (callFrame.state == StateLess.STATELESS_STATE) {
					doRoutine(push, callFrame, msg_);
				}
				else { // synchronization required
					synchronized (callFrame.state) {
						doRoutine(push, callFrame, msg_);
					}
				}
			} catch (Exception e_) {
				logger.warn(String.format(
						"exception on push request msg [%s] for handler [%s] with job [%d] on state [%s]"
						, msg_.getClass().getSimpleName()
						, callFrame.stateAware.getId()
						, callFrame.jobId
						, callFrame.state.getId()));
				logger.warn(FrameworkException.getStringStackTrace(e_));
			}			
		}
		
		private static <M extends Message,S extends State> 
		void doRoutine(Push<M,S> push, final CallFrame<M,S> callFrame, final M msg_) throws Exception {
			logger.info(String.format(
					"processing push request msg [%s] for handler [%s] with job [%d] on state [%s]"
					, msg_.getClass().getSimpleName()
					, callFrame.stateAware.getId()
					, callFrame.jobId
					, callFrame.state.getId()));
			push.getRoutine().routine(msg_, callFrame.state);
		}

		// access is 'package'
		InternalLookupBase<K> _internal;
	}
	// will dispatch using pull
	private static class PullDispatch<K> {
		
		public PullDispatch() {
			_internal = new InternalLookupBase<K>();
		}
		
		public static <R,M extends Message,S extends State> 
		Response<R> callableBody(final CallFrame<M,S> callFrame, final M msg_) {
			@SuppressWarnings("unchecked")
			final Pull<R,M,S> pull = (Pull<R, M, S>) callFrame.stateAware;
			try {
				if (callFrame.state == StateLess.STATELESS_STATE) {
					return doCalculation(pull, callFrame, msg_);
				}
				else {
					synchronized (callFrame.state) {
						return doCalculation(pull, callFrame, msg_);
					}
				}				
			} catch (Exception e_) {
				logger.warn(String.format(
						"exception on pull request msg [%s] for handler [%s] with job [%d] on state [%s]"
						, msg_.getClass().getSimpleName()
						, callFrame.stateAware.getId()
						, callFrame.jobId
						, callFrame.state.getId()));
				logger.warn(FrameworkException.getStringStackTrace(e_));
				return new ResponseBase<R>(e_);
			}			
		}
		
		private static <R,M extends Message,S extends State> 
		Response<R> doCalculation(Pull<R,M,S> pull, final CallFrame<M,S> callFrame, final M msg_) throws Exception {
			logger.info(String.format(
					"processing pull request msg [%s] for handler [%s] with job [%d] on state [%s]"
					, msg_.getClass().getSimpleName()
					, callFrame.stateAware.getId()
					, callFrame.jobId
					, callFrame.state.getId()));
			return new ResponseBase<R>(pull.getCalculation().calculate(msg_, callFrame.state));
		}
		
		// access is 'package'
		InternalLookupBase<K> _internal;
	}
	

	// Used to encapsulate internal lookup logic
	private static interface InternalLookup<K> {
		<M extends Message,S extends State> void checkedPut(final K key_, final StateAware<M,S> proposedStateAware_) throws Exception;
		<M extends Message,S extends State> StateAware<M,S> get(final K key_);
	}
	
	private static class InternalLookupBase<K> implements InternalLookup<K> {

		public InternalLookupBase() { _provider = new HashMap<K,StateAware<? extends Message,? extends State>>(); }
		
		@Override
		public <M extends Message,S extends State> void checkedPut(final K key_, final StateAware<M,S> proposedStateAware_) throws Exception {

			@SuppressWarnings("unchecked")
			final StateAware<M,S> entrenchedStateAware = (StateAware<M,S>) _provider.get(key_); 
			if (entrenchedStateAware != null) {
				final String diag = String.format("Message bind failure: already bound");
				logger.warn(diag);
				throw new RouteException(diag);
			}	
			_provider.put(key_, proposedStateAware_);
		}
		
		@Override
		public <M extends Message,S extends State> StateAware<M,S> get(final K key_) {
			@SuppressWarnings("unchecked")
			final StateAware<M,S> stateAware = (StateAware<M,S>) _provider.get(key_);
			return stateAware;
		}
		
		private final Map<K,StateAware<? extends Message,? extends State>> _provider;
	}
	

	
	
	private static final ExecutorService EXECUTION_SERVICE = Executors.newFixedThreadPool(Framework.getThreadpoolSize());
	
	private static final ScheduledExecutorService SCHEDULED_EXECUTION_SERVICE = Executors.newScheduledThreadPool(Framework.getScheduledThreadpoolSize()); 
	
	
	
	// dispatchers...
	private static final MsgPush msgPush = new MsgPush();
	private static final MsgPull msgPull = new MsgPull();
	
	
	private static final HashPush hashPush = new HashPush();
	private static final HashPull hashPull = new HashPull();
	
	private static final Logger logger = Logger.getLogger(Dispatch.class);
}
