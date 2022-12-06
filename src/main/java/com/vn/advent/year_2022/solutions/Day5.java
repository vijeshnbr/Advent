package com.vn.advent.year_2022.solutions;

import com.vn.advent.Solution;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5 implements Solution {

	private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
	private static final Pattern CRATE_PATTERN = Pattern.compile("\\[[A-Z]\\]");
	private static final Predicate<String> IS_BLANK = String::isBlank;
	private static final SortedMap<Integer, Deque<String>> EVER_GIVEN = new TreeMap<>();

	public static void main(String[] args) {
		Solution solution = new Day5();
		System.out.println(solution.run());
	}

	public String partOne(Stream<String> lines) {
		final List<String> input = lines.toList();
		loadEverGivenWithCrates(input);

		getInstructionStreamForCrateMover(input)
				.forEach(instruction ->{
							int noOfCratesToBeMoved = Integer.valueOf(instruction.get(0));
							int sourceCrate = Integer.valueOf(instruction.get(1));
							int destinationCrate = Integer.valueOf(instruction.get(2));
							for(int i=0; i<noOfCratesToBeMoved; i++)
								EVER_GIVEN.get(destinationCrate)
										.addFirst(EVER_GIVEN.get(sourceCrate)
												.removeFirst());
						});

		return topCrateOfEachStack();

	}

	public String partTwo(Stream<String> lines) {
		final List<String> input = lines.toList();
		loadEverGivenWithCrates(input);

		getInstructionStreamForCrateMover(input)
				.forEach(instruction ->{
					int noOfCratesToBeMoved = Integer.valueOf(instruction.get(0));
					int sourceCrate = Integer.valueOf(instruction.get(1));
					int destinationCrate = Integer.valueOf(instruction.get(2));
					for(int i=0; i<noOfCratesToBeMoved; i++)
						EVER_GIVEN.get(sourceCrate).addLast(EVER_GIVEN.get(sourceCrate)
								.removeFirst());
					for(int i=0; i<noOfCratesToBeMoved; i++)
						EVER_GIVEN.get(destinationCrate)
								.addFirst(EVER_GIVEN.get(sourceCrate)
										.removeLast());
				});

		return topCrateOfEachStack();

	}

	private void loadEverGivenWithCrates(List<String> input) {
		EVER_GIVEN.clear();
		input.stream()
				.takeWhile(IS_BLANK.negate())
				.forEach(line -> {
							CRATE_PATTERN
									.matcher(line)
									.results()
									.forEach(matchResult -> {
										int stackNo = matchResult.start() / 4 + 1;
										EVER_GIVEN.compute(stackNo, (k, v) -> {
											if (v == null) {
												v = new ArrayDeque<>();
											}
											v.addLast(matchResult.group());
											return v;
										});
									});
						});
	}

	private Stream<List<String>> getInstructionStreamForCrateMover(List<String> input) {
		return input.stream()
				.dropWhile(IS_BLANK.negate())
				.skip(1)
				.map(line ->
						NUMBER_PATTERN
								.matcher(line)
								.results()
								.map(MatchResult::group)
								.toList());
	}

	private String topCrateOfEachStack() {
		return EVER_GIVEN.values()
				.stream()
				.map(Deque::peekFirst)
				.collect(Collectors.joining())
				.replaceAll("\\[|\\]", "");
	}


	@Override
	public String getInputFileName() {
		return "2022/input_5";
	}


}
