package com.vn.advent.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FileUtil {
	public static void runCodeForLinesInFile(String inputFileName,
			Consumer<Stream<String>> consumeFileAsStream) {
		Path inputFilePath = null;
		Stream<String> lines = null;
		try {
			inputFilePath = Paths
					.get(ClassLoader.getSystemResource(inputFileName).toURI());
			lines = Files.lines(inputFilePath);
			consumeFileAsStream.accept(lines);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		} finally {
			lines.close();
		}
	}
}
