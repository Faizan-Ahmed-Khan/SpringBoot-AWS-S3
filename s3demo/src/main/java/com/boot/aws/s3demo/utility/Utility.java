package com.boot.aws.s3demo.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.web.multipart.MultipartFile;

public class Utility {

	public static void displayText(InputStream input) throws IOException {
		// Read one text line at a time and display.
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		while (true) {
			String line = reader.readLine();
			if (line == null)
				break;
			System.out.println("    " + line);
		}
	}

	public static File convertMultiPartToFile(MultipartFile multiPartfile) throws IOException {

		File convFile = new File(multiPartfile.getOriginalFilename());
		try (FileOutputStream fos = new FileOutputStream(convFile)) {
			fos.write(multiPartfile.getBytes());
			return convFile;
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
			throw ioEx;
		}

	}
}
