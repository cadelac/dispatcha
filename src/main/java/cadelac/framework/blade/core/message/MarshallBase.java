package cadelac.framework.blade.core.message;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.apache.log4j.Logger;

import cadelac.framework.blade.Framework;
import cadelac.framework.blade.core.code.annotation.ColumnName;
import cadelac.framework.blade.core.code.annotation.FlattenAs;
import cadelac.framework.blade.core.code.annotation.InflateAs;
import cadelac.framework.blade.core.code.annotation.MarshallNo;
import cadelac.framework.blade.core.code.annotation.TableName;
import cadelac.framework.blade.core.exception.FrameworkException;
import cadelac.framework.blade.core.exception.InitializationException;
import cadelac.framework.blade.core.exception.JsonMessageException;
import cadelac.framework.blade.facility.db.DbCommConnection;

public abstract class MarshallBase implements Marshallable {

	// flatten
	public void marshall(final JsonObjectBuilder jbuilder) {
		final Class<?> aClass = this.getClass();
		for (Field field : aClass.getDeclaredFields()) {
			final MarshallNo skipMarshall = 
					(MarshallNo) field.getAnnotation(MarshallNo.class);
			if (skipMarshall==null) {
				// annotation not found => we go ahead and marshall the field...
				//getter
				final FlattenAs flatten = field.getAnnotation(FlattenAs.class);
				final String fieldName = 
						(flatten==null) ? field.getName() : flatten.value();
				final Class<?> fieldType = field.getType();
				try {
					if (fieldType.isPrimitive()) {
						if (fieldType == Integer.TYPE)
							jbuilder.add(fieldName, (Integer)getField(field));
						else if (fieldType == Long.TYPE)
							jbuilder.add(fieldName, (Long)getField(field));
						else if (fieldType == Double.TYPE)
							jbuilder.add(fieldName, (Double)getField(field));
						else if (fieldType == Boolean.TYPE)
							jbuilder.add(fieldName, (Boolean)getField(field));
					}
					else if (fieldType == String.class) {
						final String value = (String)getField(field);
						if (value != null)
							jbuilder.add(fieldName, value);
					}
					else if (fieldType.isArray()) {
						if (fieldType.getComponentType() == String.class) {
							marshallStringArray(field, jbuilder);
						}
						else if (fieldType.getComponentType() == Integer.class) {
							marshallIntegerArray(field, jbuilder);
						}
						else { // assume it is an array of some arbitrary kind of object
							marshallObjectArray(field, jbuilder);
						}
					}
					else { 
						marshallObject(field, jbuilder);
					}
			    }
				catch (IllegalArgumentException 
						| IllegalAccessException 
						| NoSuchMethodException 
						| SecurityException 
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			else
				logger.debug(
						String.format(
								"MarshallNo Annotation found: marshalling %s.%s, skipped"
								, aClass.getSimpleName()
								, field.getName()));
	    }
	}

	public void marshall(final DbCommConnection connection_) 
			throws InitializationException
			, SQLException
			, NoSuchMethodException
			, SecurityException
			, IllegalAccessException
			, IllegalArgumentException
			, InvocationTargetException {
		
		final Class<?> aClass = this.getClass();
		
		final TableName tableName = aClass.getAnnotation(TableName.class);
		if (tableName == null) {
			throw new InitializationException(
					String.format(
							"Message %s does not have @TableName annotation"
							, aClass.getSimpleName()));
		}

		// comma-separated list of column names
		final StringBuilder columnNames = new StringBuilder(); 
		// comma-separated list of place holders
		final StringBuilder placeHolders = new StringBuilder(); 
		
		// for preserving correct order of columns...
		final List<Field> columnNamedFields = getColumnNamedFields(aClass); 
		for (Field field : columnNamedFields) {
			final ColumnName columnName = 
					(ColumnName) field.getAnnotation(ColumnName.class);
			if (columnNames.length()>0) {
				columnNames.append(", ");
				placeHolders.append(", ");
			}
			columnNames.append(columnName.value());
			placeHolders.append("?");
		}
		
		// now we have an ordered-list of fields with ColumnName Annotations...
		
		final String tableNameValue = tableName.value();
		logger.debug("table name is " + tableNameValue);
		
		// build the insert query string
		final StringBuilder queryStringBuilder = new StringBuilder(
				String.format("INSERT INTO %s (%s) VALUES (%s)"
						, tableNameValue
						, columnNames.toString()
						, placeHolders.toString()));
		
		// bind values to parameters (placeholder)
		
		final String queryString = queryStringBuilder.toString();
		final PreparedStatement preparedStatement = 
				connection_.getConnection().prepareStatement(queryString);
		int i = 1;
		for (Field field : columnNamedFields) {
			final Method getMethod = 
					aClass.getDeclaredMethod("get" + field.getName());
			if (field.getType() == Integer.TYPE){
				final int value = (int) getMethod.invoke(this);
				preparedStatement.setInt(i, value);
			}
			if (field.getType() == Long.TYPE){
				final long value = (long) getMethod.invoke(this);
				preparedStatement.setLong(i, value);
			}
			else if (field.getType() == Double.TYPE){
				final double value = (double) getMethod.invoke(this);
				preparedStatement.setDouble(i, value);
			}
			else if (field.getType() == String.class){
				final String value = (String) getMethod.invoke(this);
				preparedStatement.setString(i, value);
			}
			else if (field.getType() == Boolean.TYPE){
				final Boolean value = (Boolean) getMethod.invoke(this);
				preparedStatement.setBoolean(i, value);
			}
			++i;
		}

		preparedStatement.executeUpdate();
	}



	public void demarshall(final JsonObject jo_) 
			throws Exception, JsonMessageException {
		final Class<?> aClass = this.getClass();
		for (Field field : aClass.getDeclaredFields()) {
			final MarshallNo skipMarshall = 
					(MarshallNo) field.getAnnotation(MarshallNo.class);
			if (skipMarshall==null) {
				// setter
				final InflateAs inflate = field.getAnnotation(InflateAs.class);
				final String fieldName = 
						(inflate==null) ? field.getName() : inflate.value();
				final Class<?> fieldType = field.getType();
				try {
					if (fieldType.isPrimitive()) {
						if (fieldType == Integer.TYPE) {
							if (jo_.containsKey(fieldName)){
								setField(field, jo_.getInt(fieldName));
							}
						}
						else if (fieldType == Long.TYPE) {
							if (jo_.containsKey(fieldName)){
								setField(field, jo_.getJsonNumber(fieldName).longValue());
							}
						}
						else if (fieldType == Double.TYPE) {
							if (jo_.containsKey(fieldName)){
								setField(field, jo_.getJsonNumber(fieldName).doubleValue());
							}
						}
						else if (fieldType == Boolean.TYPE) {
							if (jo_.containsKey(fieldName)){
								setField(field, jo_.getBoolean(fieldName));
							}
						}
					}
					else if (fieldType == String.class) {
						if (jo_.containsKey(fieldName) && !jo_.isNull(fieldName))
							setField(field, jo_.getString(fieldName));
					}
					else if (fieldType.isArray()) {
						if (jo_.containsKey(fieldName) && !jo_.isNull(fieldName)) {
							if (fieldType.getComponentType() == String.class) {
								unmarshallStringArray(field, jo_);
							}
							else if (fieldType.getComponentType() == Integer.class) {
								unmarshallIntegerArray(field, jo_);
							}
							else { // assume it is an array of some arbitrary kind of object
								unmarshallObjectArray(field, jo_);
							}
						}
					}
					else {
						if (jo_.containsKey(fieldName) && !jo_.isNull(fieldName)) {
							unmarshallObject(field, jo_);
						}
					}
				}
				catch (IllegalArgumentException 
						| IllegalAccessException 
						| InstantiationException e) {
					e.printStackTrace();
				}
				catch (JsonMessageException j) {
					throw new JsonMessageException(
							String.format(
									"field: %s: %s"
									, field.getName()
									, j.getMessage())); 
				}
				catch (Exception e) {
					throw new Exception(
							String.format(
									"field: %s: %s"
									, field.getName()
									, e.getMessage())); 
				}
			}
			else
				logger.debug(String.format(
						"@MarshallNo found: demarshalling %s.%s, skipped"
						, aClass.getSimpleName()
						, field.getName()));
		}
	}

	
	public void demarshall(final ResultSet resultSet_) 
			throws Exception {
		final Class<?> aClass = this.getClass();

		// for preserving correct order of columns...
		final List<Field> columnNamedFields = 
				getColumnNamedFields(aClass);
		
		for (Field field : columnNamedFields) {
			final ColumnName columnNameAnnotation = 
					(ColumnName) field.getAnnotation(ColumnName.class);
			// acquire "set" method
			
			if (field.getType() == Integer.TYPE){
				final Method setMethod = 
						aClass.getDeclaredMethod(
								"set" + field.getName(), Integer.TYPE);
				setMethod.invoke(
						this
						, resultSet_.getInt(columnNameAnnotation.value()));
			}
			if (field.getType() == Long.TYPE){
				final Method setMethod = 
						aClass.getDeclaredMethod(
								"set" + field.getName(), Long.TYPE);
				setMethod.invoke(
						this
						, resultSet_.getLong(columnNameAnnotation.value()));
			}
			else if (field.getType() == Double.TYPE){
				final Method setMethod = 
						aClass.getDeclaredMethod(
								"set" + field.getName(), Double.TYPE);
				setMethod.invoke(
						this
						, resultSet_.getDouble(columnNameAnnotation.value()));
			}
			else if (field.getType() == String.class){
				final Method setMethod = 
						aClass.getDeclaredMethod(
								"set" + field.getName(), String.class);
				setMethod.invoke(
						this
						, resultSet_.getString(columnNameAnnotation.value()));
			}
			else if (field.getType() == Boolean.TYPE){
				final Method setMethod = 
						aClass.getDeclaredMethod(
								"set" + field.getName(), Boolean.class);
				setMethod.invoke(
						this
						, resultSet_.getBoolean(columnNameAnnotation.value()));
			}
		}
	}
	
	
	// implemented in the subclass because this class cannot access subclass' private data members
	protected abstract Object getField(final Field field_) 
			throws IllegalArgumentException, IllegalAccessException;
	protected abstract void setField(final Field field_, final Object msg_) 
			throws JsonMessageException, IllegalAccessException;
	
	
	// return a list of fields with column-names
	private List<Field> getColumnNamedFields(final Class<?> aClass) {
		final List<Field> orderedFields = new ArrayList<Field>();
		for (Field field : aClass.getDeclaredFields()) {
			final Annotation columnNameAnnotation = field.getAnnotation(ColumnName.class);
			if (columnNameAnnotation != null && columnNameAnnotation instanceof ColumnName) {
				orderedFields.add(field);
			}
		}
		return orderedFields;
	}
	
	private void marshallStringArray(final Field field, final JsonObjectBuilder jbuilder) 
			throws IllegalArgumentException, IllegalAccessException {
		  final String[] strings = (String[]) getField(field);
		  if (strings != null) {
			  final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
			  for (String str : strings)
				  arrayBuilder.add(Json.createObjectBuilder().add(STRING_ARRAY_ELEMENT, str));
			  jbuilder.add(field.getName(), arrayBuilder);
		  }		
	}
	
	private void marshallIntegerArray(final Field field, final JsonObjectBuilder jbuilder) 
			throws IllegalArgumentException, IllegalAccessException {
		  final Integer[] series = (Integer[]) getField(field);
		  if (series != null) {
			  final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
			  for (Integer s : series)
				  arrayBuilder.add(Json.createObjectBuilder().add(INTEGER_ARRAY_ELEMENT, s));
			  jbuilder.add(field.getName(), arrayBuilder);
		  }		
	}
	
	
	private void marshallObjectArray(Field field, final JsonObjectBuilder jbuilder) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		  final Object[] objects = (Object[]) getField(field);
		  if (objects != null) {
			  final Class<?> fieldType = field.getType();
			  final String elementName = fieldType.getComponentType().getSimpleName();
			  final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
			  for (Object object : objects) {
				  final JsonObjectBuilder jb2 = Json.createObjectBuilder();
				  marshallObject2(elementName, object, jb2);
				  arrayBuilder.add(jb2);
			  }
			  jbuilder.add(field.getName(), arrayBuilder);
		  }			
	}
	
	private void marshallObject(Field field, final JsonObjectBuilder jbuilder) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		  final Object obj = getField(field);
		  final String fieldName = field.getName();
		  marshallObject2(fieldName, obj, jbuilder);
	}
	
