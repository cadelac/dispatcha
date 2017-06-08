package cadelac.framework.blade.module.accountpack.handler;

import cadelac.framework.blade.handler2.FacilityReplyBase;
import cadelac.lib.primitive.exception.ArgumentException;
import cadelac.lib.primitive.message.AccountCreateMessage;
import cadelac.lib.primitive.message.AccountCreateResponseMessage;


/**
 * Creates Account
 * @author cadelac
 *
 */
public class AccountCreateFacility 
		extends FacilityReplyBase<AccountCreateResponseMessage,AccountCreateMessage>{

	public AccountCreateFacility() throws ArgumentException {
		super(AccountCreateFacility.class.getSimpleName());
	}
}
