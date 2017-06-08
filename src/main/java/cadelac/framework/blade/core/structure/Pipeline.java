package cadelac.framework.blade.core.structure;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.service.delivery.Dispatch;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.delivery.pull.Pull;
import cadelac.lib.primitive.invocation.Response;

public class Pipeline {
	
	public static class Interim<M extends Message> {
		private final M _message;
		public Interim(final M message_) {
			_message = message_;
		}
		public M get() { return _message; }
		
		public <R extends Message, S extends State> Interim<R> into(Pull<R,M,S> pull_) throws Exception {
			Future<Response<R>> fut = Dispatch.pull(pull_, _message);
			Response<R> response = fut.get();
			Exception exception = response.getException();
			if (exception != null) {
				final String diag = String.format("exception: [%s]", exception.getMessage());
				logger.warn(diag);
				throw exception;
			}
			return new Interim<R>(response.getResponse());
		}
	}
	
	
	public static <M extends Message> Interim<M> take(final M message_) {
		return new Interim<M>(message_);
	}

	
	private List<Pull<? extends Message, ? extends Message, ? extends State>> _pulls;
	@SafeVarargs
	public Pipeline(Pull<? extends Message, ? extends Message, ? extends State>... pull) {
		_pulls = Arrays.asList(pull);
	}
	@SuppressWarnings("unchecked")
	public <R extends Message, M extends Message> R pull(final M message_) throws Exception {
		@SuppressWarnings("rawtypes")
		Interim interim = take(message_);
		for (Pull<?,?,?> pull : _pulls) {
			interim = interim.into(pull);
		}
		return (R) interim.get();
	}
	
	
	private static final Logger logger = Logger.getLogger(Pipeline.class);
}
