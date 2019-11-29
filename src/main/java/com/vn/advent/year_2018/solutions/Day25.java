package com.vn.advent.year_2018.solutions;

import com.vn.advent.Solution;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day25 implements Solution {

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day25();
		System.out.println(solution.run());
	}

	@Override
	public String partOne(Stream<String> lines) {
		List<Point> points = lines.map(Point::new)
			.collect(Collectors.toList());
		Set<Constellation> constellations = new HashSet<>();
		for (Point p : points) {
			Set<Constellation> afterAdd = constellations.stream()
				.map(c -> c.add(p))
				.collect(Collectors.toSet());
			Optional<Constellation> merged = afterAdd.stream()
				.filter(c -> c.contains(p))
				.reduce(Constellation::merge);
			Set<Constellation> rest = afterAdd.stream()
				.filter(c -> !c.contains(p))
				.collect(Collectors.toSet());
			rest.add(merged.orElse(Constellation.of(p)));
			constellations = rest;
		}
		return String.valueOf(constellations.size());
	}

	@Override
	public String partTwo(Stream<String> lines) {
		return "TRIGGERED UNDERFLOW to RETURN BACK to 2018 - time for New Year 2019!!";
	}

	static class Constellation {
		final Set<Point> points;

		Constellation(Set<Point> points) {
			this.points = points;
		}

		static Constellation of(Point p) {
			Set<Point> setOfOnePoint = new HashSet<>();
			setOfOnePoint.add(p);
			return new Constellation(setOfOnePoint);
		}

		Constellation add(Point point) {
			Set<Point> morePoints = new HashSet<>(this.points);
			boolean added = false;
			for (Point p : morePoints) {
				if (p.distanceTo(point) <= 3) {
					p.neighbors.add(point);
					point.neighbors.add(p);
					added = true;
				}
			}
			if (added)
				morePoints.add(point);
			return new Constellation(morePoints);
		}

		boolean contains(Point p) {
			return this.points.contains(p);
		}

		Constellation merge(Constellation other) {
			Set<Point> allPoints = new HashSet<>(this.points);
			allPoints.addAll(other.points);
			return new Constellation(allPoints);
		}

		@Override
		public int hashCode() {
			return Objects.hash(points);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Constellation other = (Constellation) obj;
			if (points == null) {
				if (other.points != null)
					return false;
			} else if (!points.equals(other.points))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return points.toString();
		}
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
			return Math.abs(other.x - x) + Math.abs(other.y - y)
					+ Math.abs(other.z - z) + Math.abs(other.t - t);
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
	public String getInputFileName() {
		return "2018/input_25";
	}
}
