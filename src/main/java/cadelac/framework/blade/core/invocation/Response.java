package cadelac.framework.blade.core.invocation;

public interface Response<T> {
	public T getResponse();
	public Exception getException();
}
