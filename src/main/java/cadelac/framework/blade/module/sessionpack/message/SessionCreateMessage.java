package cadelac.framework.blade.module.sessionpack.message;

import cadelac.lib.primitive.concept.Message;
import cadelac.lib.primitive.message.AuthenticationMessage;

public interface SessionCreateMessage extends Message {
	public AuthenticationMessage getAuthenticationMessage();
	public void setAuthenticationMessage(AuthenticationMessage authenticationMessage);
}
