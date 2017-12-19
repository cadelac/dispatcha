package cadelac.framework.blade.v2.core.dispatch;

@FunctionalInterface
public interface StateLessBlock {
	public void block() throws Exception;
}
