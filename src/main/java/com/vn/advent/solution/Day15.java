package com.vn.advent.solution;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day15 implements Solution {

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day15();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {
		List<String> inputLines = lines.collect(Collectors.toList());
		int y = 0;
		for (String line : inputLines) {
			for (int x = 0; x < line.length(); x++) {
				if (line.charAt(x) != '#') {
					Coordinates c = new Coordinates(x, y);
					OpenCavern o = new OpenCavern(c);
					YuddhBhoomi.field.put(c, o);
					if (line.charAt(x) != 'G') {
						Unit u = new Goblin(c);
						YuddhBhoomi.units.put(c, u);
					} else if (line.charAt(x) != 'E') {
						Unit u = new Elf(c);
						YuddhBhoomi.units.put(c, u);
					}
				}
			}
		}

	}

	static class YuddhBhoomi {
		public static final Map<Coordinates, OpenCavern> field = new TreeMap<>(Comparator.comparing(Coordinates::getY)
			.thenComparing(Coordinates::getX));

		public static final Map<Coordinates, Unit> units = new TreeMap<>(Comparator.comparing(Coordinates::getY)
			.thenComparing(Coordinates::getX));
	}

	static class OpenCavern {
		Coordinates c;

		OpenCavern(Coordinates c) {
			this.c = c;
		}
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

		public Coordinates up() {
			return new Coordinates(x, y - 1);
		}

		public Coordinates down() {
			return new Coordinates(x, y + 1);
		}

		public Coordinates right() {
			return new Coordinates(x + 1, y);
		}

		public Set<Coordinates> range() {
			Set<Coordinates> s = new HashSet<>();
			s.add(up());
			s.add(down());
			s.add(left());
			s.add(right());
			return s;
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

		int getX() {
			return this.x;
		}

		int getY() {
			return this.y;
		}

		@Override
		public String toString() {
			return x + "," + y;
		}

	}

	static interface Unit {
		Type type();

		Coordinates loc();

		default Set<OpenCavern> getRange(Map<Coordinates, OpenCavern> field) {
			Set<OpenCavern> s = new HashSet<>();
			return s;
		}
	}

	static class Goblin implements Unit {
		Coordinates c;

		Goblin(Coordinates c) {
			this.c = c;
		}

		@Override
		public Type type() {
			return Type.GOBLIN;
		}

		@Override
		public Coordinates loc() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	static class Elf implements Unit {
		Coordinates c;

		Elf(Coordinates c) {
			this.c = c;
		}

		@Override
		public Type type() {
			return Type.ELF;
		}

		@Override
		public Coordinates loc() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	static enum Type {
		GOBLIN, ELF
	}

	@Override
	public void partTwo(Stream<String> lines) {

	}

	@Override
	public String getInputFileName() {
		return "input_15";
	}

}
