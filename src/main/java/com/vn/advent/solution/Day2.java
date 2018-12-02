package com.vn.advent.solution;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vn.advent.util.FileUtil;

public class Day2 {

	public static void main(String[] args) {
		FileUtil.runCodeForLinesInFile("input_2", Day2::partOne);
		FileUtil.runCodeForLinesInFile("input_2", Day2::partTwo);
	}

	private static void partOne(Stream<String> lines) {
		List<String> boxes = Collections
				.unmodifiableList(lines.collect(Collectors.toList()));

		long twoCount = boxes.stream()
				.filter(Day2::testHasLetterAppearExactlyTwice).count();
		long threeCount = boxes.stream()
				.filter(Day2::testHasLetterAppearExactlyThrice).count();
		long checksum = twoCount * threeCount;

		System.out.println(String.format("PART 1: %d", checksum));
	}

	private static void partTwo(Stream<String> lines) {
		List<String> boxes = Collections
				.unmodifiableList(lines.collect(Collectors.toList()));

		int minLengthOfId = boxes.stream().map(String::length)
				.min(Integer::compare).get();

		for (int i = 0; i < minLengthOfId; i++) {
			final int index = i;
			Optional<String> commonLetters = boxes.stream()
					.map(id -> removeCharAtIndex(id, index))
					.collect(Collectors.groupingBy(Function.identity(),
							Collectors.counting()))
					.entrySet().stream().filter(entry -> entry.getValue() == 2)
					.map(Map.Entry::getKey).findFirst();

			commonLetters.ifPresent(letters -> System.out
					.println(String.format("PART 2: %s", letters)));
			if (commonLetters.isPresent())
				break;
		}
	}

	private static String removeCharAtIndex(String str, int index) {
		List<Character> listOfChars = str.chars().mapToObj(i -> (char) i)
				.collect(Collectors.toList());
		listOfChars.remove(index);
		return listOfChars.stream().map(String::valueOf)
				.collect(Collectors.joining());
	}

	private static boolean testHasLetterAppearExactlyTwice(String str) {
		Map<Character, Long> mapOfCharAndCount = str.chars()
				.mapToObj(i -> (char) i).collect(Collectors.groupingBy(
						Function.identity(), Collectors.counting()));
		return mapOfCharAndCount.values().stream()
				.anyMatch(value -> value == 2);
	}

	private static boolean testHasLetterAppearExactlyThrice(String str) {
		Map<Character, Long> mapOfCharAndCount = str.chars()
				.mapToObj(i -> (char) i).collect(Collectors.groupingBy(
						Function.identity(), Collectors.counting()));
		return mapOfCharAndCount.values().stream()
				.anyMatch(value -> value == 3);
	}

}
