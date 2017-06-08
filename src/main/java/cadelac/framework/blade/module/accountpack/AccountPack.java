package cadelac.framework.blade.module.accountpack;

import cadelac.framework.blade.handler2.AgentReplyBase;
import cadelac.framework.blade.handler2.Router;
import cadelac.framework.blade.module.accountpack.handler.AccountCreateFacility;
import cadelac.framework.blade.module.accountpack.handler.AuthenticationFacility;
import cadelac.framework.blade.pack.PackBase;
import cadelac.lib.primitive.concept.state.State;
import cadelac.lib.primitive.message.AccountCreateMessage;
import cadelac.lib.primitive.message.AccountCreateResponseMessage;
import cadelac.lib.primitive.message.AuthenticationMessage;
import cadelac.lib.primitive.message.AuthenticationResponseMessage;

/**
 * Account Manager
 * manages accounts
 * responsible for account creation
 * responsible for authentication
 * 
 * @author cadelac
 *
 */
public class AccountPack extends PackBase {
	
	public AccountPack(
			final AgentReplyBase<AccountCreateResponseMessage,AccountCreateMessage,? extends State> accountCreateAgent_,
			final AgentReplyBase<AuthenticationResponseMessage,AuthenticationMessage,? extends State> authAgent_) 
					throws Exception {
		super(AccountPack.class.getSimpleName());
		
		// create facilities
		
		final AccountCreateFacility accountCreateFacility = new AccountCreateFacility();
		accountCreateFacility.setAgent(accountCreateAgent_);
		this.addContainable(accountCreateFacility);
		Router.registerFacility(AccountCreateMessage.class, accountCreateFacility);
				
		final AuthenticationFacility authenticationFacility = new AuthenticationFacility();
		authenticationFacility.setAgent(authAgent_);
		this.addContainable(authenticationFacility);
		Router.registerFacility(AuthenticationMessage.class, authenticationFacility);
	}
}
