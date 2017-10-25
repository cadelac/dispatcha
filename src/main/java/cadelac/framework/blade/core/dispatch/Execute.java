package cadelac.framework.blade.core.dispatch;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cadelac.framework.blade.Framework;
import cadelac.framework.blade.core.invocation.Response;
import cadelac.framework.blade.core.message.Message;
import cadelac.framework.blade.core.state.State;

public class Execute {

	private static final ExecutorService EXECUTION_SERVICE = 
			Executors.newFixedThreadPool(
					Framework.getThreadpoolSize());

	
	public static void immediateExecution(final Runnable command_) {
		EXECUTION_SERVICE.execute(command_);
	}
	
	public static <R, M extends Message, S extends State> 
	Future<Response<R>> executePull(
			Callable<Response<R>> callable) 
					throws Exception {
		final List<Future<Response<R>>> futures = 
				EXECUTION_SERVICE.invokeAll(
						Arrays.asList(callable)
						, Framework.getQuantum()
						, TimeUnit.MILLISECONDS);
		return futures.get(0);		
	}

	
	
	private static final ScheduledExecutorService SCHEDULED_EXECUTION_SERVICE =
			Executors.newScheduledThreadPool(
					Framework.getScheduledThreadpoolSize());

	public static void delayedExecution(
			final Runnable command_
			, final long delay_) {
		SCHEDULED_EXECUTION_SERVICE.schedule(
				command_
				, delay_
				, TimeUnit.MILLISECONDS);	
	}
	
	public static void repeatedExecution(
			final Runnable command_
			, final long delay_
			, final long period_) {
		SCHEDULED_EXECUTION_SERVICE.scheduleAtFixedRate(
				command_
				, delay_
				, period_
				, TimeUnit.MILLISECONDS);	
	}
}
















/*
public static <M extends Message, S extends State> 
void push(
		StateAware<M,S> stateAware_
		, final M implementation_
		, final long delay_) 
				throws Exception {
	// in call to this method, argument passed is implementation class
	final Dispatch.CallFrame<M,S> callFrame = 
			Dispatch.populateCallFrame(
					new Dispatch.CallFrame<M,S>()
					, stateAware_
					, implementation_);

	
	pushy(
			() -> Dispatch.msgPush.runnableBody(callFrame, implementation_)
			, delay_);
}
public static <M extends Message, S extends State> 
void push(
		StateAware<M,S> stateAware_
		, final M implementation_
		, final long delay_
		, final long period_) 
				throws Exception {
	// in call to this method, argument passed is implementation class
	final Dispatch.CallFrame<M,S> callFrame = 
			Dispatch.populateCallFrame(
					new Dispatch.CallFrame<M,S>()
					, stateAware_
					, implementation_);

	pushy(
			() -> Dispatch.msgPush.runnableBody(callFrame, implementation_)
			, delay_
			, period_);
}
*/
