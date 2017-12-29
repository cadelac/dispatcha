package cadelac.framework.blade.core.message;

import java.sql.ResultSet;

import javax.json.JsonObject;

import cadelac.framework.blade.core.exception.FrameworkException;

/**
 * Indicates that it can be demarshalled
 * @author cadelac
 *
 */
public interface Demarshallable {
	
	public void demarshall(final JsonObject jsonObject_) 
			throws FrameworkException, Exception;
	
	public void demarshall(final ResultSet resultSet_) 
			throws Exception;
}
