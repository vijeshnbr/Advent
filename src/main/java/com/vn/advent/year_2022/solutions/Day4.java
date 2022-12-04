package com.vn.advent.year_2022.solutions;

import com.vn.advent.Solution;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day4 implements Solution {

	private static final Pattern PATTERN = Pattern.compile("\\d+");
	private static List<Pair<Range>> ALL_ELF_PAIRS = null;
	private static final Predicate<Pair<Range>> IS_ANY_RANGE_OVERLAP = pair -> Range.isAnyRangeOverlap(pair.first(), pair.second());
	private static final Predicate<Pair<Range>> IS_ANY_RANGE_INCLUSIVE = pair -> Range.isAnyRangeInclusive(pair.first(), pair.second());

	public static void main(String[] args) {
		Solution solution = new Day4();
		System.out.println(solution.run());
	}

	public String partOne(Stream<String> lines) {
		initializeListOfElfPairs(lines);

		return String.valueOf(ALL_ELF_PAIRS
				.stream()
				.filter(IS_ANY_RANGE_INCLUSIVE)
				.count());
	}

	public String partTwo(Stream<String> lines) {
		return String.valueOf(ALL_ELF_PAIRS
				.stream()
				.filter(IS_ANY_RANGE_OVERLAP.negate())
				.count());
	}

	@Override
	public String getInputFileName() {
		return "2022/input_4";
	}

	private void initializeListOfElfPairs(Stream<String> lines) {
		ALL_ELF_PAIRS = lines
				.map(line -> PATTERN
						.matcher(line)
						.results()
						.map(MatchResult::group)
						.toList())
				.map(matches -> new Pair<Range>(
						new Range(Integer.valueOf(matches.get(0)), Integer.valueOf(matches.get(1))),
						new Range(Integer.valueOf(matches.get(2)), Integer.valueOf(matches.get(3)))
				))
				.toList();
	}

	private record Range(Integer lower, Integer upper) {
		static boolean isAnyRangeInclusive(Range one, Range two) {
			return one.isWithin(two) || two.isWithin(one);
		}

		static boolean isAnyRangeOverlap(Range one, Range two) {
			return two.lower>one.upper || one.lower>two.upper;
		}
		public boolean isWithin(Range given) {
			return this.lower>=given.lower && this.upper<=given.upper;
		}
	}

	private record Pair<T>(T first, T second) {}

}
