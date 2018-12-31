package com.vn.advent.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.stream.Stream;

public class FileUtil {
	public static String runCodeForLinesInFile(String inputFileName,
			Function<Stream<String>, String> consumeFileAsStream) {
		Path inputFilePath = null;
		Stream<String> lines = null;
		String result = "";
		try {
			inputFilePath = Paths
				.get(ClassLoader.getSystemResource(inputFileName)
					.toURI());
			lines = Files.lines(inputFilePath);
			result = consumeFileAsStream.apply(lines);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		} finally {
			lines.close();
		}
		return result;
	}
}
