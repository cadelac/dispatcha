package cadelac.framework.blade.module.sessionpack;


import cadelac.framework.blade.core.component.shell.Shell;
import cadelac.framework.blade.module.sessionpack.handler.SessionCreateHandler;
import cadelac.framework.blade.module.sessionpack.handler.SessionDestroyHandler;
import cadelac.framework.blade.module.sessionpack.handler.SessionHelper;
import cadelac.framework.blade.module.sessionpack.message.SessionCreateMessage;
import cadelac.framework.blade.module.sessionpack.message.SessionCreateResponseMessage;
import cadelac.framework.blade.module.sessionpack.message.SessionDestroyMessage;
import cadelac.framework.blade.pack.PackBase;
import cadelac.framework.blade.plug.RequestOutputPlug;
import cadelac.lib.primitive.concept.state.StateLess;
import cadelac.lib.primitive.exception.FrameworkException;
import cadelac.lib.primitive.message.AuthenticationMessage;
import cadelac.lib.primitive.message.AuthenticationResponseMessage;


/**
 * Session Manager
 * manages lifecycle of sessions (creation, destruction, timeout, etc.)
 * responsible for granting/revoking access
 * implements single-sign-on "SSO"
 * 
 * @author cadelac
 *
 */
public class SessionPack extends PackBase {


	public static final String SESSION_CREATE_INPUTPLUG = "sessionCreateInputPlug";
	public static final String SESSION_DESTROY_INPUTPLUG = "sessionDestroyInputPlug";
	
	public static final String AUTHENTICATION_OUTPUTPLUG = "authenticationOutputPlug";
	
	
	public SessionPack() throws FrameworkException, Exception {
		super(SessionPack.class.getSimpleName());
		
		// create helper
		final SessionHelper sessionHelper = new SessionHelper();
		
		// create handlers
		final SessionCreateHandler sessionCreateHandler = new SessionCreateHandler();
		sessionCreateHandler.setSessionHelper(sessionHelper);
		addContainable(sessionCreateHandler);
		
		final SessionDestroyHandler sessionDestroyHandler = new SessionDestroyHandler();
		sessionDestroyHandler.setSessionHelper(sessionHelper);
		addContainable(sessionDestroyHandler);
		
		// connect plugs

		
		// expose input plugs
		exposeRequestInputPlug(SESSION_CREATE_INPUTPLUG, sessionCreateHandler.getInputPlug());
		exposeSendInputPlug(SESSION_DESTROY_INPUTPLUG, sessionDestroyHandler.getInputPlug());
		// expose output plugs
		@SuppressWarnings("unchecked")
		final RequestOutputPlug<AuthenticationResponseMessage,AuthenticationMessage,StateLess> authOP = 
			(RequestOutputPlug<AuthenticationResponseMessage, AuthenticationMessage, StateLess>) 
			sessionCreateHandler.getOutputPlug(SessionCreateHandler.AUTHENTICATION_OUTPUTPLUG);
		exposeRequestOutputPlug(AUTHENTICATION_OUTPUTPLUG, authOP);

		// register classes
		final Shell shell = getShell();
		shell.registerClass(SessionCreateMessage.class);
		shell.registerClass(SessionCreateResponseMessage.class);
		shell.registerClass(SessionDestroyMessage.class);
	}
}
