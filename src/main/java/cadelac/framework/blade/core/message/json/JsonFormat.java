package cadelac.framework.blade.core.message.json;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.websocket.EncodeException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import cadelac.framework.blade.core.exception.FrameworkException;
import cadelac.framework.blade.core.message.Message;

public class JsonFormat {

	public static boolean willDecode(String message_) {
		try {
			Json.createReader(new StringReader(message_)).read();
			return true;
		}
		catch (JsonException e_) {
			logger.error("Exception: " + e_.getMessage() + "\nStacktrace:\n" + FrameworkException.getStringStackTrace(e_));
			return false;	
		}		
	}

	
	public static <M extends Message> M decode(final M message_, final String jsonText_) 
			throws FrameworkException, Exception {
		final JsonObject jo = Json.createReader(new StringReader(jsonText_)).readObject();
		message_.demarshall(jo);
		return message_;
	}

	
	public static String encode(final Message message_) throws EncodeException {
	
		final String encodedString = JsonFormat.encodeOnly(message_);
		logger.info(String.format(">>> encoded %s: [\n%s\n]", 
				message_.getClass().getSimpleName(), encodedString));
		return encodedString;
	}


	public static String encodeOnly(final Message message_) throws EncodeException {
		final JsonObjectBuilder jbuilder = Json.createObjectBuilder();
		message_.marshall(jbuilder);
		return jbuilder.build().toString();
	}
	
	
	private static final Logger logger = LogManager.getLogger(JsonFormat.class);
}
