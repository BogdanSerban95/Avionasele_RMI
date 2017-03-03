package server;

import java.rmi.registry.Registry;
import java.util.Scanner;

import common.RegistryManager;
import common.Settings;

public class ServerConsole {

	public static void main(String[] args) {
		try {
			int port = Settings.getServerPort();
			Registry registry = RegistryManager.getRegistry(port);
			new ServerService(registry, Settings.getServerService());
			System.out.println("Server is running, type 'exit' to stop it...");
			try (Scanner scanner = new Scanner(System.in)) {
				while (true) {
					String command = scanner.nextLine();
					if (command == null || "exit".equalsIgnoreCase(command)) {
						break;
					}
				}
			}
			System.out.println("Server stopped...");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
