package cadelac.framework.blade.core;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log {
	
	public static void configure() {
		BasicConfigurator.configure();
		logger.info("configured logger with basic configuration");
	}
	
	public static void configure(final String configFullPath_) {
		PropertyConfigurator.configure(configFullPath_);
		logger.info(String.format("configured logger with: %s", configFullPath_));
	}
	
	private static final Logger logger = Logger.getLogger(Log.class);
}
