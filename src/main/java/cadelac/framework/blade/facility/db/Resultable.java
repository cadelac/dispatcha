package cadelac.framework.blade.facility.db;

import java.util.List;
/**
 * Rows of SQL results can be extracted.
 * @author cadelac
 *
 * @param <R>
 */
public interface Resultable<R> {
	public List<R> getResults() throws Exception;
}
