package cadelac.framework.blade.core.invocation;

public class ResponseBase<T> implements Response<T> {

	public ResponseBase(final T response_) {
		_response = response_;
	}
	
	public ResponseBase(final Exception exception_) {
		_exception = exception_;
	}
	
	@Override
	public T getResponse() {
		return _response;
	}

	@Override
	public void setResponse(final T response_) {
		_response = response_;
	}
	
	@Override
	public Exception getException() {
		return _exception;
	}
	
	@Override
	public void setException(final Exception exception_) {
		_exception = exception_;
	}
			
	private T _response;	
	private Exception _exception;
}