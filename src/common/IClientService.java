package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IClientService extends Remote {
	
	public void onReceiveMessage(String message) throws RemoteException;
	
	public void onShotResponse(String response) throws RemoteException;
	
}
