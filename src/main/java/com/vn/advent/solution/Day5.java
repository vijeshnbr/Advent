package com.vn.advent.solution;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day5 implements Solution {

	private static final String ALPHABET_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";

	private static final Pattern PATTERN_OPPOSITE_POLARITY_LETTERS = Pattern
			.compile(
					"Qq|qQ|Ww|wW|eE|Ee|Rr|rR|tT|Tt|Yy|yY|Uu|uU|Ii|iI|oO|Oo|pP|Pp|Aa|aA|sS|Ss|Dd|dD|fF|Ff|gG|Gg|hH|Hh|jJ|Jj|Kk|kK|Ll|lL|Zz|zZ|Xx|xX|cC|Cc|vV|Vv|Bb|bB|Nn|nN|mM|Mm");

	public static void main(String[] args) {
		Solution solution = new Day5();
		solution.run();
	}

	public void partOne(Stream<String> lines) {
		lines.map(this::processLinePartOne).forEach(System.out::print);
	}

	public void partTwo(Stream<String> lines) {
		lines.forEach(this::processLinePartTwo);
	}

	private void processLinePartTwo(String str) {
		IntStream.range(0, ALPHABET_LOWERCASE.length()).boxed().parallel()
				.map(index -> {
					char lowerChar = ALPHABET_LOWERCASE.charAt(index);
					String newPolymer = str
							.replace(String.valueOf(lowerChar), "")
							.replace(String.valueOf(lowerChar).toUpperCase(),
									"");
					return newPolymer;
				}).map(this::replaceAllRecursive).min(Integer::compare)
				.ifPresent(System.out::print);
	}

	private int processLinePartOne(String str) {
		return replaceAllRecursive(str);
	}

	private int replaceAllRecursive(String str) {
		return replaceAllRecursiveHelper(str,
				PATTERN_OPPOSITE_POLARITY_LETTERS);
	}

	private int replaceAllRecursiveHelper(String str, Pattern p) {
		int originalLength = str.length();
		String result = str;
		Matcher m = p.matcher(str);
		while (m.find()) {
			result = m.replaceAll("");
		}
		int newLength = result.length();
		if (newLength != originalLength) {
			return replaceAllRecursive(result);
		}
		return originalLength;
	}

	@Override
	public String getInputFileName() {
		return "input_5";
	}

}