	private void marshallObject2(final String fieldName, final Object obj, final JsonObjectBuilder jbuilder) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException {
		  if (obj != null) {
			  final Class<?> mclass = obj.getClass();
			  try {
				  final Method method = mclass.getMethod("marshall", JsonObjectBuilder.class);
				  if (method != null) {
					  // marshall it only if we know how to, i.e. method is defined
					  final JsonObjectBuilder jb = Json.createObjectBuilder();
					  method.invoke(obj, jb);
					  jbuilder.add(fieldName, jb);
				  }				  
			  }
			  catch (NoSuchMethodException e) {
				  // we don't know how to marshall it... let's just skip it.
			  }
		  }		
	}
	
	
	private void unmarshallStringArray(final Field field, final JsonObject jo_) 
			throws JsonMessageException, IllegalAccessException {
		final JsonArray jarray = jo_.getJsonArray(field.getName());
		final String[] strings = new String[jarray.size()];
		for (int i=0; i<jarray.size(); ++i) {
			JsonObject job = jarray.getJsonObject(i);
			strings[i] = job.getString(STRING_ARRAY_ELEMENT);
		}
		setField(field, strings);	
	}
	
	private void unmarshallIntegerArray(final Field field, final JsonObject jo_) 
			throws JsonMessageException, IllegalAccessException {
		final JsonArray jarray = jo_.getJsonArray(field.getName());
		final Integer[] series = new Integer[jarray.size()];
		for (int i=0; i<jarray.size(); ++i) {
			JsonObject job = jarray.getJsonObject(i);
			series[i] = job.getInt(INTEGER_ARRAY_ELEMENT); //.getString(INTEGER_ARRAY_ELEMENT);
		}
		setField(field, series);	
	}
	
