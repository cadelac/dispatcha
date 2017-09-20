package cadelac.framework.blade.core.state;

public class StateLess extends StateBase {

	public static final String    STATELESS_STATE_ID = "STATELESS";
	public static final StateLess STATELESS_STATE = new StateLess(STATELESS_STATE_ID);

	
	public StateLess(final String id_) {
		super(id_);
	}
}
