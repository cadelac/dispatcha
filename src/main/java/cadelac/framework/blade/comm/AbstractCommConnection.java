package cadelac.framework.blade.comm;

import java.io.IOException;

import cadelac.framework.blade.core.IdentifiedBase;
import cadelac.framework.blade.core.exception.FrameworkException;

public abstract class AbstractCommConnection 
	extends IdentifiedBase implements CommConnection {

	public AbstractCommConnection(final String id_) {
		super(id_);
		_isConnected = false;
	}

	@Override
	public abstract void disconnect() throws FrameworkException, IOException;


	@Override
	public boolean getIsConnected() {
		return _isConnected;
	}
	
	protected void setIsConnected(final boolean isConnected_) {
		_isConnected = isConnected_;
	}

	private /* not final */ boolean _isConnected;
}
