package com.vn.advent.solution;

import java.util.Set;
import java.util.stream.Stream;

public class Day8 implements Solution {

	public static void main(String[] args) {
		Solution solution = new Day8();
		solution.run();
	}

	public void partOne(Stream<String> lines) {
		String header = lines.findFirst().get();
		int[] arr = Stream.of(header.split(" ")).map(Integer::parseInt)
				.mapToInt(i -> (int) i).toArray();

		for (int i = 0; i < arr.length; i++) {

		}

	}

	public class Node {
		int id;
		Set<Node> children;
	}

	public void partTwo(Stream<String> lines) {

	}

	@Override
	public String getInputFileName() {
		return "input_8";
	}

}
