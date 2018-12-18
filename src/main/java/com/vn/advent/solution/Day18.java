package com.vn.advent.solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day18 implements Solution {

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day18();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {
		List<String> allLines = lines.collect(Collectors.toList());
		int y = 0;
		for (String line : allLines) {
			char[] acresInLine = line.toCharArray();
			for (int x = 0; x < acresInLine.length; x++) {
				char c = acresInLine[x];
				Coordinates loc = new Coordinates(x, y);
				Acre.acres.put(loc, Acre.of(c, loc));
			}
		}
	}

	@Override
	public void partTwo(Stream<String> lines) {

	}

	static enum Type {
		GROUND, TREES, LUMBERYARD
	}

	static interface Acre {
		public static final Map<Coordinates, Acre> acres = new HashMap<>();

		Type type();

		Coordinates getCoordinates();

		static Acre of(char c, Coordinates loc) {
			Acre a = null;
			if (c == '.') {
				a = new Ground(loc);
			} else if (c == '#') {
				a = new LumberYard(loc);
			} else if (c == '|') {
				a = new Trees(loc);
			}
			return a;
		}

		static List<Acre> neighbors(Coordinates c) {
			return c.getNeighbors()
				.stream()
				.map(acres::get)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
		}
	}

	static class Ground implements Acre {

		Coordinates c;

		Ground(Coordinates c) {
			this.c = c;
		}

		@Override
		public Type type() {
			return Type.GROUND;
		}

		@Override
		public Coordinates getCoordinates() {
			return this.c;
		}

	}

	static class Trees implements Acre {
		Coordinates c;

		Trees(Coordinates c) {
			this.c = c;
		}

		@Override
		public Type type() {
			return Type.TREES;
		}

		@Override
		public Coordinates getCoordinates() {
			return this.c;
		}

	}

	static class LumberYard implements Acre {
		Coordinates c;

		LumberYard(Coordinates c) {
			this.c = c;
		}

		@Override
		public Type type() {
			return Type.LUMBERYARD;
		}

		@Override
		public Coordinates getCoordinates() {
			return this.c;
		}

	}

	@Override
	public String getInputFileName() {
		return "test_input";
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

		public Coordinates upleft() {
			return new Coordinates(x - 1, y - 1);
		}

		public Coordinates upright() {
			return new Coordinates(x + 1, y + 1);
		}

		public Coordinates downleft() {
			return new Coordinates(x - 1, y + 1);
		}

		public Coordinates downright() {
			return new Coordinates(x + 1, y + 1);
		}

		public List<Coordinates> getNeighbors() {
			List<Coordinates> list = new ArrayList<>();
			list.add(up());
			list.add(down());
			list.add(left());
			list.add(right());
			list.add(upleft());
			list.add(downleft());
			list.add(upright());
			list.add(downright());
			return list;
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

}
