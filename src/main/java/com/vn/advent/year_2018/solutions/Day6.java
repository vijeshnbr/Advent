package com.vn.advent.year_2018.solutions;

import com.vn.advent.Solution;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day6 implements Solution {

	public static final int RANGE = 10000;

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day6();
		System.out.println(solution.run());
	}

	@Override
	public String partOne(Stream<String> lines) {
		Set<Coordinates> setOfCoordinates = lines.map(Coordinates::new)
			.collect(Collectors.toSet());

		int minX = setOfCoordinates.stream()
			.map(c -> c.x)
			.min(Integer::compare)
			.get();
		int minY = setOfCoordinates.stream()
			.map(c -> c.y)
			.min(Integer::compare)
			.get();
		int maxX = setOfCoordinates.stream()
			.map(c -> c.x)
			.max(Integer::compare)
			.get();
		int maxY = setOfCoordinates.stream()
			.map(c -> c.y)
			.max(Integer::compare)
			.get();

		int rowSize = maxY - minY + 1;
		int colSize = maxX - minX + 1;

		final Square[][] grid = new Square[rowSize][colSize];

		// Populate grid with closest loc coordinates or empty coordinates if
		// tied
		for (int row = 0; row < rowSize; row++) {
			for (int col = 0; col < colSize; col++) {
				final Coordinates positionInGrid = new Coordinates(col + minX,
						row + minY);
				grid[row][col] = getClosestLocation(positionInGrid,
						setOfCoordinates);
			}
		}

		Map<Coordinates, List<Square>> locationAndArea = Arrays.stream(grid)
			.flatMap(rowArr -> Arrays.stream(rowArr))
			.filter(square -> !square.isOnBoundary(minX, minY, maxX, maxY))
			.filter(Square::isNotTied)
			.collect(Collectors.groupingBy(Square::getClosestLocation));

		return String.valueOf(locationAndArea.values()
			.stream()
			.map(List::size)
			.max(Integer::compare)
			.get());

	}

	@Override
	public String partTwo(Stream<String> lines) {
		Set<Coordinates> setOfCoordinates = lines.map(Coordinates::new)
			.collect(Collectors.toSet());

		int minX = setOfCoordinates.stream()
			.map(c -> c.x)
			.min(Integer::compare)
			.get();
		int minY = setOfCoordinates.stream()
			.map(c -> c.y)
			.min(Integer::compare)
			.get();
		int maxX = setOfCoordinates.stream()
			.map(c -> c.x)
			.max(Integer::compare)
			.get();
		int maxY = setOfCoordinates.stream()
			.map(c -> c.y)
			.max(Integer::compare)
			.get();

		int rowSize = maxY - minY + 1;
		int colSize = maxX - minX + 1;

		final Square[][] grid = new Square[rowSize][colSize];

		// Populate grid with sum of distances to all Locations if in range or
		// empty coordinates if
		// not in range
		for (int row = 0; row < rowSize; row++) {
			for (int col = 0; col < colSize; col++) {
				final Coordinates positionInGrid = new Coordinates(col + minX,
						row + minY);
				grid[row][col] = getSumOfDistancesToAllLocations(positionInGrid,
						setOfCoordinates, RANGE);
			}
		}

		long regionSize = Arrays.stream(grid)
			.flatMap(rowArr -> Arrays.stream(rowArr))
			.filter(square -> square.sumOfDistancesToAllLocations.isPresent())
			.count();
		return String.valueOf(regionSize);
	}

	private Square getSumOfDistancesToAllLocations(Coordinates positionInGrid,
			Set<Coordinates> setOfCoordinates, int range) {
		Square s = new Square(positionInGrid);
		int sum = setOfCoordinates.stream()
			.mapToInt(c -> Coordinates.distanceBetween(c, positionInGrid))
			.sum();
		if (sum < 10000) {
			s = new Square(positionInGrid, sum);
		}
		return s;
	}

	private Square getClosestLocation(Coordinates positionInGrid,
			Set<Coordinates> setOfCoordinates) {

		Optional<Integer> min = setOfCoordinates.stream()
			.map(location -> Coordinates.distanceBetween(positionInGrid,
					location))
			.min(Integer::compare);
		return min.map(dist -> {
			Square s = new Square(positionInGrid);
			List<Coordinates> closestLocations = setOfCoordinates.stream()
				.filter(location -> Coordinates.distanceBetween(positionInGrid,
						location) == dist)
				.collect(Collectors.toList());
			if (closestLocations.size() == 1) {
				s = new Square(positionInGrid, closestLocations.get(0));
			}
			return s;
		})
			.get();
	}

	/*
	 * Intention - square will either have the coordinates of closest location
	 * or have empty coordinates if there is a tie
	 */
	static class Square {
		final Optional<Coordinates> closestLocation;
		final Coordinates positionInGrid;
		final Optional<Integer> sumOfDistancesToAllLocations;

		Square(Coordinates positionInGrid, Coordinates closestLocation) {
			this.positionInGrid = positionInGrid;
			this.closestLocation = Optional.of(closestLocation);
			this.sumOfDistancesToAllLocations = Optional.empty();
		}

		Square(Coordinates positionInGrid) {
			this.positionInGrid = positionInGrid;
			this.closestLocation = Optional.empty();
			this.sumOfDistancesToAllLocations = Optional.empty();
		}

		Square(Coordinates positionInGrid,
				Integer sumOfDistancesToAllLocations) {
			this.positionInGrid = positionInGrid;
			this.closestLocation = Optional.empty();
			this.sumOfDistancesToAllLocations = Optional
				.of(sumOfDistancesToAllLocations);
		}

		boolean isOnBoundary(int minX, int minY, int maxX, int maxY) {
			int x = positionInGrid.x;
			int y = positionInGrid.y;
			if (x == minX || x == maxX || y == minY || y == maxY) {
				return true;
			}
			return false;
		}

		public Coordinates getClosestLocation() {
			return closestLocation.get();
		}

		boolean isNotTied() {
			return closestLocation.isPresent();
		}
	}

	static class Coordinates {
		final int x;
		final int y;

		static final Map<Coordinates[], Integer> CACHE_DISTANCE_BETWEENS = new ConcurrentHashMap<>();

		Coordinates(int x, int y) {
			this.x = x;
			this.y = y;
		}

		Coordinates(String coordinates) {
			String[] arr = coordinates.split(",");
			this.x = Integer.parseInt(arr[0].trim());
			this.y = Integer.parseInt(arr[1].trim());
		}

		private static Integer distanceBetween(Coordinates c1, Coordinates c2) {
			Coordinates[] arr = new Coordinates[]{c1, c2};
			Function<Coordinates[], Integer> calcDistance = c -> Math
				.abs(c[0].x - c[1].x) + Math.abs(c[0].y - c[1].y);
			return CACHE_DISTANCE_BETWEENS.computeIfAbsent(arr, calcDistance);
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
	}

	@Override
	public String getInputFileName() {
		return "2018/input_6";
	}

}
