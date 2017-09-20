package cadelac.framework.blade.facility.db;

@FunctionalInterface
public interface Fabricator<T,R> {
	public R fabricate(T arg) throws Exception;
}
