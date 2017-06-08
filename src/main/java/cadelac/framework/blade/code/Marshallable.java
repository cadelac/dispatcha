package cadelac.framework.blade.code;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.lang.NoSuchMethodException;
import java.lang.annotation.Annotation;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import cadelac.framework.blade.core.Framework;
import cadelac.lib.primitive.concept.ColumnName;
import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.exception.FrameworkException;
import cadelac.lib.primitive.exception.JsonMessageException;

public abstract class Marshallable {

	// implemented on the subclass because this class cannot access subclass' private data members
	protected abstract Object getField(final Field field_) throws IllegalArgumentException, IllegalAccessException;
	protected abstract void setField(final Field field_, final Object msg_) throws JsonMessageException, IllegalAccessException;

	public void marshall(final JsonObjectBuilder jbuilder) {
		  final Class<?> aClass = this.getClass();
		  for (Field field : aClass.getDeclaredFields()) {
			  Class<?> fieldType = field.getType();
			  try {
				  if (fieldType.isPrimitive()) {
					  if (fieldType == Integer.TYPE)
						  jbuilder.add(field.getName(), (Integer)getField(field));
					  else if (fieldType == Long.TYPE)
						  jbuilder.add(field.getName(), (Long)getField(field));
					  else if (fieldType == Double.TYPE)
						  jbuilder.add(field.getName(), (Double)getField(field));
					  else if (fieldType == Boolean.TYPE)
						  jbuilder.add(field.getName(), (Boolean)getField(field));
				  }
				  else if (fieldType == String.class) {
					  final String value = (String)getField(field);
					  if (value != null)
						  jbuilder.add(field.getName(), value);
				  }
				  else if (fieldType.isArray()) {
					  if (fieldType.getComponentType() == String.class) {
						  mashStringArray(field, jbuilder);
					  }
					  else { // assume it is an array of some arbitrary kind of object
						  mashObjectArray(field, jbuilder);
					  }
				  }
				  else {
					  mashObject(field, jbuilder);
				  }
		       }
			  catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
				  e.printStackTrace();
			  }
	     }
    }

	private void mashObjectArray(Field field, final JsonObjectBuilder jbuilder) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		  final Object[] objects = (Object[]) getField(field); //field.get(this);
		  if (objects != null) {
			  Class<?> fieldType = field.getType();
			  final String elementName = fieldType.getComponentType().getSimpleName();
			  final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
			  for (Object object : objects) {
				  final JsonObjectBuilder jb2 = Json.createObjectBuilder();
				  mashObject2(elementName, object, jb2);
				  arrayBuilder.add(jb2);
			  }
			  jbuilder.add(field.getName(), arrayBuilder);
		  }			
	}

	
	// marshall String[]
	private void mashStringArray(Field field, final JsonObjectBuilder jbuilder) throws IllegalArgumentException, IllegalAccessException {
		  final String[] strings = (String[]) getField(field); //field.get(this);
		  if (strings != null) {
			  final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
			  for (String str : strings)
				  arrayBuilder.add(Json.createObjectBuilder().add(STRING_ARRAY_ELEMENT, str));
			  jbuilder.add(field.getName(), arrayBuilder);
		  }		
	}
	
	private void mashObject(Field field, final JsonObjectBuilder jbuilder) 
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		  final Object obj = getField(field); //field.get(this);
		  final String fieldName = field.getName();
		  mashObject2(fieldName, obj, jbuilder);
	}
	
	private void mashObject2(final String fieldName, final Object obj, final JsonObjectBuilder jbuilder) 
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
	
	public void demarshall(final JsonObject jo_) throws Exception, JsonMessageException {
		final Class<?> aClass = this.getClass();
		for (Field field : aClass.getDeclaredFields()) {
			final Class<?> fieldType = field.getType();
			final String fieldName = field.getName();
			try {
				if (fieldType.isPrimitive()) {
					if (fieldType == Integer.TYPE) {
						if (jo_.containsKey(fieldName)){
							setField(field, jo_.getInt(fieldName));//field.setInt(this, jo_.getInt(fieldName));
						}
					}
						
					else if (fieldType == Long.TYPE) {
						if (jo_.containsKey(fieldName)){
							setField(field, jo_.getJsonNumber(fieldName).longValue());//field.setLong(this, jo_.getJsonNumber(fieldName).longValue());
						}
					}
					else if (fieldType == Double.TYPE) {
						if (jo_.containsKey(fieldName)){
							setField(field, jo_.getJsonNumber(fieldName).doubleValue());//field.setLong(this, jo_.getJsonNumber(fieldName).longValue());
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
						setField(field, jo_.getString(fieldName)); //field.set(this, jo_.getString(fieldName));
				}
				else if (fieldType.isArray()) {
					if (jo_.containsKey(fieldName) && !jo_.isNull(fieldName)) {
						if (fieldType.getComponentType() == String.class) {
							unmashStringArray(field, jo_);
						}
						else { // assume it is an array of some arbitrary kind of object
							unmashObjectArray(field, jo_);
						}
					}
				}
				else {
					unmashObject(field, jo_);
				}
			}
			catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
				e.printStackTrace();
			}
			catch (JsonMessageException j) {
				throw new JsonMessageException(String.format("field: %s: %s", field.getName(), j.getMessage())); 
			}
			catch (Exception e) {
				throw new Exception(String.format("field: %s: %s", field.getName(), e.getMessage())); 
			}
		}
	}
	
	public void demarshall(final ResultSet resultSet_) throws Exception {
		final Class<?> aClass = this.getClass();
		for (Method method : aClass.getMethods()) {
			if (method.getName().startsWith("set")) {
				// only set methods have Column annotations
				Annotation[] annotations = method.getAnnotations(); //.getDeclaredAnnotations();
				for (Annotation annotation : annotations) {
					if(annotation instanceof ColumnName){
						final ColumnName columnName = (ColumnName) annotation;
						final Class<?>[] formal_parameters = method.getParameterTypes();
						// we expect only one parameter
						if (formal_parameters[0] == Integer.TYPE) {
							method.invoke(this, resultSet_.getInt(columnName.value()));
						}
						else if (formal_parameters[0] == String.class) {
							method.invoke(this, resultSet_.getString(columnName.value()));
						}
					}
				}
			}
		}
	}
	
	private void unmashStringArray(final Field field, final JsonObject jo_) throws JsonMessageException, IllegalAccessException {
		final JsonArray jarray = jo_.getJsonArray(field.getName());
		final String[] strings = new String[jarray.size()];
		for (int i=0; i<jarray.size(); ++i) {
			JsonObject job = jarray.getJsonObject(i);
			strings[i] = job.getString(STRING_ARRAY_ELEMENT);
		}
		setField(field, strings);	
	}
	
	@SuppressWarnings("unchecked")
	private void unmashObjectArray(final Field field, final JsonObject jo_) throws FrameworkException, Exception {
		final JsonArray jarray = jo_.getJsonArray(field.getName());

		Object myArray = Array.newInstance(field.getType().getComponentType(), jarray.size());

		Class<?> fieldType = field.getType();
		final String elementName = fieldType.getComponentType().getSimpleName();
		for (int i=0; i<jarray.size(); ++i) {
			JsonObject jobi = jarray.getJsonObject(i);
			JsonObject job = jobi.getJsonObject(elementName );
			//unmashObject3(myArray, i, (Class<? extends Message>) fieldType.getComponentType(), job);
			Array.set(myArray, i, Framework.getObjectFactory().fabricate(
					(Class<? extends Message>) fieldType.getComponentType()
					, p -> { 
						p.demarshall(job); 
					}
			));
		}
		setField(field, myArray);	
	}
	
	private void unmashObject(final Field field, final JsonObject jo_) 
			throws FrameworkException, Exception {
		final JsonObject job = jo_.getJsonObject(field.getName());
		if (job != null) {
			@SuppressWarnings("unchecked")
			final Class<? extends Message> fType =  (Class<? extends Message>) field.getType();
			setField(field, Framework.getObjectFactory().fabricate(
					fType
					, msg -> { 
						msg.demarshall(job); 
					}
			));
		}		
	}

	
	private static final String STRING_ARRAY_ELEMENT = "mashStringArray-elem";
}
