package server;

import java.io.Console;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.IClientService;
import common.IServerService;
import common.Utilities;

public class ServerService extends UnicastRemoteObject implements IServerService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<String> configFileNames = new ArrayList<>();
	Map<IClientService, String> clients = new HashMap<>();
	String[][] config;
	Map<String, String> airplanes = new HashMap<>();
	private int destroyedAirplanes;

	public ServerService(Registry registry, String name) throws RemoteException {
		registry.rebind(name, this);
		configFileNames.add("config1.txt");
		configFileNames.add("config2.txt");
		configFileNames.add("config3.txt");
		configFileNames.add("config4.txt");
		configFileNames.add("config5.txt");
		config = Utilities.loadConfiguration(configFileNames);
		destroyedAirplanes = 0;
		airplanes.put("A", "1");
		airplanes.put("B", "2");
		airplanes.put("C", "3");

	}

	@Override
	public void subscribe(IClientService client, String clientName) throws RemoteException {
		if (!clients.containsKey(client)) {
			synchronized (clients) {
				clients.put(client, clientName);
			}
		}
		clients.forEach((clt, name) -> {
			if (clt != client) {
				String message = clientName + " has connected...";
				try {
					clt.onReceiveMessage(message);
				} catch (RemoteException e) {

				}
			}
		});

	}

	@Override
	public void unsubscribe(IClientService client) throws RemoteException {
		String clientName = "";
		if (clients.containsKey(client)) {
			clientName = clients.get(client);
			synchronized (clients) {
				clients.remove(client);
			}
		}
		String message = clientName + " has disconected...";
		broadcastMessage(message);

	}

	@Override
	public void broadcastMessage(String message) throws RemoteException {
		clients.forEach((client, name) -> {
			try {
				client.onReceiveMessage(message);
			} catch (RemoteException e) {

			}
		});

	}

	@Override
	public void shoot(int x, int y, IClientService shooter) throws RemoteException {
		String target = config[x][y];
		String shooterName = clients.get(shooter);

		if (target.equals("A") || target.equals("B") || target.equals("C")) {
			config[x][y] = "X";
			airplaneDestroyed(target, shooterName);

		} else {
			if (target.equals("1") || target.equals("2") || target.equals("3")) {
				config[x][y] = "X";
				String message = shooterName + " has hit an airplane...";
				broadcastMessage(message);
			} else {
				String message = shooterName + " has missed...";
				broadcastMessage(message);
			}
		}

	}

	private void airplaneDestroyed(String airplaneHead, String shooterName) throws RemoteException {
		String airplaneBody = airplanes.get(airplaneHead);
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (config[i][j].equals(airplaneBody)) {
					synchronized (config) {
						config[i][j] = "X";
					}

				}
			}
		}
		destroyedAirplanes++;
		String message = shooterName + " shoot down an airplane!\nRemaining airplanes: " + (3 - destroyedAirplanes);
		this.broadcastMessage(message);

		if (destroyedAirplanes == 3) {
			message = shooterName + " shoot down the last airplane.\nReseting game...";
			broadcastMessage(message);
			config = Utilities.loadConfiguration(configFileNames);
			destroyedAirplanes = 0;
			message = "Game has been restarted.";
			broadcastMessage(message);
		}

	}

}
