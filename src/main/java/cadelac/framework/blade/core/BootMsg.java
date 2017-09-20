package cadelac.framework.blade.core;

import cadelac.framework.blade.core.message.Message;

/**
 * Used to start the scheduler and prevents application from exiting.
 * @author cadelac
 *
 */
public interface BootMsg extends Message {
	
	public static final String OPERATION = "boot-time";
	
	long getBootTime();
	void setBootTime(long bootTime);
}
