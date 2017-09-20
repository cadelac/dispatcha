package cadelac.framework.blade.core.message;

import java.security.NoSuchAlgorithmException;

import cadelac.framework.blade.Framework;
import cadelac.framework.blade.core.Utilities;

public interface HandlerDescriptorMsg extends Message {
	
	String getAppName();
	void setAppName(String appName);
	
	String getModuleName();
	void setModuleName(String moduleName);	
	
	String getPackName();
	void setPackName(String packName);
	
	String getHandlerName();
	void setHandlerName(String handlerName);
	
	
	default String buildHashName() {
		final StringBuilder hashNameBuilder = new StringBuilder();
		hashNameBuilder
			.append("handler://")
			.append(getAppName()).append("/")
			.append(getModuleName()).append("/")
			.append(getPackName()).append("/")
			.append(getHandlerName())
			;
		return hashNameBuilder.toString();
	}
	
	default String buildHashId() throws NoSuchAlgorithmException {
		return Utilities.md5Encode(buildHashName());
	}
	
	static HandlerDescriptorMsg createHandlerDescriptorMsg(
			final String appName
			, final String moduleName
			, final String packName
			, final String handlerName) 
			throws Exception {
		return Framework.getObjectFactory().fabricate(
				HandlerDescriptorMsg.class
				, p -> {
					p.setAppName(appName);
					p.setModuleName(moduleName);
					p.setPackName(packName);
					p.setHandlerName(handlerName);
				});
	}
	
	static HandlerDescriptorMsg createHandlerDescriptorMsg() 
			throws Exception {
		return Framework.getObjectFactory().fabricate(HandlerDescriptorMsg.class);
	}
}
