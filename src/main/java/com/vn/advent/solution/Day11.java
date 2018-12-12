package com.vn.advent.solution;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Stream;

public class Day11 implements Solution {

	private static final int GRID_SERIAL_NO = 1133;

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day11();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {
		Set<Coordinates> setOfCoordinatesThatForm3by3Grid = new HashSet<>();
		int size = 5;
		int xyMax = 300 - size + 1;
		for (int y = 1; y <= xyMax; y++) {
			for (int x = 1; x <= xyMax; x++) {
				Coordinates c = new Coordinates(x, y, size);
				setOfCoordinatesThatForm3by3Grid.add(c);
			}
		}
		setOfCoordinatesThatForm3by3Grid.stream()
			.max(Comparator.comparing(Coordinates::getPowerLevel))
			.ifPresent(System.out::print);
	}

	@Override
	public void partTwo(Stream<String> lines) {
		// List<Coordinates> listOfCoordinatesThatFormSquareGrids = new
		// ArrayList<>();
		// for (int size = 1; size <= 300; size++) {
		// int xyMax = 300 - size + 1;
		// for (int y = 1; y <= xyMax; y++) {
		// for (int x = 1; x <= xyMax; x++) {
		// Coordinates c = new Coordinates(x, y, size);
		// listOfCoordinatesThatFormSquareGrids.add(c);
		// }
		// }
		// }
		// listOfCoordinatesThatFormSquareGrids.stream()
		// .max(Comparator.comparing(Coordinates::getPowerLevel))
		// .ifPresent(System.out::print);
	}

	static class Coordinates {
		final int x, y, size;
		private static final Map<Coordinates, Integer> CACHE_POWERLEVEL = new ConcurrentHashMap<>();
		// A compute function that can be used to compute value to be cached if
		// key not in cache
		private static final Function<Coordinates, Integer> computePowerLevelOfOne = c -> {
			int rackId = c.getRackId();
			int power = c.y * rackId + GRID_SERIAL_NO;
			power *= rackId;
			power = (power / 100) % 10;
			power -= 5;
			return power;
		};

		private static final Map<KeyOfCoordinatesAndN, Set<Coordinates>> CACHE_SQUARES_FOR_A_COORDINATE = new ConcurrentHashMap<>();

		Coordinates(int x, int y, int size) {
			this.x = x;
			this.y = y;
			this.size = size;
		}
		Coordinates(int x, int y) {
			this.x = x;
			this.y = y;
			this.size = 0;
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

		private int getRackId() {
			return x + 10;
		}

		private int getPowerLevelOfOne() {
			return CACHE_POWERLEVEL.computeIfAbsent(this,
					computePowerLevelOfOne);
		}

		private Set<Coordinates> getNbyNAtThisCoordinate(int n) {

			final Function<KeyOfCoordinatesAndN, Set<Coordinates>> computeSetOfCoordinatesNbyNAtThisCoordinate = key -> {
				Set<Coordinates> set = new HashSet<>();
				if (key.n == 1)
					set.add(key.c);
				else {
					set.addAll(getNbyNAtThisCoordinate(key.n - 1));
					for (int i = key.c.x; i < key.c.x + key.n; i++) {
						set.add(new Coordinates(i, key.c.y + key.n - 1));
					}
					for (int j = key.c.y; j < key.c.y + key.n; j++) {
						set.add(new Coordinates(key.c.x + key.n - 1, j));
					}
				}
				return set;
			};

			Set<Coordinates> computeIfAbsent = CACHE_SQUARES_FOR_A_COORDINATE
				.computeIfAbsent(new KeyOfCoordinatesAndN(this, n),
						computeSetOfCoordinatesNbyNAtThisCoordinate);

			return computeIfAbsent;
		}

		int getPowerLevel() {
			return getNbyNAtThisCoordinate(size).stream()
				.mapToInt(Coordinates::getPowerLevelOfOne)
				.sum();
		}

		@Override
		public String toString() {
			return x + "," + y + "," + size;
		}

		static class KeyOfCoordinatesAndN {
			final Coordinates c;
			final int n;
			KeyOfCoordinatesAndN(Coordinates c, int n) {
				this.c = c;
				this.n = n;
			}
			@Override
			public int hashCode() {
				return Objects.hash(c, n);
			}
			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				KeyOfCoordinatesAndN other = (KeyOfCoordinatesAndN) obj;
				if (c == null) {
					if (other.c != null)
						return false;
				} else if (!c.equals(other.c))
					return false;
				if (n != other.n)
					return false;
				return true;
			}
			@Override
			public String toString() {
				return "KeyOfCoordinatesAndN [c=" + c + ", n=" + n + "]";
			}
		}

	}
	@Override
	public String getInputFileName() {
		return "input_11";
	}

}
