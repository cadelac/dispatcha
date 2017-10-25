package cadelac.framework.blade.core;

import java.security.NoSuchAlgorithmException;

import cadelac.framework.blade.Framework;

/**
 * Alternative to HandlerDescriptorMsg
 * @author cadelac
 *
 */
public class AgentDescriptor {

	public static String build(final String agentName_) 
			throws NoSuchAlgorithmException {
		final StringBuilder hashNameBuilder = new StringBuilder();
		hashNameBuilder.append(
				String.format(
						"%s/%s"
						, Framework.getApplication().getId()
						, agentName_));
		return Utilities.md5Encode(hashNameBuilder.toString());
	}
}
