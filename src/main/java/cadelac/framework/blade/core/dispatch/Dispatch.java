package cadelac.framework.blade.core.dispatch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import cadelac.framework.blade.Framework;
import cadelac.framework.blade.core.exception.ArgumentException;
import cadelac.framework.blade.core.exception.FrameworkException;
import cadelac.framework.blade.core.exception.InitializationException;
import cadelac.framework.blade.core.exception.RouteException;
import cadelac.framework.blade.core.exception.SystemException;
import cadelac.framework.blade.core.invocation.Response;
import cadelac.framework.blade.core.invocation.ResponseBase;
import cadelac.framework.blade.core.message.Dispatchable;
import cadelac.framework.blade.core.message.Generated;
import cadelac.framework.blade.core.message.Message;
import cadelac.framework.blade.core.state.State;
import cadelac.framework.blade.core.state.StateAware;
import cadelac.framework.blade.core.state.StateLess;
import cadelac.framework.blade.core.state.StateManager;
import cadelac.framework.blade.core.state.experimental.StateBlock;


public class Dispatch {

	
	
	/*
	 * call below determines the mapping from prototype to concrete class
	 */
	@SuppressWarnings("unchecked")
	public static <G extends Generated> 
	Class<G> getConcreteClassOf(final Class<G> protoType_) throws Exception {
		return (Class<G>) Framework.getPrototype2ConcreteMap().get(protoType_);
	}
	
	
	/*
	 * BIND CALLS
	 */
	
	// bind message to push

	public static <M extends Message, S extends State> 
	void bind(final Class<M> protoType_, Push<M,S> proposedPush_) 
			throws Exception {
		final Class<M> implementation = getConcreteClassOf(protoType_);
		bindPushDispatchable(implementation, proposedPush_);
	}		
	
	public static <D extends Dispatchable, S extends State> 
	void bindPushDispatchable(final Class<D> implementation_, Push<D,S> proposedPush_) 
			throws Exception {
		msgPush._internal.checkedPut(implementation_, proposedPush_);
	}

	// bind hashId to push
	public static <D extends Dispatchable, S extends State>
	void bind(final String hashId_, Push<D,S> proposedPush_)
			throws Exception {
		hashPush._internal.checkedPut(hashId_, proposedPush_);
	}
	
	
	
	
	// bind message to pull
	
	
	public static <R, M extends Message, S extends State>
	void bind(final Class<M> protoType_, Pull<R,M,S> proposedPull_)
			throws Exception {
		final Class<M> implementation = getConcreteClassOf(protoType_);
		bindPullDispatchable(implementation, proposedPull_);
	}
	
	public static <R, D extends Dispatchable, S extends State> 
	void bindPullDispatchable(final Class<D> implementation_, Pull<R,D,S> proposedPull_) 
			throws Exception {
		msgPull._internal.checkedPut(implementation_, proposedPull_);
	}
	
	// bind hashId to pull
	public static <R, D extends Dispatchable, S extends State>
	void bind(final String hashId_, Pull<R,D,S> proposedPull_) 
			throws Exception {
		hashPull._internal.checkedPut(hashId_, proposedPull_);
	}
	
	
	
	/*
	 * PUSH CALLS
	 */

	// dispatch push using message
	
	public static <D extends Dispatchable, S extends State> 
	void push(final D implementation_) 
			throws Exception {
		@SuppressWarnings("unchecked")
		final StateAware<D,S> stateAware = 
			(StateAware<D,S>) msgPush._internal.get(
					implementation_.getClass());
		immediatePush(stateAware, implementation_);	
	}
	
	public static <D extends Dispatchable, S extends State> 
	void push(final D implementation_, final long delay_) 
			throws Exception {

		@SuppressWarnings("unchecked")
		final StateAware<D,S> stateAware = 
			(StateAware<D,S>) msgPush._internal.get(
					implementation_.getClass());
		delayedPush(stateAware, implementation_, delay_);	
	}
	
	public static <D extends Dispatchable, S extends State> 
	void push(final D implementation_, final long delay_, final long period_)
			throws Exception {
		@SuppressWarnings("unchecked")
		final StateAware<D,S> stateAware = 
			(StateAware<D,S>) msgPush._internal.get(
					implementation_.getClass());		
		repeatedPush(stateAware, implementation_, delay_, period_);
	}	
	
