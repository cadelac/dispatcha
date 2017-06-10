# dispatcha

## Synopsis

A framework for message-based multi-threaded dispatch using Inversion of Control/Dependency Injection

## Multi-threaded Dispatch

The framework is multi-threaded and automatically dispatches messages to appropriate handlers.

## Message

A Message is defined as an interface composed only of data-member getter and setter methods.

The framework automatically generates the corresponding concrete implementaton-class definition, dynamically compiles, and loads into the running application.

## Handler

When a Message is dispatched, the framework invokes the appropriate Handler, of which, there are two types: a PULL  Handler returns a value, whereas, a PUSH Handler does not.

## Routine

A Routine is a block of code that a PUSH handler executes when the handler is invoked.

## Calculation

A Calculation is a block of that returns a value that a PULL handler executes when it is invoked.

## State

State is a named--has a getId() method--object that contains the data that a Handler operates on. The framework automatically manages the State and injects the State into the Handler when invoked together with the Message. 

The application programmer is freed from the minutiae of managing the State.

## StateIdMapper

A Handler defines a @FunctionalInterface StateIdMapper. This allows the Handler to name the State the Handler will operate on.

	`@FunctionalInterface
public interface StateIdMapper<M extends Message> {
	public String getStateId(final M message);
}
`

## StateCreator

A Handler defines a @FunctionalInterface StateIdMapper. This allows the Handler to specify how State will be created.

	`@FunctionalInterface
public interface StateCreator<M extends Message, S extends State> {
	public S createState(final M message_) throws Exception;
}
`

## Binding a Message to Handler

Dispatch.bind() associates a named Message Type 

## Sending a Message

Dispatch.push() sends a "fire-and-forget" message to a handler

Dispatch.pull() sends a message to a handler and returns a Future

Call get() on the future to retrieve response of type Response<T>

Call getException() on response to get Exception, if any

Call getResponse() on response to get returned value of handler
