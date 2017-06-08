package cadelac.framework.blade.module.sessionpack.message;

import cadelac.lib.primitive.concept.Message;

public interface SessionDestroyMessage extends Message {
	public String getSessionId();
	public void setSessionId(String sessionId);
}
