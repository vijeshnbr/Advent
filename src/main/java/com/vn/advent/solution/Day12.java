package com.vn.advent.solution;

import java.util.ArrayList;
import java.util.Deque;
import java.util.IntSummaryStatistics;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day12 implements Solution {

	private static final Pattern FIND_PATTERNS_PATTERN = Pattern
		.compile("([#|\\.]{5})\\s\\=\\>\\s#");

	private static final String INITIAL_STATE = "#.#####.#.#.####.####.#.#...#.......##..##.#.#.#.###..#.....#.####..#.#######.#....####.#....##....#";
	private static final int TOTAL_GEN_PART_ONE = 20;
	private static final long TOTAL_GEN_PART_TWO = 50000000000L;

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day12();
		System.out.println(solution.run());
	}

	@Override
	public String partOne(Stream<String> lines) {
		List<Pattern> plantYieldingPatterns = lines.map(str -> {
			String find = null;
			Matcher m = FIND_PATTERNS_PATTERN.matcher(str);
			if (m.find()) {
				find = m.group(1);
			}
			return find;
		})
			.filter(Objects::nonNull)
			.map(str -> str.replaceAll("\\.", "\\\\."))
			// .peek(System.out::println)
			.map(Pattern::compile)
			.collect(Collectors.toList());

		Deque<String> stackOfGenerations = new LinkedList<>();
		String prepCurrentGenPlantStateForAnalysis = prepPlantStateForAnalysis(
				INITIAL_STATE);
		LOGGER.info(prepCurrentGenPlantStateForAnalysis);
		stackOfGenerations.push(prepCurrentGenPlantStateForAnalysis);
		long totalOfIndices = 0;
		int head = 0;

		List<Integer> indicesYieldingPlantsInNextGeneration = new ArrayList<>();

		for (long i = 1; i <= TOTAL_GEN_PART_ONE; i++) {
			String currentGen = stackOfGenerations.peek();
			// System.out.println(i - 1 + ": " + currentGen);

			int tmpHead = head;
			plantYieldingPatterns.forEach(pattern -> {
				// System.out.println(pattern);
				Matcher m = pattern.matcher(currentGen);
				while (m.find()) {
					int start = m.start();
					int indexOfPlant = start + 2;
					// System.out.println("Found match " + m.group() + " at "
					// + (indexOfPlant - 4));
					indicesYieldingPlantsInNextGeneration
						.add(indexOfPlant - (4 - tmpHead));
					m.region(start + 1, currentGen.length() - 1);
				}
			});
			StringBuilder nextGen = new StringBuilder(
					currentGen.replaceAll("#", "."));
			indicesYieldingPlantsInNextGeneration.forEach(
					index -> nextGen.setCharAt(index + (4 - tmpHead), '#'));
			String prepNextGenPlantStateForAnalysis = prepPlantStateForAnalysis(
					nextGen.toString());
			// System.out.println(prepNextGenPlantStateForAnalysis);
			stackOfGenerations.push(prepNextGenPlantStateForAnalysis);
			IntSummaryStatistics summaryStatistics = indicesYieldingPlantsInNextGeneration
				.stream()
				.mapToInt(Integer::valueOf)
				.summaryStatistics();
			head = summaryStatistics.getMin();
			if (i == TOTAL_GEN_PART_ONE)
				totalOfIndices = summaryStatistics.getSum();
			indicesYieldingPlantsInNextGeneration.sort(Integer::compare);
			// System.out.println(indicesYieldingPlantsInNextGeneration);
			indicesYieldingPlantsInNextGeneration.clear();

		}
		return String.valueOf(totalOfIndices);
	}

	private String prepPlantStateForAnalysis(String s) {
		int firstIndexOfPlant = s.indexOf('#');
		int lastIndexOfPlant = s.lastIndexOf('#');
		return "...." + s.substring(firstIndexOfPlant, lastIndexOfPlant + 1)
				+ "....";
	}

	@Override
	public String partTwo(Stream<String> lines) {
		List<Pattern> plantYieldingPatterns = lines.map(str -> {
			String find = null;
			Matcher m = FIND_PATTERNS_PATTERN.matcher(str);
			if (m.find()) {
				find = m.group(1);
			}
			return find;
		})
			.filter(Objects::nonNull)
			.map(str -> str.replaceAll("\\.", "\\\\."))
			// .peek(System.out::println)
			.map(Pattern::compile)
			.collect(Collectors.toList());

		Deque<String> stackOfGenerations = new LinkedList<>();
		String prepCurrentGenPlantStateForAnalysis = prepPlantStateForAnalysis(
				INITIAL_STATE);
		// System.out.println();
		// System.out.println(prepCurrentGenPlantStateForAnalysis);
		stackOfGenerations.push(prepCurrentGenPlantStateForAnalysis);
		long totalOfIndices = 0;
		int head = 0;
		long differential = 0;

		List<Integer> indicesYieldingPlantsInNextGeneration = new ArrayList<>();
		long currGen = 0;

		for (long i = 1; i <= TOTAL_GEN_PART_TWO; i++) {
			String currentGen = stackOfGenerations.peek();
			// System.out.println(i - 1 + ": " + currentGen);

			int tmpHead = head;
			plantYieldingPatterns.forEach(pattern -> {
				// System.out.println(pattern);
				Matcher m = pattern.matcher(currentGen);
				while (m.find()) {
					int start = m.start();
					int indexOfPlant = start + 2;
					// System.out.println("Found match " + m.group() + " at "
					// + (indexOfPlant - 4));
					indicesYieldingPlantsInNextGeneration
						.add(indexOfPlant - (4 - tmpHead));
					m.region(start + 1, currentGen.length() - 1);
				}
			});
			StringBuilder nextGen = new StringBuilder(
					currentGen.replaceAll("#", "."));
			indicesYieldingPlantsInNextGeneration.forEach(
					index -> nextGen.setCharAt(index + (4 - tmpHead), '#'));
			String prepNextGenPlantStateForAnalysis = prepPlantStateForAnalysis(
					nextGen.toString());
			// System.out.println(prepNextGenPlantStateForAnalysis);
			stackOfGenerations.push(prepNextGenPlantStateForAnalysis);
			IntSummaryStatistics summaryStatistics = indicesYieldingPlantsInNextGeneration
				.stream()
				.mapToInt(Integer::valueOf)
				.summaryStatistics();
			head = summaryStatistics.getMin();
			long currTotalOfIndices = summaryStatistics.getSum();
			long currDifferential = currTotalOfIndices - totalOfIndices;
			totalOfIndices = currTotalOfIndices;
			currGen = i;
			if (currDifferential == differential) {
				break;
			} else {
				differential = currDifferential;
			}
			indicesYieldingPlantsInNextGeneration.sort(Integer::compare);
			// System.out.println(indicesYieldingPlantsInNextGeneration);
			indicesYieldingPlantsInNextGeneration.clear();

		}
		return String.valueOf(
				totalOfIndices + (TOTAL_GEN_PART_TWO - currGen) * differential);
	}

	public static int indexOf(Pattern pattern, String s) {
		Matcher matcher = pattern.matcher(s);
		return matcher.find() ? matcher.start() : -1;
	}

	@Override
	public String getInputFileName() {
		return "input_12";
	}

}
