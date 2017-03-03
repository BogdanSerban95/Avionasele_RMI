package common;

import java.util.ResourceBundle;

public final class Settings {
	
	private final static ResourceBundle BUNDLE = ResourceBundle.getBundle(Settings.class.getName().toLowerCase());
	
	private Settings(){
		
	}
	
	public static int getServerPort(){
		return Integer.parseInt(BUNDLE.getString("serverPort"));
	}
	
	public static int getClientPort(){
		return Integer.parseInt(BUNDLE.getString("clientPort"));
	}
	
	public static String getServerHost(){
		return BUNDLE.getString("serverHost");
	}
	
	public static String getServerService(){
		return BUNDLE.getString("serverService");
	}
	public static String getClientService() {
		return BUNDLE.getString("clientService");
		
	}
}
