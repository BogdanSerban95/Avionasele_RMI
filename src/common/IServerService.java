package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IServerService extends Remote {

	public void subscribe(IClientService client, String clientName) throws RemoteException;

	public void unsubscribe(IClientService client) throws RemoteException;

	public void broadcastMessage(String message) throws RemoteException;

	public void shoot(int x, int y, IClientService shooter) throws RemoteException;

}
