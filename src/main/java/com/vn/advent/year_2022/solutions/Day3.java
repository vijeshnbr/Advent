package com.vn.advent.year_2022.solutions;

import com.vn.advent.Solution;

import java.util.*;
import java.util.stream.Stream;

public class Day3 implements Solution {

	private static final BitSet FIRST_COMPARTMENT_OF_RUCKSACK = new BitSet(53);
	private static final BitSet SECOND_COMPARTMENT_OF_RUCKSACK = new BitSet(53);

	private static final BitSet ELF1_RUCKSACK = new BitSet(53);
	private static final BitSet ELF2_RUCKSACK = new BitSet(53);
	private static final BitSet ELF3_RUCKSACK = new BitSet(53);

	public static void main(String[] args) {
		Solution solution = new Day3();
		System.out.println(solution.run());
	}

	public String partOne(Stream<String> lines) {
		return String.valueOf(lines
				.mapToInt(this::getPriorityOfCommonItemInRucksack)
				.sum());
	}

	private int getPriorityOfCommonItemInRucksack(String rucksack) {
		final int rucksackSize = rucksack.length() / 2;
		final String firstCompartmentItems = rucksack.substring(0, rucksackSize);
		final String secondCompartmentItems = rucksack.substring(rucksackSize);
		firstCompartmentItems.chars()
				.map(this::mapAsciiValueToPriorityValue)
				.forEach(FIRST_COMPARTMENT_OF_RUCKSACK::set);
		secondCompartmentItems.chars()
				.map(this::mapAsciiValueToPriorityValue)
				.forEach(SECOND_COMPARTMENT_OF_RUCKSACK::set);
		FIRST_COMPARTMENT_OF_RUCKSACK.and(SECOND_COMPARTMENT_OF_RUCKSACK);
		final int priority = FIRST_COMPARTMENT_OF_RUCKSACK.nextSetBit(0);
		FIRST_COMPARTMENT_OF_RUCKSACK.clear();
		SECOND_COMPARTMENT_OF_RUCKSACK.clear();
		return priority;
	}

	public String partTwo(Stream<String> lines) {
		Spliterator<String> split = lines.spliterator();
		int groupSize = 3;
		int sumOfPriorities = 0;
		while(true) {
			List<String> elfGroup = new ArrayList<>(groupSize);
			for (int i = 0; i < groupSize && split.tryAdvance(elfGroup::add); i++){};
			if (elfGroup.isEmpty()) break;
			sumOfPriorities += findPriorityOfBadgeOfGroup(elfGroup);
		}
		return String.valueOf(sumOfPriorities);
	}

	private int findPriorityOfBadgeOfGroup(List<String> elfGroupRuckSacks) {
		elfGroupRuckSacks.get(0).chars()
				.map(this::mapAsciiValueToPriorityValue)
				.forEach(ELF1_RUCKSACK::set);
		elfGroupRuckSacks.get(1).chars()
				.map(this::mapAsciiValueToPriorityValue)
				.forEach(ELF2_RUCKSACK::set);
		elfGroupRuckSacks.get(2).chars()
				.map(this::mapAsciiValueToPriorityValue)
				.forEach(ELF3_RUCKSACK::set);
		ELF1_RUCKSACK.and(ELF2_RUCKSACK);
		ELF1_RUCKSACK.and(ELF3_RUCKSACK);
		final int priority = ELF1_RUCKSACK.nextSetBit(0);
		ELF1_RUCKSACK.clear();
		ELF2_RUCKSACK.clear();
		ELF3_RUCKSACK.clear();
		return priority;
	}

	private int mapAsciiValueToPriorityValue(int i) {
		if(i <97)
			i = i -38;
		else
			i = i -96;
		return i;
	}

	@Override
	public String getInputFileName() {
		return "2022/input_3";
	}


}
