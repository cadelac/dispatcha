package cadelac.framework.blade.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import cadelac.framework.blade.core.exception.ArgumentException;

public class CommandSwitch {

	// 01234567810123
	// -KEY=VALUE
	public static final String PREFIX = "-";
	public static final String SEPARATOR = "=";
	public static final int PREFIX_LENGTH = PREFIX.length();
	public static final int SEPARATOR_LENGTH = SEPARATOR.length();
	
	public CommandSwitch() {
		_arguments = new HashMap<String,String>();
	}
	

	public String getArgument(final String argumentName_) {
		if (argumentName_ == null)
			return null;
		return _arguments.get(argumentName_);
	}


	public void setArgument(final String argumentName_, final String argumentValue_) {
		if (argumentName_ != null)
			_arguments.put(argumentName_, argumentValue_);
	}


	public void populate(final String[] arguments_) throws ArgumentException {
		if (arguments_ == null)
			throw new ArgumentException("cannot populate argument: argument is (invalid) null");
		
		for (String arg : arguments_) {
			int index = 0;
			if (arg.startsWith(PREFIX) && (index = arg.indexOf(SEPARATOR)) >= 0) {
				final String key = arg.substring(PREFIX_LENGTH, index);
				final String value = arg.substring(index+SEPARATOR_LENGTH);
				_arguments.put(key, value);
				logger.debug(String.format("added command-line argument: key [%s], value [%s]", key, value));
			}
		}			
	}

	private static final Logger logger = Logger.getLogger(CommandSwitch.class);
	
	private final Map<String,String> _arguments;
}
