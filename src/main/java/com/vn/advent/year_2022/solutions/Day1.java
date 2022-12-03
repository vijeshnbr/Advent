package com.vn.advent.year_2022.solutions;

import com.vn.advent.Solution;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class Day1 implements Solution {
	private static final Queue<Integer> maxHeapOfCalories = new PriorityQueue<>(Comparator.reverseOrder());

	public static void main(String[] args) {
		Solution solution = new Day1();
		System.out.println(solution.run());
	}

	public String partOne(Stream<String> lines) {
		final Optional<Integer> lastElfCalorie = lines
				.map(this::parseInt)
				.reduce(Optional.of(0), (sum, calorie) -> {
					if (calorie.isPresent()) {
						sum = Optional.of(sum.get() + calorie.get());
					} else {
						maxHeapOfCalories.add(sum.get());
						sum = Optional.of(0);
					}
					return sum;
				});
		maxHeapOfCalories.add(lastElfCalorie.get());
		return String.valueOf(maxHeapOfCalories.peek());
	}

	public String partTwo(Stream<String> lines) {
		final int totalCaloriesOfTop3Elves =Stream.generate(maxHeapOfCalories::poll)
				.limit(3)
				.mapToInt(i->i)
				.sum();
		return String.valueOf(totalCaloriesOfTop3Elves);
	}

	private Optional<Integer> parseInt(String str) {
		if(str.isBlank())
			return Optional.empty();
		return Optional.of(Integer.valueOf(str));
	}


	@Override
	public String getInputFileName() {
		return "2022/input_1";
	}

}
