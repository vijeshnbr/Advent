package com.vn.advent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.vn.advent.solution.Day1;
import com.vn.advent.solution.Day2;
import com.vn.advent.solution.Day3;
import com.vn.advent.solution.Day4;
import com.vn.advent.solution.Day5;
import com.vn.advent.solution.Solution;

public class Advent {

	public static void main(String[] args) {
		List<Supplier<Solution>> solutions = new ArrayList<>();
		solutions.add(Day1::new);
		solutions.add(Day2::new);
		solutions.add(Day3::new);
		solutions.add(Day4::new);
		solutions.add(Day5::new);

		solutions.stream().map(Supplier::get).forEach(Solution::run);
	}

}
