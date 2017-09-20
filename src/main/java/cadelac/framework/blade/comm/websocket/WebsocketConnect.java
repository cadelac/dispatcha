package cadelac.framework.blade.comm.websocket;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.glassfish.tyrus.client.ClientManager;
import org.glassfish.tyrus.server.Server;

public class WebsocketConnect {
	
	public static void startServer(
			final WebsocketConfigInfo configInfo_
			, final Map<String,Object> properties_
			, final Class<?> configClass_) {
		startServer(
				configInfo_.getHost()
				, configInfo_.getPort()
				, configInfo_.getContextPath()
				, properties_
				, configClass_);
	}
			
	public static void startServer(
			final String host
			, final int port
			, final String path
			, final Map<String,Object> properties
			, final Class<?> config) {
		
		final Server server = new Server(
        		host 
        		, port
        		, path 
        		, properties
        		, config);
		try {
			server.start();
		}
		catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
        }
	}
	
	
	public static void connectClient(
			final WebsocketConfigInfo configInfo_
			, final Class<?> configClass_) {
		connectClient(
				configInfo_.getProtocol()
				, configInfo_.getHost()
				, configInfo_.getPort()
				, configInfo_.getContextPath()
				, configInfo_.getServerEndpoint()
				, configClass_);
	}

    
    public static void connectClient(
    		final String protocol_
    		, final String host
    		, final int port
    		, final String path
    		, final String serverEndpoint
    		, final Class<?> config) {
    	final String proto = 
    			(protocol_==null || protocol_.isEmpty() || "ws".equals(protocol_))
    			? "ws" : "wss";

        // create client that will talk to server
        ClientManager client = ClientManager.createClient();
        try {
        	final StringBuilder uri = new StringBuilder(
        			proto + "://" + host);
        	if (port>0) {
        		uri.append(":" + port);
        	}
        	uri.append(path + serverEndpoint);
            // start the client that will talk to server
            client.connectToServer(config, new URI(uri.toString()));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
        }
        
    }

    public static void encryptedConnectClient(
    		final String host
    		, final int port
    		, final String path
    		, final String serverEndpoint
    		, final Class<?> config) {
    	connectClient("wss", host, port, path, serverEndpoint, config);
    }


	/**
	 * Opens server websocket to listen on for incoming client messages
	 * @throws Exception
	 */
	public static void listenOnServerWebsocket(
			final WebsocketConfigInfo wsConfigInfo
			, final Class<?> configClass_) 
					throws Exception {

		final Map<String,Object> properties = new HashMap<String,Object>();
		startServer(wsConfigInfo, properties, configClass_);
	}
}
