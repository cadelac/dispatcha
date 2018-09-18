package cadelac.framework.blade.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import cadelac.framework.blade.core.exception.ArgumentException;

public class CommandSwitch {

	// 01234567810123
	// -KEY=VALUE
	public static final String PREFIX = "-";
	public static final String SEPARATOR = "=";
	public static final int PREFIX_LENGTH = PREFIX.length();
	public static final int SEPARATOR_LENGTH = SEPARATOR.length();
	
	public CommandSwitch() {
		_map = new HashMap<String,String>();
	}
	

	public String getArgument(final String argumentName_) {
		if (argumentName_ == null)
			return null;
		return _map.get(argumentName_);
	}


	public void setArgument(final String argumentName_, final String argumentValue_) {
		if (argumentName_ != null)
			_map.put(argumentName_, argumentValue_);
	}

	public String[] getArguments() {
		return _arguments;
	}

	public void populate(final String[] arguments_) throws ArgumentException {
		if (arguments_ == null)
			throw new ArgumentException("cannot populate argument: argument is (invalid) null");
		
		_arguments = arguments_;
		for (String arg : arguments_) {
			int index = 0;
			if (arg.startsWith(PREFIX) && (index = arg.indexOf(SEPARATOR)) >= 0) {
				final String key = arg.substring(PREFIX_LENGTH, index);
				final String value = arg.substring(index+SEPARATOR_LENGTH);
				_map.put(key, value);
				logger.debug(String.format("added command-line argument: key [%s], value [%s]", key, value));
			}
		}			
	}

	private static final Logger logger = LogManager.getLogger(CommandSwitch.class);
	
	private String[] _arguments;
	private final Map<String,String> _map;
}
