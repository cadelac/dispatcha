package cadelac.framework.blade.core.message;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import javax.json.JsonObjectBuilder;

import cadelac.framework.blade.core.exception.InitializationException;
import cadelac.framework.blade.facility.db.DbCommConnection;

/**
 * Indicates that it can be marshalled
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

}
