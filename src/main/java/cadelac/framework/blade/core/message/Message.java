package cadelac.framework.blade.core.message;

/**
 * The framework is message based. A message is passed between handlers.
 * @author cadelac
 *
 */
public interface Message 
	extends Marshallable
	, Demarshallable
	, Deliverable
	, Generated {
}
