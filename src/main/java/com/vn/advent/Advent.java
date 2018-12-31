package com.vn.advent;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

import com.vn.advent.solution.Day1;
import com.vn.advent.solution.Day10;
import com.vn.advent.solution.Day12;
import com.vn.advent.solution.Day13;
import com.vn.advent.solution.Day14;
import com.vn.advent.solution.Day15;
import com.vn.advent.solution.Day16;
import com.vn.advent.solution.Day17;
import com.vn.advent.solution.Day18;
import com.vn.advent.solution.Day2;
import com.vn.advent.solution.Day20;
import com.vn.advent.solution.Day22;
import com.vn.advent.solution.Day23;
import com.vn.advent.solution.Day24;
import com.vn.advent.solution.Day25;
import com.vn.advent.solution.Day3;
import com.vn.advent.solution.Day4;
import com.vn.advent.solution.Day5;
import com.vn.advent.solution.Day6;
import com.vn.advent.solution.Day7;
import com.vn.advent.solution.Day8;
import com.vn.advent.solution.Day9;
import com.vn.advent.solution.Solution;

public class Advent {

	public static void main(String[] args) {

		Solution.LOGGER.setLevel(Level.OFF);

		List<Supplier<Solution>> solutions = new ArrayList<>();
		solutions.add(Day1::new);
		solutions.add(Day2::new);
		solutions.add(Day3::new);
		solutions.add(Day4::new);
		solutions.add(Day5::new);
		solutions.add(Day6::new);
		solutions.add(Day7::new);
		solutions.add(Day8::new);
		solutions.add(Day9::new);
		solutions.add(Day10::new);
		solutions.add(Day12::new);
		solutions.add(Day13::new);
		solutions.add(Day14::new);
		solutions.add(Day15::new);
		solutions.add(Day16::new);
		solutions.add(Day17::new);
		solutions.add(Day18::new);
		solutions.add(Day20::new);
		solutions.add(Day22::new);
		solutions.add(Day23::new);
		solutions.add(Day24::new);
		solutions.add(Day25::new);

		System.out.println("RUNNING ALL SOLUTIONS..");
		Instant start = Instant.now();
		solutions.stream()
			.parallel()
			.map(Supplier::get)
			.map(Solution::run)
			.forEach(System.out::println);
		Instant end = Instant.now();
		System.out
			.println("Total execution time: " + Duration.between(start, end)
				.toMillis());
	}

}
