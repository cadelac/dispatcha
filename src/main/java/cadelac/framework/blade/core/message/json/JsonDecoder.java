package cadelac.framework.blade.core.message.json;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.exception.FrameworkException;
import cadelac.framework.blade.core.message.Message;

public class JsonDecoder {

	public static boolean willDecode(String message_) {
		try {
			//Json.createReader(new StringReader(message_)).readObject();
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
	
	private static final Logger logger = Logger.getLogger(JsonDecoder.class);
}
