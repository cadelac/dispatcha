package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.message.Dispatchable;

@FunctionalInterface
public interface RoutineStateLess<D extends Dispatchable> {
	public void routine(D dispatchable_) throws Exception;
}
