package cadelac.framework.blade.comm.websocket;

import cadelac.framework.blade.core.config.ConfigSetting;

/**
 * Contains websocket configuration information
 * @author cadelac
 *
 */
public interface WebsocketConfigInfo extends ConfigSetting {

	static final String WEBSOCKET_CONFIG_PROPERTY = "websocket-config-file";
	
	public String getProtocol();
	public void setProtocol(String protocol);
	
	public String getHost();
	public void setHost(String host);
	
	public int getPort();
	public void setPort(int port);
	
	public String getContextPath();
	public void setContextPath(String contextPath);
	
	public String getServerEndpoint();
	public void setServerEndpoint(String serverEndpoint);
	
}
