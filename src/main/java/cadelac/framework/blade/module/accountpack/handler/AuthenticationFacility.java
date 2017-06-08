package cadelac.framework.blade.module.accountpack.handler;

import cadelac.framework.blade.handler2.FacilityReplyBase;
import cadelac.lib.primitive.message.AuthenticationMessage;
import cadelac.lib.primitive.message.AuthenticationResponseMessage;

public class AuthenticationFacility extends 
	FacilityReplyBase<AuthenticationResponseMessage,AuthenticationMessage> {

	public AuthenticationFacility() {
		super(AuthenticationFacility.class.getSimpleName());
	}
}
