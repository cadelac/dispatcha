package cadelac.framework.blade.core.invocation.job;

import org.apache.log4j.Logger;

import cadelac.framework.blade.handler.RequestHandler;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.invocation.Response;
import cadelac.lib.primitive.job.JobBase;

public class RequestJobBase<R, M extends Message, S extends State> extends JobBase<M,S> implements RequestJob<R,M,S> {

	public RequestJobBase(
			final RequestHandler<R,M,S> handler_,
			final M message_,
			final S state_,
			final long jobId_,
			final long quantum_) {
		super(message_, state_, jobId_);
		_handler = handler_;
		_quantum = quantum_;
	}

	
	@Override
	public RequestHandler<R, M, S> getHandler() {
		return _handler;
	}

	@Override
	public Response<R> call() throws Exception {
		if (logger.isDebugEnabled())
			logger.debug("RequestJob.call: jobId [" + this.getJobId() + "], quantum [" + _quantum + "]");
		Response<R> response = _handler.invoke(this.getMessage(), this.getState());
		return response;
	}
	
	
	private static final Logger logger = Logger.getLogger(RequestJobBase.class);
	
	private final RequestHandler<R,M,S> _handler;
	private final long _quantum;
}
