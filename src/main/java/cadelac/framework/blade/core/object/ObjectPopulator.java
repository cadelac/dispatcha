package cadelac.framework.blade.core.object;

@FunctionalInterface
public interface ObjectPopulator<T> {
	public void populate(T object_) throws Exception;
}
