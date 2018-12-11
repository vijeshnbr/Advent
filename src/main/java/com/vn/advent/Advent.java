package com.vn.advent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

import com.vn.advent.solution.Day1;
import com.vn.advent.solution.Day10;
import com.vn.advent.solution.Day2;
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

		solutions.stream()
			.map(Supplier::get)
			.forEach(Solution::run);
	}

}
