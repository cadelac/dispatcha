package cadelac.framework.blade.module.sessionpack.handler;

import cadelac.framework.blade.handler.RequestHandlerBase;
import cadelac.framework.blade.module.sessionpack.SessionStatus;
import cadelac.framework.blade.module.sessionpack.message.SessionCreateMessage;
import cadelac.framework.blade.module.sessionpack.message.SessionCreateResponseMessage;
import cadelac.framework.blade.plug.RequestOutputPlugBase;
import cadelac.lib.primitive.concept.state.StateLess;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.invocation.Response;
import cadelac.lib.primitive.message.AuthenticationMessage;
import cadelac.lib.primitive.message.AuthenticationResponseMessage;
import cadelac.lib.primitive.session.SessionTableState;

public class SessionCreateHandler extends RequestHandlerBase<
											SessionCreateResponseMessage,
											SessionCreateMessage,
											SessionTableState> {
	
	public static final String AUTHENTICATION_OUTPUTPLUG = "authenticationOutputPlug";
	
	
	public SessionCreateHandler() throws ArgumentException {
		super(SessionCreateHandler.class.getSimpleName());
		_sessionHelper = null;
		addOutputPlug(new RequestOutputPlugBase<
							AuthenticationResponseMessage,
							AuthenticationMessage,
							StateLess>(AUTHENTICATION_OUTPUTPLUG, this));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Response<SessionCreateResponseMessage> process(
			final SessionCreateMessage message_, final SessionTableState state_)
			throws Exception {

		// authenticate user id and password
		final AuthenticationMessage authReqMsg = message_.getAuthenticationMessage();
		if (!_sessionHelper.authenticateCredentials(authReqMsg,
				(RequestOutputPlugBase<AuthenticationResponseMessage,AuthenticationMessage,StateLess>)
				this.getOutputPlug(AUTHENTICATION_OUTPUTPLUG))) { // failed, session was not created
			return wrapResponse(_sessionHelper.makeSessionCreateFailureResponse(SessionStatus.CREDENTIALS_NOTVALID));		
		}
		
		// user id is unique
		final String userId = authReqMsg.getAccountName();
		if (!_sessionHelper.userIdIsUnique(userId, state_)) {
			return wrapResponse(_sessionHelper.makeSessionCreateFailureResponse(SessionStatus.USER_ID_NOTUNIQUE));
		}
		
		// session id is unique
		final String sessionStateId = _sessionHelper.createSessionStateId(userId, authReqMsg.getPassword());
		if (!_sessionHelper.sessionIdIsUnique(sessionStateId, state_)) {
			return wrapResponse(_sessionHelper.makeSessionCreateFailureResponse(SessionStatus.SESSION_ID_NOTUNIQUE));
		}
		
		// success
		_sessionHelper.createAndInstallSessionState(userId, sessionStateId, state_);
		return wrapResponse(_sessionHelper.makeSessionCreateSuccessfulResponse(sessionStateId));		
	}

	@Override
	public String getStateId(final SessionCreateMessage message) {
		return _sessionHelper.getSessionTableStateId();
	}

	@Override
	public SessionTableState createState(final SessionCreateMessage message_)
			throws Exception {
		return _sessionHelper.createSessionTableState();
	}

	public void setSessionHelper(final SessionHelper sessionHelper_) {
		_sessionHelper = sessionHelper_;
	}

	
	private /* not final */ SessionHelper _sessionHelper;
}
