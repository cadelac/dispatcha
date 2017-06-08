package cadelac.framework.blade.comm;

import cadelac.lib.primitive.comm.CommCredentials;
import cadelac.lib.primitive.comm.CommUrl;

public interface CommInitParams  {
	public CommUrl getCommUrl();
	public CommCredentials getCommCredentials();
}
