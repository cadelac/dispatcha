package cadelac.framework.blade.core.state;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import cadelac.framework.blade.core.IdentifiedBase;

public abstract class StateBase extends IdentifiedBase implements State {
	
	public StateBase(final String id_) {
		super(id_);
		logger.info(String.format("created state [%s]", id_));
	}
	
	private static final Logger logger = LogManager.getLogger(State.class);
}
