package cadelac.framework.blade.core.message.json;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.websocket.EncodeException;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.message.Message;

public class JsonEncoder {
	
	public static String encode(final Message message_) throws EncodeException {

		final String encodedString = encodeOnly(message_);
		//logger.info(String.format(">>> encoded %s: [\n%s\n]", 
		//		message_.getClass().getSimpleName(), encodedString));
		return encodedString;
	}
	
	public static String encodeOnly(final Message message_) throws EncodeException {
		final JsonObjectBuilder jbuilder = Json.createObjectBuilder();
		message_.marshall(jbuilder);
		return jbuilder.build().toString();
	}
	
	
	//private static final Logger logger = Logger.getLogger(JsonEncoder.class);
}
