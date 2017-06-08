package cadelac.framework.blade.module.sessionpack.handler;

import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.Framework;
import cadelac.framework.blade.core.component.shell.Shell;
import cadelac.framework.blade.core.service.dispatch.Dispatcher;
import cadelac.framework.blade.module.sessionpack.SessionStatus;
import cadelac.framework.blade.module.sessionpack.message.SessionCreateResponseMessage;
import cadelac.framework.blade.plug.RequestOutputPlugBase;
import cadelac.lib.primitive.concept.state.StateLess;
import cadelac.lib.primitive.concept.state.StateManager;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.exception.FrameworkException;
import cadelac.lib.primitive.exception.InitializationException;
import cadelac.lib.primitive.invocation.Response;
import cadelac.lib.primitive.message.AuthenticationMessage;
import cadelac.lib.primitive.message.AuthenticationResponseMessage;
import cadelac.lib.primitive.object.ObjectFactory;
import cadelac.lib.primitive.session.SessionState;
import cadelac.lib.primitive.session.SessionTableState;
import cadelac.lib.primitive.util.MD5Util;

public class SessionHelper {
	public SessionHelper() {
		_shell = Framework.getShell();
		_objectFactory = Framework.getObjectFactory();
	}

	public String getSessionTableStateId() {
		return SessionTableState.STATE_ID;
	}
	public SessionTableState createSessionTableState() {
		return new SessionTableState(SessionTableState.STATE_ID);
	}

	public String createSessionStateId(final String userId_, final String password_) throws NoSuchAlgorithmException {
		final String clearText = "sessionStateId:" + System.currentTimeMillis() + ":" + userId_ + ":" + password_;
		final String sessionStateId = MD5Util.encode(clearText);
		logger.info("clearText ["+clearText+"], sessionStateId ["+sessionStateId+"]");
		return sessionStateId;
	}
	
	public AuthenticationMessage createAuthenticationRequestMessage() 
			throws FrameworkException, Exception {
		final AuthenticationMessage authReqMsg = 
				_objectFactory.fabricate(AuthenticationMessage.class);
		return authReqMsg;
	}
	
	public boolean authenticateCredentials(
			final AuthenticationMessage authReqMsg_,
			final RequestOutputPlugBase<AuthenticationResponseMessage,AuthenticationMessage,StateLess> authOP_) 
					throws FrameworkException, Exception {
		final Future<Response<AuthenticationResponseMessage>> authFuture = authOP_.request(authReqMsg_);
		final AuthenticationResponseMessage authRespMsg = authFuture.get().getResponse();
		return authRespMsg.getAuthenticationResult();
	}
	
	public SessionCreateResponseMessage makeSessionCreateFailureResponse(final SessionStatus sessionStatus_) 
			throws FrameworkException, Exception {
		final SessionCreateResponseMessage response = 
				_objectFactory.fabricate(SessionCreateResponseMessage.class);
		//TODO: add facility in handler to fabricate directly within handler
		response.setSessionStatus(sessionStatus_);
		response.setSessionIsCreated(false);
		response.setSessionId(null);
		return response;
	}
	public SessionCreateResponseMessage makeSessionCreateSuccessfulResponse(final String sessionStateId_) 
			throws FrameworkException, Exception {
		final SessionCreateResponseMessage response = 
				_objectFactory.fabricate(SessionCreateResponseMessage.class);
		response.setSessionStatus(SessionStatus.SUCCESS);
		response.setSessionIsCreated(true);
		response.setSessionId(sessionStateId_);
		return response;
	}
	
	public SessionState createAndInstallSessionState(final String userId_, final String sessionStateId_, final SessionTableState state_) 
			throws ArgumentException, InitializationException, Exception {
		final SessionState sessionState = new SessionState(sessionStateId_, userId_);
		logger.info("sessionState ["+sessionStateId_+"] created");
		final Map<String,SessionState> sessionTableByUserId = state_.getSessionTableByUserId();
		sessionTableByUserId.put(userId_, sessionState);
		logger.info("sessionState ["+sessionStateId_+"] installed in SessionTableByUserId for user ["+userId_+"]");
		final Map<String,SessionState> sessionTableBySessionId = state_.getSessionTableBySessionId();
		sessionTableBySessionId.put(sessionStateId_, sessionState);
		logger.info("sessionState ["+sessionStateId_+"] installed in SessionTableBySessionId for user ["+sessionStateId_+"]");
		StateManager.installState(sessionState);
		return sessionState;
	}
	public void removeSessionState(final String sessionStateId_, final SessionTableState state_) {
		final Map<String,SessionState> sessionTableBySessionId = state_.getSessionTableBySessionId();
		final SessionState sessionState = sessionTableBySessionId.get(sessionStateId_);
		if (sessionState==null) {
			logger.info("not found: cannot remove sessionState ["+sessionStateId_+"] from  ["+state_.getId()+"]");
			return;
		}
		sessionTableBySessionId.remove(sessionStateId_);
		final Map<String,SessionState> sessionTableByUserId = state_.getSessionTableByUserId();
		final String userId = sessionState.getUserId();
		sessionTableByUserId.remove(userId);
		logger.info("removed sessionState ["+sessionStateId_+"] from  ["+state_.getId()+"]");
	}

	
	public boolean userIdIsUnique(final String userId_, final SessionTableState state_) {
		return ! state_.getSessionTableByUserId().containsKey(userId_);
	}
	public boolean sessionIdIsUnique(final String sessionId_, final SessionTableState state_) {
		return ! state_.getSessionTableBySessionId().containsKey(sessionId_);
	}

	private static final Logger logger = Logger.getLogger(SessionHelper.class);
	
	private final Shell _shell;
	private final ObjectFactory _objectFactory;
}
