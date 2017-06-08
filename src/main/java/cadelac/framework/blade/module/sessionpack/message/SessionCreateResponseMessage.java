package cadelac.framework.blade.module.sessionpack.message;

import cadelac.framework.blade.module.sessionpack.SessionStatus;
import cadelac.lib.primitive.concept.Message;

public interface SessionCreateResponseMessage extends Message {
	public SessionStatus getSessionStatus();
	public void setSessionStatus(SessionStatus sessionStatus);
	
	public boolean getSessionIsCreated();
	public void setSessionIsCreated(boolean sessionIsCreated);
	
	public String getSessionId();
	public void setSessionId(String sessionId);
}
