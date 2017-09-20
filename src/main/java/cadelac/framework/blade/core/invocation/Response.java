package cadelac.framework.blade.core.invocation;

public interface Response<T> {
	
	public T getResponse();
	public void setResponse(final T response_);
	
	public Exception getException();
	public void setException(final Exception exception_);
}
