package com.vn.advent.year_2022.solutions;

import com.vn.advent.Solution;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day6 implements Solution {

	private static final int bufferSizeForPacketMarker = 4;
	private static final int bufferSizeForMessageMarker = 14;

	public static void main(String[] args) {
		Solution solution = new Day6();
		System.out.println(solution.run());
	}

	public String partOne(Stream<String> lines) {
		var input = lines.collect(Collectors.joining());

		return String.valueOf(IntStream.range(0, input.length())
				.filter(index -> input.substring(index, index + bufferSizeForPacketMarker).chars().distinct().count()==bufferSizeForPacketMarker)
				.map(i -> i + bufferSizeForPacketMarker)
				.limit(1)
				.sum());
	}

	public String partTwo(Stream<String> lines) {
		var input = lines.collect(Collectors.joining());

		return String.valueOf(IntStream.range(0, input.length())
				.filter(index -> input.substring(index, index + bufferSizeForMessageMarker).chars().distinct().count()==bufferSizeForMessageMarker)
				.map(i -> i + bufferSizeForMessageMarker)
				.limit(1)
				.sum());
	}

	@Override
	public String getInputFileName() {
		return "2022/input_6";
	}
}
