package com.vn.advent.year_2018.solutions;

import com.vn.advent.Solution;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day17 implements Solution {

	private static final Pattern COORDINATES_X_FIRST = Pattern
		.compile("x.(\\d+).{2}y.(\\d+)\\.{2}(\\d+)$");
	private static final Pattern COORDINATES_Y_FIRST = Pattern
		.compile("y.(\\d+).{2}x.(\\d+)\\.{2}(\\d+)$");

	private static final Map<Coordinates, Soil> clays = new HashMap<>();

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day17();
		System.out.println(solution.run());
	}

	@Override
	public String partOne(Stream<String> lines) {
		initialize(lines);
		IntSummaryStatistics summary = clays.keySet()
			.stream()
			.mapToInt(c -> c.y)
			.summaryStatistics();
		int minY = summary.getMin();
		int maxY = summary.getMax() + 1;
		Coordinates start = new Coordinates(500, minY);
		int waterySquares = calcArea(start, maxY);
		return String.valueOf(waterySquares);
	}

	@Override
	public String partTwo(Stream<String> lines) {
		initialize(lines);
		IntSummaryStatistics summary = clays.keySet()
			.stream()
			.mapToInt(c -> c.y)
			.summaryStatistics();
		int minY = summary.getMin();
		int maxY = summary.getMax() + 1;
		Coordinates start = new Coordinates(500, minY);
		Map<Coordinates, Soil> waterySoil = new HashMap<>();
		flow(start, waterySoil, maxY);
		long retainedWater = waterySoil.values()
			.stream()
			.map(Soil::type)
			.filter(Type.WATER::equals)
			.count();
		return String.valueOf(retainedWater);
	}

	private void initialize(Stream<String> lines) {
		clays.clear();
		clays.putAll(lines.map(this::mapToClayCoordinates)
			.flatMap(Set::stream)
			.distinct()
			.map(Clay::new)
			.collect(Collectors.toMap(Soil::getCoordinates,
					Function.identity())));
	}

	private int calcArea(Coordinates start, int maxY) {
		Map<Coordinates, Soil> waterySoil = new HashMap<>();

		flow(start, waterySoil, maxY);
		return waterySoil.size();
	}

	private void flow(final Coordinates c,
			final Map<Coordinates, Soil> waterySoil, final int maxY) {
		Coordinates curr = c;
		if (curr.y >= maxY)
			return;
		Set<Coordinates> setOfClays = clays.keySet();
		Predicate<Coordinates> neitherClayNorWater = testCoordinates -> {
			boolean notClay = (!setOfClays.contains(testCoordinates));
			boolean notWater = true;
			Soil soil = waterySoil.get(testCoordinates);
			if (soil != null) {
				if (soil.type() == Type.WATER) {
					notWater = false;
				}
			}
			return notClay && notWater;
		};
		while (neitherClayNorWater.test(curr)) {
			if (curr.y >= maxY)
				return;
			Soil s = new WetSand(curr);
			waterySoil.put(curr, s);
			curr = curr.down();
		}

		// found clay/water, so go up
		curr = curr.up();
		// go left and right and fill with wet sand
		// if down is not clay and not watery then it is a fresh tipping
		// problem, so call this method recursively with the down coordinate
		boolean tippedLeft = false;
		Coordinates left = curr.left();
		while (neitherClayNorWater.test(left)) {
			if (neitherClayNorWater.test(left.down())) {
				tippedLeft = true;
				flow(left, waterySoil, maxY);
			} else {
				Soil s = new WetSand(left);
				waterySoil.put(left, s);
				left = left.left();
			}
			if (tippedLeft)
				break;
		}
		boolean tippedRight = false;
		Coordinates right = curr.right();
		while (neitherClayNorWater.test(right)) {
			if (neitherClayNorWater.test(right.down())) {
				tippedRight = true;
				flow(right, waterySoil, maxY);
			} else {
				Soil s = new WetSand(right);
				waterySoil.put(right, s);
				right = right.right();
			}
			if (tippedRight)
				break;
		}

		if (!tippedLeft && !tippedRight) {
			// Change all WetSand in this line to Water
			for (Coordinates start = left.right(); !start
				.equals(right); start = start.right()) {
				Soil s = new Water(start);
				waterySoil.put(start, s);
			}

			// After filling line go up from curr and execute flow again
			Coordinates top = curr.up();
			flow(top, waterySoil, maxY);
		}

	}

	Set<Coordinates> mapToClayCoordinates(String str) {
		Set<Coordinates> clays = new HashSet<>();
		Matcher m = COORDINATES_X_FIRST.matcher(str);
		if (m.find()) {
			int yStart = Integer.parseInt(m.group(2));
			int yEnd = Integer.parseInt(m.group(3));
			int x = Integer.parseInt(m.group(1));

			for (int y = yStart; y <= yEnd; y++) {
				Coordinates c = new Coordinates(x, y);
				clays.add(c);
			}
		} else {
			m = COORDINATES_Y_FIRST.matcher(str);
			if (m.find()) {
				int xStart = Integer.parseInt(m.group(2));
				int xEnd = Integer.parseInt(m.group(3));
				int y = Integer.parseInt(m.group(1));

				for (int x = xStart; x <= xEnd; x++) {
					Coordinates c = new Coordinates(x, y);
					clays.add(c);
				}
			}
		}

		return clays;
	}

	static class Coordinates {
		final int x, y;

		Coordinates(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public Coordinates left() {
			return new Coordinates(x - 1, y);
		}

		public Coordinates right() {
			return new Coordinates(x + 1, y);
		}

		public Coordinates up() {
			return new Coordinates(x, y - 1);
		}

		public Coordinates down() {
			return new Coordinates(x, y + 1);
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
			return x + "," + y;
		}
	}

	static enum Type {
		CLAY, WATER, WETSAND
	}

	static interface Soil {

		Coordinates getCoordinates();

		Type type();
	}

	static class WetSand implements Soil {

		Coordinates c;

		WetSand(Coordinates c) {
			this.c = c;
		}

		@Override
		public Coordinates getCoordinates() {
			return c;
		}

		@Override
		public Type type() {
			return Type.WETSAND;
		}

		@Override
		public String toString() {
			return "type=" + type() + ", c=" + c;
		}

	}

	static class Clay implements Soil {
		final Coordinates c;

		Clay(Coordinates c) {
			this.c = c;
		}

		@Override
		public Coordinates getCoordinates() {
			return c;
		}

		@Override
		public Type type() {
			return Type.CLAY;
		}

		@Override
		public String toString() {
			return "type=" + type() + ", c=" + c;
		}

	}

	static class Water implements Soil {
		Coordinates c;

		Water(Coordinates c) {
			this.c = c;
		}

		@Override
		public Coordinates getCoordinates() {
			return c;
		}

		@Override
		public Type type() {
			return Type.WATER;
		}

		@Override
		public String toString() {
			return "type=" + type() + ", c=" + c;
		}

	}

	@Override
	public String getInputFileName() {
		return "2018/input_17";
	}

}
