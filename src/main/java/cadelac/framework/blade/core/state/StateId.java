package cadelac.framework.blade.core.state;

import cadelac.framework.blade.core.IdentifiedBase;

public class StateId extends IdentifiedBase {

	public static StateId build(final String stateId_) {
		return new StateId(stateId_);
		
	}
	public StateId(final String stateId_) {
		super(stateId_);
	}
}
