package cadelac.framework.blade.module.sessionpack.handler;

import cadelac.framework.blade.handler.SendHandlerBase;
import cadelac.framework.blade.module.sessionpack.message.SessionDestroyMessage;
import cadelac.lib.primitive.session.SessionTableState;

public class SessionDestroyHandler extends SendHandlerBase<SessionDestroyMessage,SessionTableState> {

	public SessionDestroyHandler() {
		super(SessionDestroyHandler.class.getSimpleName());
		_sessionHelper = null;
	}

	@Override
	public void process(final SessionDestroyMessage message_, final SessionTableState state_) 
			throws Exception {
		_sessionHelper.removeSessionState(message_.getSessionId(), state_);
	}

	@Override
	public String getStateId(final SessionDestroyMessage message) {
		return _sessionHelper.getSessionTableStateId();
	}

	@Override
	public SessionTableState createState(final SessionDestroyMessage message_)
			throws Exception {
		return _sessionHelper.createSessionTableState();
	}

	public void setSessionHelper(final SessionHelper sessionHelper_) {
		_sessionHelper = sessionHelper_;
	}

	
	private /* not final */ SessionHelper _sessionHelper;
}
