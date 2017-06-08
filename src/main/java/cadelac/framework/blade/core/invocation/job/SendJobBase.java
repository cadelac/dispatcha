package cadelac.framework.blade.core.invocation.job;

import org.apache.log4j.Logger;

import cadelac.framework.blade.handler.SendHandler;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.exception.FrameworkException;
import cadelac.lib.primitive.job.JobBase;

public class SendJobBase<M extends Message, S extends State> extends JobBase<M,S> implements SendJob<M,S> {

	public SendJobBase(
			final SendHandler<M,S> handler_,
			final M message_,
			final S state_,
			final long jobId_) {
		super(message_, state_, jobId_);
		_handler = handler_;
	}
	
	
	@Override
	public SendHandler<M,S> getHandler() {
		return _handler;
	}
	
	
	@Override
	public void run() {
		if (logger.isDebugEnabled()) {
			logger.debug(SendJobBase.class.getSimpleName() + ".run: jobId [" + getJobId() + "]");
		}
		try {
			_handler.invoke(getMessage(), getState());
		}
		catch (Exception e_) {
			logger.error("exception on routine with job id  [" + getJobId() + "]: " + 
					e_.getMessage() + "\nStacktrace:\n" + FrameworkException.getStringStackTrace(e_));
		}
	}


	private static final Logger logger = Logger.getLogger(SendJobBase.class);
	
	private final SendHandler<M,S> _handler;

	
}
