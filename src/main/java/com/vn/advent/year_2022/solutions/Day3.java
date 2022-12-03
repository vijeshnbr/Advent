package com.vn.advent.year_2022.solutions;

import com.vn.advent.Solution;

import java.util.*;
import java.util.stream.Stream;

public class Day3 implements Solution {

	private static final BitSet BUCKET_1 = new BitSet(53);
	private static final BitSet BUCKET_2 = new BitSet(53);

	private static final BitSet ELF1_RUCKSACK = new BitSet(53);
	private static final BitSet ELF2_RUCKSACK = new BitSet(53);
	private static final BitSet ELF3_RUCKSACK = new BitSet(53);

	public static void main(String[] args) {
		Solution solution = new Day3();
		System.out.println(solution.run());
	}

	public String partOne(Stream<String> lines) {
		return String.valueOf(lines
				.mapToInt(this::processLine)
				.sum());
	}

	private int processLine(String line) {
		final int bucketSize = line.length() / 2;
		final String firstBucketItems = line.substring(0, bucketSize);
		System.out.println(firstBucketItems);
		final String secondBucketItems = line.substring(bucketSize);
		System.out.println(secondBucketItems);
		firstBucketItems.chars().
				map(i -> {
					if(i<97)
						i = i-38;
					else
						i = i-96;
					return i;
				})
				//.peek(num -> System.out.print(num +","))
				.forEach(BUCKET_1::set);
		//System.out.println("\n-----------------------");
		secondBucketItems.chars().
				map(i -> {
					if(i<97)
						i = i-38;
					else
						i = i-96;
					return i;
				})
				//.peek(num -> System.out.print(num +","))
				.forEach(BUCKET_2::set);
		//System.out.println("\n-----------------------");
		//System.out.println(BUCKET_1);
		//System.out.println(BUCKET_2);
		BUCKET_1.and(BUCKET_2);
		//System.out.println(BUCKET_1);
		final int priority = BUCKET_1.nextSetBit(0);
		BUCKET_1.clear();
		BUCKET_2.clear();
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
		elfGroupRuckSacks.get(0).chars().
				map(i -> {
					if(i<97)
						i = i-38;
					else
						i = i-96;
					return i;
				})
				//.peek(num -> System.out.print(num +","))
				.forEach(ELF1_RUCKSACK::set);
		//System.out.println("\n-----------------------");
		elfGroupRuckSacks.get(1).chars().
				map(i -> {
					if(i<97)
						i = i-38;
					else
						i = i-96;
					return i;
				})
				//.peek(num -> System.out.print(num +","))
				.forEach(ELF2_RUCKSACK::set);
		//System.out.println("\n-----------------------");
		//System.out.println(BUCKET_1);
		//System.out.println(BUCKET_2);
		elfGroupRuckSacks.get(2).chars().
				map(i -> {
					if(i<97)
						i = i-38;
					else
						i = i-96;
					return i;
				})
				//.peek(num -> System.out.print(num +","))
				.forEach(ELF3_RUCKSACK::set);
		ELF1_RUCKSACK.and(ELF2_RUCKSACK);
		ELF1_RUCKSACK.and(ELF3_RUCKSACK);
		//System.out.println(BUCKET_1);
		final int priority = ELF1_RUCKSACK.nextSetBit(0);
		ELF1_RUCKSACK.clear();
		ELF2_RUCKSACK.clear();
		ELF3_RUCKSACK.clear();
		return priority;
	}

	@Override
	public String getInputFileName() {
		return "2022/input_3";
	}


}
