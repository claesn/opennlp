package utils;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileUtils {
	/**
	 * Get file data as string
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readAsString(String fileName) {
		try {
			String data = new String(readAllBytes(get(fileName)));
			return data;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	/*
	 * Zeilenweises Einlesen (für Dateien mit einem Satz je Zeile)
	 */
	public static String[] readLines(String inputFile) {
		
		List<String> lines = new ArrayList<String>();
		try {
			Scanner scanner = new Scanner(new File(inputFile));
			while (scanner.hasNextLine()) {
				lines.add(scanner.nextLine());
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return lines.toArray(new String[lines.size()]);
	}
}