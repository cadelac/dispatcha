package cadelac.framework.blade.core.dispatch;

@FunctionalInterface
public interface StateLessBlock {
	public void block() throws Exception;
}
