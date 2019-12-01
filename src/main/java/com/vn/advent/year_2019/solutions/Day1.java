package com.vn.advent.year_2019.solutions;

import java.util.stream.Stream;

import com.vn.advent.Solution;

public class Day1 implements Solution {

	public static void main(String[] args) {
		Solution solution = new Day1();
		System.out.println(solution.run());
	}

	public String partOne(Stream<String> lines) {

		long fuel = lines.mapToInt(Integer::parseInt)
				.map(mass -> mass / 3 - 2)
				.sum();
		return String.valueOf(fuel);
	}

	public String partTwo(Stream<String> lines) {
		long fuelMass = lines.mapToInt(Integer::parseInt)
				.map(this::calculateFuelForMass)
				.sum();
		return String.valueOf(fuelMass);
	}


	private int calculateFuelForMass(int mass) {
		return calculateFuelForMassHelper(mass, 0);
	}

	private int calculateFuelForMassHelper(int mass, int previousTotalMass) {
		if (mass <= 0)
			return previousTotalMass;
		int fuelMass = mass / 3 - 2;
		fuelMass = fuelMass > 0 ? fuelMass : 0;
		return calculateFuelForMassHelper(fuelMass, previousTotalMass + fuelMass);
	}

	@Override
	public String getInputFileName() {
		return "2019/input_1";
	}

}
