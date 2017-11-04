package cadelac.framework.blade.core.dispatch;

import cadelac.framework.blade.core.message.Dispatchable;
import cadelac.framework.blade.core.state.StateLess;

public class PushStateLessBase<D extends Dispatchable>
	extends PushBase<D,StateLess> {

	public PushStateLessBase(
			final String id_
			, final RoutineStateLess<D> routineStateLess_) {
		super(
				id_
				, (D d, StateLess s) -> { 
					routineStateLess_.routine(d); 
				}
				, m -> StateLess.STATELESS_STATE_ID
				, m -> StateLess.STATELESS_STATE);
	}
}
