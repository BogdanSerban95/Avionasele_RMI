package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

public class Utilities {

	public static String[][] loadConfiguration(List<String> configFileNames) {
		String[][] config = new String[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				config[i][j] = "";
			}
		}

		int pos = (int) (Math.random() * (configFileNames.size() - 1));

		String fileName = configFileNames.get(pos);

		try {
			// FileWriter writer = new FileWriter(new File(fileName));
			try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
				String line;
				int lineNr = 0;
				while ((line = reader.readLine()) != null) {
					for (int i = 0; i < 10; i++) {
						config[lineNr][i] += line.charAt(i);
					}
					lineNr++;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return config;
	}
}
