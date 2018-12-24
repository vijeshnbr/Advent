package com.vn.advent.solution;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Day23 implements Solution {

	private static final Pattern PATTERN = Pattern.compile("\\<(.*)\\>, r=(\\d*)");

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day23();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {
		List<Nanobot> nanobots = lines.map(line -> {
			Matcher m = PATTERN.matcher(line);
			boolean find = m.find();
			Nanobot n = null;
			if (find) {
				n = new Nanobot(new Coordinates(m.group(1)), Integer.parseInt(m.group(2)));
			}
			return n;
		})
			.collect(Collectors.toList());

		final Nanobot max = nanobots.stream()
			.max(Comparator.comparing(Nanobot::getRadius))
			.get();

		System.out.println(nanobots.stream()
			.filter(n -> n.inRangeOf(max))
			.count());

	}

	@Override
	public void partTwo(Stream<String> lines) {

		final List<Nanobot> nanobots = lines.map(line -> {
			Matcher m = PATTERN.matcher(line);
			boolean find = m.find();
			Nanobot n = null;
			if (find) {
				n = new Nanobot(new Coordinates(m.group(1)), Integer.parseInt(m.group(2)));
			}
			return n;
		})
			.collect(Collectors.toList());

		LongSummaryStatistics summaryX = nanobots.stream()
			.map(Nanobot::getCoordinates)
			.mapToLong(Coordinates::getX)
			.summaryStatistics();
		long deltaX = Math.abs(summaryX.getMax() - summaryX.getMin());

		LongSummaryStatistics summaryY = nanobots.stream()
			.map(Nanobot::getCoordinates)
			.mapToLong(Coordinates::getY)
			.summaryStatistics();
		long deltaY = Math.abs(summaryY.getMax() - summaryY.getMin());

		LongSummaryStatistics summaryZ = nanobots.stream()
			.map(Nanobot::getCoordinates)
			.mapToLong(Coordinates::getZ)
			.summaryStatistics();
		long deltaZ = Math.abs(summaryZ.getMax() - summaryZ.getMin());

		long currentRadius = Math.max(deltaX, Math.max(deltaY, deltaZ));

		// immutable set - for fun :-)
		Set<Nanobot> currentNanobots = new HashSet<>(
				Arrays.asList(Nanobot.newInstance(Coordinates.ZERO, currentRadius)));
		currentNanobots = Collections.unmodifiableSet(currentNanobots);

		while (currentRadius > 0) {
			currentRadius = (currentRadius / 2) + ((currentRadius > 2) ? 1 : 0);
			long cr = currentRadius;
			List<Pair<Nanobot, Long>> nanobotAndCount = currentNanobots.stream()
				.flatMap(nb -> nb.c.neighbors(cr)
					.stream()
					.map(c -> new Nanobot(c, cr)))
				.map(nb -> new Pair<Nanobot, Long>(nb, nb.countOfIntersectingBots(nanobots)))
				.collect(Collectors.toList());

			Optional<Long> max = nanobotAndCount.stream()
				.map(Pair::second)
				.map(u -> u)
				.max(Long::compare);
			long maxIntersectionCount = max.isPresent() ? max.get() : 0;
			currentNanobots = nanobotAndCount.stream()
				.filter(nbc -> (nbc.second == maxIntersectionCount))
				.map(Pair::first)
				.map(t -> t)
				.collect(Collectors.toSet());
		}
		System.out.print(currentNanobots.stream()
			.map(nb -> nb.distanceToCoordinate(Coordinates.ZERO))
			.min(Long::compare)
			.get());
	}

	static class Pair<T, U> {
		final T first;
		final U second;

		Pair(T f, U s) {
			this.first = f;
			this.second = s;
		}

		T first() {
			return first;
		}

		U second() {
			return second;
		}
	}

	static class Nanobot {
		Coordinates c;
		long radius;

		Nanobot(Coordinates c, long radius) {
			this.c = c;
			this.radius = radius;
		}

		long getRadius() {
			return radius;
		}

		Coordinates getCoordinates() {
			return c;
		}

		static Nanobot newInstance(Coordinates c, long radius) {
			return new Nanobot(c, radius);
		}

		Long countOfIntersectingBots(List<Nanobot> inputNanobots) {
			return inputNanobots.stream()
				.filter(ib -> intersects(ib))
				.count();
		}

		boolean inRangeOf(Nanobot other) {
			return distanceToCoordinate(other.c) <= other.radius;
		}

		boolean coordinatesInRangeOf(Coordinates loc) {
			long distance = Math.abs(loc.x - c.x) + Math.abs(loc.y - c.y) + Math.abs(loc.z - c.z);
			return distance <= radius;
		}

		long distanceToCoordinate(Coordinates loc) {
			return Math.abs(loc.x - c.x) + Math.abs(loc.y - c.y) + Math.abs(loc.z - c.z);
		}

		boolean intersects(Nanobot other) {
			return distanceToCoordinate(other.c) <= radius + other.radius;
		}

		@Override
		public int hashCode() {
			return Objects.hash(c, radius);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Nanobot other = (Nanobot) obj;
			if (c == null) {
				if (other.c != null)
					return false;
			} else if (!c.equals(other.c))
				return false;
			if (radius != other.radius)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "c=" + c + ", radius=" + radius + ", distance=" + distanceToCoordinate(Coordinates.ZERO);
		}

	}

	static class Coordinates {
		static final Coordinates ZERO = new Coordinates("0,0,0");
		final long x, y, z;

		Coordinates(long x, long y, long z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		Coordinates(String c) {
			String[] split = c.split(",");
			this.x = Integer.parseInt(split[0]);
			this.y = Integer.parseInt(split[1]);
			this.z = Integer.parseInt(split[2]);
		}

		long getX() {
			return x;
		}

		long getY() {
			return y;
		}

		long getZ() {
			return z;
		}

		Set<Coordinates> neighbors(long delta) {
			return LongStream.rangeClosed(-1, 1)
				.boxed()
				.flatMap(xd -> {
					return LongStream.rangeClosed(-1, 1)
						.boxed()
						.flatMap(yd -> {
							return LongStream.rangeClosed(-1, 1)
								.mapToObj(zd -> {
									return new Coordinates(x + xd * delta, y + yd * delta, z + zd * delta);
								});
						});
				})
				.collect(Collectors.toSet());
		}

		@Override
		public int hashCode() {
			return Objects.hash(x, y, z);
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
			if (z != other.z)
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "[" + x + "," + y + "," + z + "]";
		}

	}

	@Override
	public String getInputFileName() {
		return "input_23";
	}

}