	@SuppressWarnings("unchecked")
	private void unmarshallObjectArray(final Field field, final JsonObject jo_) 
			throws FrameworkException, Exception {
		final JsonArray jarray = jo_.getJsonArray(field.getName());

		final Object myArray = 
				Array.newInstance(
						field.getType().getComponentType()
						, jarray.size());

		final Class<?> fieldType = field.getType();
		final String elementName = fieldType.getComponentType().getSimpleName();
		for (int i=0; i<jarray.size(); ++i) {
			JsonObject jobi = jarray.getJsonObject(i);
			JsonObject job = jobi.getJsonObject(elementName );
			Array.set(myArray, i, Framework.getObjectFactory().fabricate(
					(Class<? extends Message>) fieldType.getComponentType()
					, p -> { 
						p.demarshall(job); 
					}
			));
		}
		setField(field, myArray);	
	}
	
	private void unmarshallObject(final Field field, final JsonObject jo_) 
			throws FrameworkException, Exception {
		if (jo_.containsKey(field.getName()) && !jo_.isNull(field.getName())) {
			final JsonObject job = jo_.getJsonObject(field.getName());
			@SuppressWarnings("unchecked")
			final Class<? extends Message> fType =  
				(Class<? extends Message>) field.getType();
			setField(field, Framework.getObjectFactory().fabricate(
					fType
					, msg -> { 
						msg.demarshall(job); 
					}
			));
		}		
	}
	
	private static final String INTEGER_ARRAY_ELEMENT = "e";
	private static final String STRING_ARRAY_ELEMENT = "e";
	
	private static final Logger logger = Logger.getLogger(MarshallBase.class);
}