	// dispatch push using hash id
	
	public static <D extends Dispatchable, S extends State> 
	void push(final String hashId_, final D implementation_) 
			throws Exception {
		final StateAware<D,S> stateAware = hashPush._internal.get(hashId_);
		immediatePush(stateAware, implementation_);	
	}

	public static <D extends Dispatchable, S extends State> 
	void push(final String hashId_, final D implementation_, final long delay_)
			throws Exception {
		final StateAware<D,S> stateAware = hashPush._internal.get(hashId_);
		delayedPush(stateAware, implementation_, delay_);	
	}
	
	public static <D extends Dispatchable, S extends State> 
	void push(
			final String hashId_
			, final D implementation_
			, final long delay_
			, final long period_) 
					throws Exception {
		final StateAware<D,S> stateAware = hashPush._internal.get(hashId_);
		repeatedPush(stateAware, implementation_, delay_, period_);
	}	


	
	/*
	 * PULL CALLS
	 */
	
	// dispatch pull using message
	
	public static <R, D extends Dispatchable, S extends State> 
	Future<Response<R>> pull(final D implementation_)
			throws Exception {
		final Class<? extends Dispatchable> mc = implementation_.getClass();
		@SuppressWarnings("unchecked")
		final StateAware<D,S> stateAware = 
			(StateAware<D,S>) msgPull._internal.get(mc);
		return pull(stateAware, implementation_);
	}
	
	// dispatch pull using hash id
	
	public static <R, D extends Dispatchable, S extends State> 
	Future<Response<R>> pull(final String hashId_, final D implementation_)
			throws Exception {
		final StateAware<D,S> stateAware = hashPull._internal.get(hashId_);
		return pull(stateAware, implementation_);
	}
	
	public static <D extends Dispatchable> 
	D extractResponse(final Future<Response<D>> future) 
			throws SystemException, InterruptedException, ExecutionException {
		final Response<D> futureResponse = future.get();
		if (futureResponse.getException() != null) {
			final String diagnostic = "exception encountered on reaping future...";
			logger.warn(diagnostic);
			throw (SystemException) new SystemException(diagnostic)
				.initCause(futureResponse.getException());
		}
		return futureResponse.getResponse();
	}
	

	public static <D extends Dispatchable> 
	D extractResponse(
			final Future<Response<D>> future
			, final String operation) 
					throws SystemException, InterruptedException, ExecutionException {
		final Response<D> futureResponse = future.get();
		if (futureResponse.getException() != null) {
			final String diagnostic = 
					String.format("exception encountered on operation: %s", operation);
			logger.warn(diagnostic);
			throw (SystemException) new SystemException(diagnostic)
				.initCause(futureResponse.getException());
		}
		return futureResponse.getResponse();
	}

	
	
	// activation record
	static class CallFrame<D extends Dispatchable, S extends State> {
		public StateAware<D,S> stateAware;
		public String stateId;
		public S state;
		public long jobId;
	}
	
	static <D extends Dispatchable, S extends State> 
	CallFrame<D,S> populateCallFrame(
			final CallFrame<D,S> callFrame_
			, StateAware<D,S> stateAware_
			, final D msg_)
					throws Exception {
		if (msg_ == null) {
			final String diag = String.format("message is null: invalid argument");
			logger.warn(diag);
			throw new ArgumentException(diag);			
		}
		if (stateAware_==null) {
			final String errorMsg = 
					String.format(
							"Message %s not bound to an agent: call Dispatch.bind"
							, msg_.getClass().getSimpleName());
			logger.error(errorMsg);
			throw new InitializationException(errorMsg);
		}
		callFrame_.stateAware = stateAware_;
		callFrame_.stateId = callFrame_.stateAware.getStateId(msg_);
		callFrame_.state = 
				StateManager.acquireState(
						callFrame_.stateAware
						, msg_
						, callFrame_.stateId);
		callFrame_.jobId = Framework.getMonitor().getNextJobId();
		return callFrame_;
	}
	
	// Push by routing using class of Message
	private static class MsgPush extends PushDispatch<Class<? extends Dispatchable>> {
	}
	
