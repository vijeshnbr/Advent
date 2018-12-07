package com.vn.advent.solution;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5 implements Solution {

	private static final String ALPHABET_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";

	private static final String REGEX_OPPOSITE_POLARITY_PAIR = Arrays
			.stream(ALPHABET_LOWERCASE.split(""))
			.map(c -> c + c.toUpperCase() + "|" + c.toUpperCase() + c)
			.collect(Collectors.joining("|"));

	private static final Pattern PATTERN = Pattern
			.compile(REGEX_OPPOSITE_POLARITY_PAIR);

	public static void main(String[] args) {
		Solution solution = new Day5();
		solution.run();
	}

	public void partOne(Stream<String> lines) {
		lines.map(this::reactPolymer).map(String::length)
				.forEach(System.out::print);
	}

	public void partTwo(Stream<String> lines) {
		lines.map(this::performBestReaction).filter(Optional::isPresent)
				.map(Optional::get).map(String::length)
				.forEach(System.out::print);
	}

	private Optional<String> performBestReaction(String polymer) {
		return Arrays.stream(ALPHABET_LOWERCASE.split("")).parallel()
				.map(c -> c + "|" + c.toUpperCase())
				.map(regex -> polymer.replaceAll(regex, ""))
				.map(this::reactPolymer)
				.min(Comparator.comparing(String::length));
	}

	private String reactPolymer(String polymer) {
		Matcher m = PATTERN.matcher(polymer);
		while (m.find()) {
			polymer = m.replaceAll("");
			m = PATTERN.matcher(polymer);
		}
		return polymer;
	}

	@Override
	public String getInputFileName() {
		return "input_5";
	}

}
