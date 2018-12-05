package com.vn.advent.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day3 implements Solution {

	private static Pattern PATTERN_NUM = Pattern.compile("\\d+");

	public static void main(String[] args) {
		Solution solution = new Day3();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {
		long areaClaimedMultipleTimes = lines.map(this::extractParams)
				.map(Claim::getAllCoordinates).flatMap(List::stream)
				.collect(Collectors.groupingBy(Function.identity(),
						Collectors.counting()))
				.values().stream().filter(count -> count > 1).count();
		System.out.print(areaClaimedMultipleTimes);
	}

	@Override
	public void partTwo(Stream<String> lines) {

		List<Claim> listOfAllClaims = lines.map(this::extractParams)
				.collect(Collectors.toList());
		Set<Coordinates> setOfCoordinatesWithSingleClaim = listOfAllClaims
				.stream().map(Claim::getAllCoordinates).flatMap(List::stream)
				.collect(Collectors.groupingBy(Function.identity(),
						Collectors.counting()))
				.entrySet().stream().filter(e -> e.getValue() == 1)
				.map(Map.Entry::getKey).collect(Collectors.toSet());

		listOfAllClaims.stream()
				.filter(claim -> checkIfClaimDoesntOverlap(claim,
						setOfCoordinatesWithSingleClaim))
				.map(claim -> claim.id).findFirst()
				.ifPresent(System.out::print);
	}

	private boolean checkIfClaimDoesntOverlap(Claim claim,
			Set<Coordinates> setOfCoordinatesWithSingleClaim) {
		return claim.getAllCoordinates().stream()
				.allMatch(coordinates -> setOfCoordinatesWithSingleClaim
						.contains(coordinates));
	}

	private Claim extractParams(String str) {
		Matcher m = PATTERN_NUM.matcher(str);
		List<String> groups = new ArrayList<>();
		while (m.find()) {
			groups.add(m.group());
		}
		int[] params = groups.stream().mapToInt(Integer::parseInt).toArray();
		return new Claim(params[0], new Coordinates(params[1], params[2]),
				params[3], params[4]);
	}

	private static class Claim {
		final int id;
		final Coordinates start;
		final int width;
		final int height;
		private Claim(int id, Coordinates start, int width, int height) {
			this.id = id;
			this.start = start;
			this.width = width;
			this.height = height;
		}
		@Override
		public String toString() {
			return "Claim [id=" + id + ", start=" + start + ", width=" + width
					+ ", height=" + height + "]";
		}

		List<Coordinates> getAllCoordinates() {
			List<Coordinates> allCoordinates = new ArrayList<>();
			for (int i = start.x; i < start.x + width; i++) {
				for (int j = start.y; j < start.y + height; j++) {
					allCoordinates.add(new Coordinates(i, j));
				}
			}
			return allCoordinates;
		}

	}

	private static class Coordinates {
		final int x;
		final int y;

		Coordinates(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public int hashCode() {
			return Objects.hash(x, y);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Coordinates other = (Coordinates) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Coordinates [x=" + x + ", y=" + y + "]";
		}
	}

	@Override
	public String getInputFileName() {
		return "input_3";
	}

}
