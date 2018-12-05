package com.vn.advent.solution;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day5 implements Solution {

	private static final Pattern PATTERN_SAME_TWO_LETTERS = Pattern
			.compile(".([a-z])\\1{2}");

	public static void main(String[] args) {
		Solution solution = new Day5();
		solution.run();
	}

	public void partOne(Stream<String> lines) {

		lines.forEach(this::processLine);
	}

	public void partTwo(Stream<String> lines) {

	}

	private void processLine(String str) {
		Matcher m = PATTERN_SAME_TWO_LETTERS.matcher(str);
		while (m.find()) {
			System.out.println(m.group());
		}
	}

	@Override
	public String getInputFileName() {
		return "input_1";
	}

}
