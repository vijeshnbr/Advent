package com.vn.advent.solution;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.vn.advent.util.FileUtil;

public interface Solution {

	public static final Logger LOGGER = Logger.getLogger(Solution.class.getName());

	public default void run() {
		System.out.println("Running " + getClass().getSimpleName()
			.toUpperCase());
		runWithInput(getInputFileName());
		System.out.println();
	}

	public default void runWithInput(String input) {
		System.out.print("PART 1 : ");
		Instant startPartOne = Instant.now();
		FileUtil.runCodeForLinesInFile(input, this::partOne);
		Instant finishPartOne = Instant.now();
		printDurationOfExecution(startPartOne, finishPartOne);
		System.out.println();
		System.out.print("PART 2 : ");
		Instant startPartTwo = Instant.now();
		FileUtil.runCodeForLinesInFile(input, this::partTwo);
		Instant finishPartTwo = Instant.now();
		printDurationOfExecution(startPartTwo, finishPartTwo);
		System.out.println();
	}

	public void partOne(Stream<String> lines);

	public void partTwo(Stream<String> lines);

	public String getInputFileName();

	public static void printDurationOfExecution(Instant start, Instant finish) {
		System.out.println();
		System.out.print(String.format("Finished in %d ms", Duration.between(start, finish)
			.toMillis()));
	}

}