	// Pull by routing using class of Message
	private static class MsgPull extends PullDispatch<Class<? extends Dispatchable>> {
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
		
		public <D extends Dispatchable, S extends State>
		void runnableBody(final CallFrame<D,S> callFrame, final D msg_) {
			
			final Push<D,S> push = (Push<D,S>) callFrame.stateAware;
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
		
		private static <D extends Dispatchable,S extends State> 
		void doRoutine(Push<D,S> push, final CallFrame<D,S> callFrame, final D msg_) 
				throws Exception {
			logger.debug(String.format(
					"processing push request msg [%s] for handler [%s] with job [%d] on state [%s]"
					, msg_.getClass().getSimpleName()
					, callFrame.stateAware.getId()
					, callFrame.jobId
					, callFrame.state.getId()));
			push.getRoutine().routine(
					msg_
					, callFrame.state);
		}

		
		
		// access is 'package'
		InternalLookupBase<K> _internal;
	}
	
	// will dispatch using pull
	private static class PullDispatch<K> {
		
		public PullDispatch() {
			_internal = new InternalLookupBase<K>();
		}
		
		public static <R,D extends Dispatchable,S extends State> 
		Response<R> callableBody(final CallFrame<D,S> callFrame, final D msg_) {
			@SuppressWarnings("unchecked")
			final Pull<R,D,S> pull = (Pull<R,D,S>) callFrame.stateAware;
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
		
		private static <R,D extends Dispatchable,S extends State> 
		Response<R> doCalculation(Pull<R,D,S> pull, final CallFrame<D,S> callFrame, final D msg_) 
				throws Exception {
			logger.info(String.format(
					"processing pull request msg [%s] for handler [%s] with job [%d] on state [%s]"
					, msg_.getClass().getSimpleName()
					, callFrame.stateAware.getId()
					, callFrame.jobId
					, callFrame.state.getId()));
			return new ResponseBase<R>(
					pull.getCalculation().calculate(
							msg_
							, callFrame.state));
		}
		
		// access is 'package'
		InternalLookupBase<K> _internal;
	}
	

	// Used to encapsulate internal lookup logic
	private static interface InternalLookup<K> {
		
		<D extends Dispatchable,S extends State> 
		void checkedPut(final K key_, final StateAware<D,S> proposedStateAware_)
				throws Exception;
		
		<D extends Dispatchable,S extends State> 
		StateAware<D,S> get(final K key_);
	}
	
	private static class InternalLookupBase<K> implements InternalLookup<K> {

		public InternalLookupBase() { 
			_provider = new HashMap<
					K,
					StateAware<? extends Dispatchable,? extends State>>(); 
		}
		
		@Override
		public <D extends Dispatchable,S extends State> 
		void checkedPut(final K key_, final StateAware<D,S> proposedStateAware_) 
				throws Exception {

			@SuppressWarnings("unchecked")
			final StateAware<D,S> entrenchedStateAware = 
				(StateAware<D,S>) _provider.get(key_);
			
			if (entrenchedStateAware != null) {
				final String diag = "Message bind failure: already bound";
				logger.warn(diag);
				throw new RouteException(diag);
			}	
			_provider.put(key_, proposedStateAware_);
		}
		
		@Override
		public <D extends Dispatchable,S extends State>
		StateAware<D,S> get(final K key_) {
			@SuppressWarnings("unchecked")
			final StateAware<D,S> stateAware = 
				(StateAware<D,S>) _provider.get(key_);
			return stateAware;
		}
		
		private final Map<K,StateAware<? extends Dispatchable,? extends State>> _provider;
	}
	
	// to expose private method push() below...
	public static <D extends Dispatchable, S extends State>
	void inlinePush(
			final StateAware<D,S> stateAware_
			, final D implementation_) 
			throws Exception {
		immediatePush(stateAware_, implementation_);
	}

	
	// using StateAware
	private static <D extends Dispatchable, S extends State> 
	void immediatePush(
			final StateAware<D,S> stateAware_
			, final D implementation_) 
					throws Exception {

		final CallFrame<D,S> callFrame = 
				populateCallFrame(
						new Dispatch.CallFrame<D,S>()
						, stateAware_
						, implementation_);
	
		Execute.immediateExecution(
				() -> msgPush.runnableBody(callFrame, implementation_));		
	}
	
	
	public static <S extends State>
	void pushInline(
			final S state_
			, final StateBlock<S> stateBlock_) throws Exception {
		Execute.immediateExecution(
				() -> Dispatch.runnableStateBlock(state_, stateBlock_));
	}
	
