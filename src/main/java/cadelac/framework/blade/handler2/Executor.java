package cadelac.framework.blade.handler2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.Framework;
import cadelac.framework.blade.core.component.shell.ShellSimple;
import cadelac.framework.blade.core.invocation.job.RequestJob;
import cadelac.framework.blade.core.invocation.job.SendJob;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.handler.ActivationReply;
import cadelac.lib.primitive.handler.ActivationSubmit;
import cadelac.lib.primitive.invocation.Response;

public class Executor {
	
	public static int THREADPOOL_SIZE = 32;
	public static int SCHEDULED_THREADPOOL_SIZE = 5;
	
	public static void init(final int threadPoolSize_) {
		logger.info(String.format("Threadpool initialized with [%d] threads", threadPoolSize_));
		_executorService = Executors.newFixedThreadPool(threadPoolSize_);
		logger.info(String.format("Scheduled Threadpool initialized with [%d] threads", SCHEDULED_THREADPOOL_SIZE));
		_scheduledExecutorService = Executors.newScheduledThreadPool(SCHEDULED_THREADPOOL_SIZE);
	}
	
	public static <M extends Message, S extends State> 
	void execute(final SendJob<M,S> sendJob_)
			throws InterruptedException {
		_executorService.execute(sendJob_);
	}
	
	
	public static <M extends Message, S extends State> 
	void execute(final ActivationSubmit<M,S> activation_)
			throws InterruptedException {
		_executorService.execute(activation_);
	}

	
	public static <R, M extends Message, S extends State>
	Future<Response<R>> executeRequestJob(final RequestJob<R,M,S> job_, final long quantum_) 
			throws InterruptedException {
		final List<RequestJob<R,M,S>> jobs = new ArrayList<RequestJob<R,M,S>>();
		jobs.add(job_);
		final List<Future<Response<R>>> futures = _executorService.invokeAll(jobs, 
				quantum_, 
				TimeUnit.MILLISECONDS);
		return futures.get(0);
	}
	
	public static <R, M extends Message, S extends State> 
	Future<Response<R>> executeRequest(final ActivationReply<R,M,S> activation_) 
			throws InterruptedException {

		final List<ActivationReply<R,M,S>> requests = new ArrayList<ActivationReply<R,M,S>>();
		requests.add(activation_);
		
		final List<Future<Response<R>>> futures = _executorService.invokeAll(requests, 
				Framework.getShell().getRack().getQuantum(), 
				TimeUnit.MILLISECONDS);
		final Future<Response<R>> fut = futures.get(0);
		return fut;
	}
	
	public static <M extends Message, S extends State>
	void delayedCall(final SendJob<M,S> sendJob_, final long delay_) {
		_scheduledExecutorService.schedule(sendJob_, delay_, TimeUnit.MILLISECONDS);
	}
	
	
	public static <M extends Message, S extends State>
	void recurringCall(final SendJob<M,S> sendJob_, final long delay_, final long period_) {
		_scheduledExecutorService.scheduleAtFixedRate(sendJob_, delay_, period_, TimeUnit.MILLISECONDS);
	}
	
	//		_scheduledExecutorService.scheduleAtFixedRate(sendJob, delay_, period_, TimeUnit.MILLISECONDS);
	private static final Logger logger = Logger.getLogger(Executor.class);
			
	private static ExecutorService _executorService;
	private static ScheduledExecutorService _scheduledExecutorService;
}
