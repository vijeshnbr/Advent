package com.vn.advent.solution;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day10 implements Solution {

	private static final Pattern PATTERN = Pattern
		.compile("(.\\d+),\\s(.\\d+).*(.\\d+),\\s(.\\d+)");

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day10();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {
		final Set<Point> allPoints = lines.map(this::extractPoint)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toSet());
		int minX = allPoints.stream()
			.map(point -> point.p.x)
			.min(Integer::compare)
			.get();
		int minY = allPoints.stream()
			.map(point -> point.p.y)
			.min(Integer::compare)
			.get();
		int maxX = allPoints.stream()
			.map(point -> point.p.x)
			.max(Integer::compare)
			.get();
		int maxY = allPoints.stream()
			.map(point -> point.p.y)
			.max(Integer::compare)
			.get();

		int rowSize = maxY - minY + 1;
		int colSize = maxX - minX + 1;

		final Character[][] grid = new Character[rowSize][colSize];
		Set<Position> allPositions = allPoints.stream()
			.map(point -> point.p)
			.collect(Collectors.toSet());

		// Populate grid with all available points
		for (int row = 0; row < rowSize; row++) {
			for (int col = 0; col < colSize; col++) {
				int currX = col + minX;
				int currY = row + minY;
				Position p = new Position(currX, currY);
				if (allPositions.contains(p)) {
					grid[row][col] = '#';
				} else {
					grid[row][col] = '.';
				}
			}
		}
		System.out.println();
		print2DArray(grid);
	}

	private void print2DArray(Object[][] arr) {
		for (int row = 0; row < arr.length; row++) {
			System.out.println(Arrays.stream(arr[row])
				.map(Object::toString)
				.collect(Collectors.joining(" ")));
		}
	}

	private Optional<Point> extractPoint(String line) {
		Optional<Point> point = Optional.empty();
		Matcher m = PATTERN.matcher(line);
		if (m.find()) {
			Velocity v = new Velocity(Integer.parseInt(m.group(3)
				.trim()),
					Integer.parseInt(m.group(4)
						.trim()));
			Position p = new Position(Integer.parseInt(m.group(1)
				.trim()),
					Integer.parseInt(m.group(2)
						.trim()));
			point = Optional.of(new Point(p, v));
		}
		return point;
	}

	@Override
	public void partTwo(Stream<String> lines) {

	}

	static class Point {
		final Position p;
		final Velocity v;
		Point(Position p, Velocity v) {
			this.p = p;
			this.v = v;
		}
	}

	static class Position {
		final int x;
		final int y;
		Position(int x, int y) {
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
			Position other = (Position) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}

	static class Velocity {
		final int x;
		final int y;
		Velocity(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	@Override
	public String getInputFileName() {
		return "input_10";
	}

}