	public static <S extends State>
	void pushInline(
			final long delay_
			, final S state_
			, final StateBlock<S> stateBlock_) throws Exception {
		Execute.delayedExecution(
				() -> Dispatch.runnableStateBlock(state_, stateBlock_)
				, delay_);
	}
	
	public static <S extends State>
	void pushInline(
			final long period_
			, final long delay_
			, final S state_
			, final StateBlock<S> stateBlock_) throws Exception {
		Execute.repeatedExecution(
				() -> Dispatch.runnableStateBlock(state_, stateBlock_)
				, delay_
				, period_);
		

	}
	
	public static <S extends State>
	void runnableStateBlock(
			final S state_
			, final StateBlock<S> stateBlock_)  {
		try {
			if (state_ == StateLess.STATELESS_STATE) {
				doInlineStateBlock(state_, stateBlock_);
			}
			else { // synchronization required
				synchronized (state_) {
					doInlineStateBlock(state_, stateBlock_);
				}
			}
		} catch (Exception e_) {
			logger.warn(String.format(
					"exception on inline push on state [%s]"
					, state_.getId()));
			logger.warn(FrameworkException.getStringStackTrace(e_));
		}	
	}
	
	private static <S extends State> 
	void doInlineStateBlock(final S state_, final StateBlock<S> stateBlock_) 
			throws Exception {
		logger.debug(String.format(
				"processing inline on state [%s]"
				, state_.getId()));
		stateBlock_.block(state_);
	}
	
	
	private static <D extends Dispatchable, S extends State> 
	void delayedPush(
			final StateAware<D,S> stateAware
			, final D implementation_
			, final long delay_) 
					throws Exception {
		
		final CallFrame<D,S> callFrame = 
				populateCallFrame(
						new CallFrame<D,S>()
						, stateAware
						, implementation_);
		Execute.delayedExecution(
				() -> msgPush.runnableBody(callFrame, implementation_)
				, delay_);
	}
	
	private static <D extends Dispatchable, S extends State> 
	void repeatedPush(
			final StateAware<D,S> stateAware
			, final D implementation_
			, final long delay_
			, final long period_) 
					throws Exception {
		
		final CallFrame<D,S> callFrame = 
				populateCallFrame(
						new CallFrame<D,S>()
						, stateAware
						, implementation_);
		Execute.repeatedExecution(
				() -> msgPush.runnableBody(callFrame, implementation_)
				, delay_
				, period_);
	}
	
	// to expose private method pull() below...
	public static <R, D extends Dispatchable, S extends State> 
	Future<Response<R>> inlinePull(
			final StateAware<D,S> stateAware_
			, final D implementation_) 
			throws Exception {
		return pull(stateAware_, implementation_);
	}
	
	// dispatch pull
	private static <R, D extends Dispatchable, S extends State> 
	Future<Response<R>> pull(
			final StateAware<D,S> stateAware_
			, final D implementation_) 
					throws Exception {
		final CallFrame<D,S> callFrame = 
				populateCallFrame(
						new CallFrame<D,S>()
						, stateAware_
						, implementation_);
		return Execute.executePull(
				() -> MsgPull.callableBody(callFrame, implementation_)
		);
	}
	
	// dispatchers...
	
	// dispatchers using message-type to discriminate
	private static final MsgPush msgPush = new MsgPush();
	private static final MsgPull msgPull = new MsgPull();
	
	// dispatchers using hash id to discriminate
	private static final HashPush hashPush = new HashPush();
	private static final HashPull hashPull = new HashPull();
	
	
	
	private static final Logger logger = Logger.getLogger(Dispatch.class);
}


//remove reference to EnvelopeMsg...
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


//remove reference to EnvelopeMsg...
//	public static <R extends Dispatchable> R remotePull(
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
