package cadelac.framework.blade.core.message;

import cadelac.framework.blade.v2.core.dispatch.Deliverable;

/**
 * The framework is message based. A message is passed between handlers.
 * @author cadelac
 *
 */
public interface Message 
	extends Marshallable
	, Dispatchable
	, Deliverable
	, Generated {
}
