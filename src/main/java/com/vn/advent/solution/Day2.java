package com.vn.advent.solution;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day2 implements Solution {

	public static void main(String[] args) {
		Solution solution = new Day2();
		System.out.println(solution.run());
	}

	public String partOne(Stream<String> lines) {
		List<String> boxes = Collections
			.unmodifiableList(lines.collect(Collectors.toList()));

		long twoCount = boxes.stream()
			.filter(Day2::testHasLetterAppearExactlyTwice)
			.count();
		long threeCount = boxes.stream()
			.filter(Day2::testHasLetterAppearExactlyThrice)
			.count();
		long checksum = twoCount * threeCount;

		return String.valueOf(checksum);
	}

	public String partTwo(Stream<String> lines) {
		List<String> boxes = Collections
			.unmodifiableList(lines.collect(Collectors.toList()));
		int minLengthOfId = boxes.stream()
			.map(String::length)
			.min(Integer::compare)
			.get();
		String result = null;
		for (int i = 0; i < minLengthOfId; i++) {
			final int index = i;
			Optional<String> commonLetters = boxes.stream()
				.map(id -> removeCharAtIndex(id, index))
				.collect(Collectors.groupingBy(Function.identity(),
						Collectors.counting()))
				.entrySet()
				.stream()
				.filter(entry -> entry.getValue() == 2)
				.map(Map.Entry::getKey)
				.findFirst();
			if (commonLetters.isPresent()) {
				result = commonLetters.get();
				break;
			}
		}
		return result;
	}

	private static String removeCharAtIndex(String str, int index) {
		List<Character> listOfChars = str.chars()
			.mapToObj(i -> (char) i)
			.collect(Collectors.toList());
		listOfChars.remove(index);
		return listOfChars.stream()
			.map(String::valueOf)
			.collect(Collectors.joining());
	}

	private static boolean testHasLetterAppearExactlyTwice(String str) {
		Map<Character, Long> mapOfCharAndCount = str.chars()
			.mapToObj(i -> (char) i)
			.collect(Collectors.groupingBy(Function.identity(),
					Collectors.counting()));
		return mapOfCharAndCount.values()
			.stream()
			.anyMatch(value -> value == 2);
	}

	private static boolean testHasLetterAppearExactlyThrice(String str) {
		Map<Character, Long> mapOfCharAndCount = str.chars()
			.mapToObj(i -> (char) i)
			.collect(Collectors.groupingBy(Function.identity(),
					Collectors.counting()));
		return mapOfCharAndCount.values()
			.stream()
			.anyMatch(value -> value == 3);
	}

	@Override
	public String getInputFileName() {
		return "input_2";
	}

}
