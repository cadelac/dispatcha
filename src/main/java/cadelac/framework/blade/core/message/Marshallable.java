package cadelac.framework.blade.core.message;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import cadelac.framework.blade.core.exception.FrameworkException;
import cadelac.framework.blade.core.exception.InitializationException;
import cadelac.framework.blade.facility.db.DbCommConnection;

/**
 * Indicates that it can be marshalled and demarshalled.
 * @author cadelac
 *
 */
public interface Marshallable {
	
	public void marshall(final JsonObjectBuilder jsonBuilder_);
	
	public void marshall(final DbCommConnection connection_) 
			throws InitializationException
			, SQLException
			, NoSuchMethodException
			, SecurityException
			, IllegalAccessException
			, IllegalArgumentException
			, InvocationTargetException;
	
	public void demarshall(final JsonObject jsonObject_) 
			throws FrameworkException, Exception;
	
	public void demarshall(final ResultSet resultSet_) 
			throws Exception;
}
