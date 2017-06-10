## Synopsis

A framework for message-based multi-threaded dispatch using Inversion of Control/Dependency Injection

## Multi-threaded Dispatch

The framework is multi-threaded and automatically dispatches messages to appropriate handlers.

## Message

A Message is defined as an interface composed only of getter and setter methods (Java Bean).

The framework automatically generates the corresponding concrete implementaton-class definition, dynamically compiles, and loads into the running application.

A Message is intended to be an immutable object and should not be changed by Handlers. This allows a distinct Message to be passed to multiple Handlers without unintended side effects.

Define a Message:

`public interface SessionIdCheck extends Message {
	public ApiEnvelope getApiEnvelope();
	public void setApiEnvelope(ApiEnvelope apiEnvelope);
}`

## Object Factory

Create a Message:

`final SessionIdCheck sessionIdCheck = Framework.getObjectFactory().fabricate(
       SessionIdCheck.class
       , p -> {
       	 p.setApiEnvelope(apiEnvelopeClientRequest_);
       }
);`

The Object Factory automatically generates the code for the concrete class, compiles and loads it onto the running application.

## Handler

When a Message is dispatched, the framework invokes the appropriate Handler, of which, there are two types: a PULL  Handler returns a value, whereas, a PUSH Handler does not.

A PUSH Handler is used for "fire-and-forget" messages where the sender does not need a return value.

A PULL Handler should be used when the sender needs to get a return value.

A Handler should not contain any data.

## State

State is a named--has a getId() method--object that contains the data that a Handler operates on. The framework automatically manages the State and injects the State into the Handler when invoked together with the Message. 

The application programmer is freed from the minutiae of managing the State.

The State does not contain any business logic. It is merely a container of data.

## Routine

A Routine is a block of code that a PUSH handler executes when the handler is invoked.

`public interface Routine<M extends Message,S extends State> {
	public void routine(M msg_, S state_) throws Exception;
}`

## Calculation

A Calculation is a block of that returns a value that a PULL handler executes when it is invoked.

`public interface Calculation<R,M extends Message,S extends State> {
	public R calculate(M msg_, S state_) throws Exception;
}`

## StateIdMapper

A Handler defines a StateIdMapper. This allows the Handler to name the State the Handler will operate on.

`@FunctionalInterface
public interface StateIdMapper<M extends Message> {
	public String getStateId(final M message);
}`

## StateCreator

A Handler defines a StateIdMapper. This allows the Handler to specify how State will be created.

`@FunctionalInterface
public interface StateCreator<M extends Message, S extends State> {
	public S createState(final M message_) throws Exception;
}`

## Creating a PUSH Handler

A PUSH Handler is created by instantiating

`PushBase<M extends Message,S extends State>`

like so:

`final Push<GetQuestionsMsg,QuestionManagerState> push = new PushBase<GetQuestionsMsg,QuestionManagerState>(
	GetQuestionsMsg.OPERATION // id
	, (GetQuestionsMsg msg, QuestionManagerState state) -> {
		// perform processing here...
	}
	, m -> {
		return buildQuestionManagerStateId((LoginMsg) m.getSessionRecord().getApiMsg());
	}
	, m -> { 
		new QuestionManagerState(buildQuestionManagerStateId((LoginMsg) m.getSessionRecord().getApiMsg());
	}
)`

## Creating a PULL Handler

A PULL Handler is created by instantiating

`Pull<R,M extends Message,S extends State>`

like so:

`final Pull<ApiEnvelope,GetQuestionsMsg,QuestionManagerState> pull = new PullBase<ApiEnvelope,GetQuestionsMsg,QuestionManagerState>(
	GetQuestionsMsg.OPERATION // id
	, (GetQuestionsMsg msg, QuestionManagerState state) -> {
                final ApiEnvelope env = new ApiEnvelope.create();
                // perform processing here
                return env;
        }
	, m -> {
		return buildQuestionManagerStateId((LoginMsg) m.getSessionRecord().getApiMsg());
	}
	, m -> { 
		new QuestionManagerState(buildQuestionManagerStateId((LoginMsg) m.getSessionRecord().getApiMsg());
	}
)`


## Binding a Message to Handler

Pair a PUSH Handler to a Message:

`Dispatch.bind(GetQuestionsMsg.class, push);`

Pair a PULL Handler to a Message:

`Dispatch.bind(GetQuestionsMsg.class, pull);`

## Sending a Message

Send a "fire-and-forget" message to a PUSH HANDLER

`Dispatch.push(msg);`

Send a message to a PULL HANDLER and retrieve return value

`
final Future<Response<ApiEnvelope>> future = Dispatch.pull(msg);
// do something else productive while handler works on request...

// call get() on the future to retrieve response
final Response<ApiEnvelope> response = future.get();
// check if there was an error/exception
// call getException() to get Exception, if any
if (response.getException()!=null) {
  // there was an error, process exception
  processException(response.getException());
}
else {
  // call getResponse() to get returned value of handler
  final ApiEnvelope env = response.getResponse();
  // ... do something with the response ...
}
`

