package cadelac.framework.blade.module.sessionpack;

public enum SessionStatus {
	SUCCESS("Success"),
	CREDENTIALS_NOTVALID("Credentials not valid"),
	USER_ID_NOTUNIQUE("UserId is not unique"),
	SESSION_ID_NOTUNIQUE("SessionId is not unique");

	private SessionStatus(final String text_) {
		_text = text_;
	}
	
	@Override public String toString() {
		return _text;
	}
	
	private final String _text;
}
