package client;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import common.IClientService;

public class ClientService extends UnicastRemoteObject implements IClientService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	IClientService client;

	public ClientService(Registry registry, String name, IClientService client)
			throws AccessException, RemoteException {
		registry.rebind(name, this);
		this.client = client;

	}

	@Override
	public void onReceiveMessage(String message) throws RemoteException {
		client.onReceiveMessage(message);

	}

	@Override
	public void onShotResponse(String response) throws RemoteException {
		client.onShotResponse(response);
	}

}
