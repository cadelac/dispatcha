package cadelac.framework.blade.comm;

/**
 * Used to identify the URL (or address) of a peer in a Comm connection
 * @author cadelac
 *
 */
public interface CommUrl {
	
	/**
	 * @return true if the URL is well-formed
	 */
	public boolean isValid();
}
