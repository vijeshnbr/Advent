package com.vn.advent.solution;

import java.time.Duration;
import java.time.Instant;
import java.util.logging.Logger;
import java.util.stream.Stream;

import com.vn.advent.util.FileUtil;

public interface Solution {

	public static final Logger LOGGER = Logger
		.getLogger(Solution.class.getName());

	public default String run() {
		StringBuilder s = new StringBuilder();
		s.append("Running " + getClass().getSimpleName()
			.toUpperCase());
		s.append("\n");
		s.append(runWithInput(getInputFileName()));
		s.append("\n");
		return s.toString();
	}

	public default String runWithInput(String input) {
		StringBuilder s = new StringBuilder();
		s.append("PART 1 : ");
		Instant startPartOne = Instant.now();
		s.append(FileUtil.runCodeForLinesInFile(input, this::partOne));
		Instant finishPartOne = Instant.now();
		s.append(printDurationOfExecution(startPartOne, finishPartOne));
		s.append("\n");
		s.append("PART 2 : ");
		Instant startPartTwo = Instant.now();
		s.append(FileUtil.runCodeForLinesInFile(input, this::partTwo));
		Instant finishPartTwo = Instant.now();
		s.append(printDurationOfExecution(startPartTwo, finishPartTwo));
		s.append("\n");
		return s.toString();
	}

	public String partOne(Stream<String> lines);

	public String partTwo(Stream<String> lines);

	public String getInputFileName();

	public static String printDurationOfExecution(Instant start,
			Instant finish) {
		StringBuilder s = new StringBuilder();
		s.append("\n");
		s.append(String.format("Finished in %d ms",
				Duration.between(start, finish)
					.toMillis()));
		return s.toString();
	}

}
