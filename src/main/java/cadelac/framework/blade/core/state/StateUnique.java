package cadelac.framework.blade.core.state;

/**
 * Used to indicate that a new unique state must be created.
 * @author cadelac
 *
 */
public class StateUnique {
	
	public static String getUniqueStateId() {
		return STATE_UNIQUE_ID;
	}

	private static final String STATE_UNIQUE_ID = "STATE_UNIQUE";
}
