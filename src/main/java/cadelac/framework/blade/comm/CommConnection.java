package cadelac.framework.blade.comm;

import java.io.IOException;

import cadelac.framework.blade.core.Identified;
import cadelac.framework.blade.core.exception.FrameworkException;


public interface CommConnection extends Identified {	
	public void disconnect() throws FrameworkException, IOException;
	public boolean getIsConnected();
}
