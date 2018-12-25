package com.vn.advent.solution;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day25 implements Solution {

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day25();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {
		List<Point> points = lines.map(Point::new)
			.collect(Collectors.toList());
		Set<Set<Point>> constellations = new HashSet<>();
		System.out.println();
		for (Point p : points) {
			boolean pAddedToAConstellation = false;
			Set<Set<Point>> setOfConstellationsToBeMerged = new HashSet<>();
			for (Set<Point> constellation : constellations) {
				boolean added = addData(constellation, p);
				if (added) {
					pAddedToAConstellation = true;
					setOfConstellationsToBeMerged.add(constellation);
				}
			}
			if (setOfConstellationsToBeMerged.size() > 1) {
				Set<Point> mergedConstellation = new HashSet<>();
				setOfConstellationsToBeMerged.stream()
					.forEach(mergedConstellation::addAll);
				Set<Set<Point>> tmpConst = new HashSet<>(constellations);
				constellations = tmpConst;
				setOfConstellationsToBeMerged.stream()
					.forEach(constellations::remove);
				constellations.add(mergedConstellation);
			}
			if (!pAddedToAConstellation) {
				// Create a new constellation with the point and add it to set
				// of constellations
				constellations.add(new HashSet<Point>(Arrays.asList(p)));
			}
		}
		System.out.print(constellations.size());
	}

	private boolean addData(Set<Point> constellation, Point point) {
		boolean added = false;
		for (Point p : constellation) {
			if (p.distanceTo(point) <= 3) {
				p.neighbors.add(point);
				point.neighbors.add(p);
				added = true;
			}
		}
		if (added) {
			constellation.add(point);
		}
		return added;
	}

	static class Point {
		final int x, y, z, t;

		Set<Point> neighbors = new HashSet<>();

		Point(String str) {
			String s[] = str.split(",");
			this.x = Integer.parseInt(s[0]);
			this.y = Integer.parseInt(s[1]);
			this.z = Integer.parseInt(s[2]);
			this.t = Integer.parseInt(s[3]);
		}

		int distanceTo(Point other) {
			return Math.abs(other.x - x) + Math.abs(other.y - y) + Math.abs(other.z - z) + Math.abs(other.t - t);
		}

		@Override
		public int hashCode() {
			return Objects.hash(x, y, z, t);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			if (t != other.t)
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			if (z != other.z)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "<" + x + "," + y + "," + z + "," + t + ">";
		}
	}

	@Override
	public void partTwo(Stream<String> lines) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getInputFileName() {
		return "input_25";
	}
}
