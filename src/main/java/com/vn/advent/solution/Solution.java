package com.vn.advent.solution;

import java.util.stream.Stream;

import com.vn.advent.util.FileUtil;

public interface Solution {

	public default void run() {
		System.out
				.println("Running " + getClass().getSimpleName().toUpperCase());
		runWithInput(getInputFileName());
		System.out.println();
	}

	public default void runWithInput(String input) {
		System.out.print("PART 1 : ");
		FileUtil.runCodeForLinesInFile(input, this::partOne);
		System.out.println();
		System.out.print("PART 2 : ");
		FileUtil.runCodeForLinesInFile(input, this::partTwo);
		System.out.println();
	}

	public void partOne(Stream<String> lines);

	public void partTwo(Stream<String> lines);

	public String getInputFileName();

}
