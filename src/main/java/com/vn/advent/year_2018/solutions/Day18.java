package com.vn.advent.year_2018.solutions;

import com.vn.advent.Solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day18 implements Solution {

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day18();
		System.out.println(solution.run());
	}

	@Override
	public String partOne(Stream<String> lines) {
		List<String> allLines = lines.collect(Collectors.toList());
		int y = 0;
		for (String line : allLines) {
			char[] acresInLine = line.toCharArray();
			for (int x = 0; x < acresInLine.length; x++) {
				char c = acresInLine[x];
				Coordinates loc = new Coordinates(x, y);
				Acre.acres.put(loc, Acre.of(c, loc));
			}
			y++;
		}
		for (int i = 1; i <= 10; i++) {
			Map<Coordinates, Acre> changedLandscape = Acre.acres.values()
				.stream()
				.parallel()
				.map(Acre::getNewValue)
				.collect(Collectors.toMap(Acre::getCoordinates,
						Function.identity()));
			Acre.acres.clear();
			Acre.acres.putAll(changedLandscape);
		}

		long countLumberYard = Acre.acres.values()
			.stream()
			.map(Acre::type)
			.filter(Type.LUMBERYARD::equals)
			.count();
		long countAcresWithTrees = Acre.acres.values()
			.stream()
			.map(Acre::type)
			.filter(Type.TREES::equals)
			.count();
		return String.valueOf(countLumberYard * countAcresWithTrees);
	}

	@Override
	public String partTwo(Stream<String> lines) {
		return "A pattern problem for large input - so solved manually/mathematically";
	}

	static interface Acre {
		public static final Map<Coordinates, Acre> acres = new TreeMap<>(
				Comparator.comparing(Coordinates::getY)
					.thenComparing(Coordinates::getX));

		Type type();

		Coordinates getCoordinates();

		Predicate<Acre> isGroundAndSurroundedBy3orMoreAcresWithTrees = a -> {
			boolean isGround = a.type() == Type.GROUND;
			long count = Acre.neighbors(a)
				.stream()
				.map(Acre::type)
				.filter(Type.TREES::equals)
				.count();
			boolean isSurroundedBy3orMoreAcresWithTrees = count >= 3;
			return isGround && isSurroundedBy3orMoreAcresWithTrees;
		};
		Predicate<Acre> isTreesAndSurroundedBy3orMoreLumberYards = a -> {
			boolean isTrees = a.type() == Type.TREES;
			long count = Acre.neighbors(a)
				.stream()
				.map(Acre::type)
				.filter(Type.LUMBERYARD::equals)
				.count();
			boolean isSurroundedBy3orMoreLumberYards = count >= 3;
			return isTrees && isSurroundedBy3orMoreLumberYards;
		};

		Predicate<Acre> isLumberYardAndNotSurroundedBy1orMoreLumberYardAnd1OrMoreAcresWithTrees = a -> {
			boolean isLumberYard = a.type() == Type.LUMBERYARD;
			long countLumberYard = Acre.neighbors(a)
				.stream()
				.map(Acre::type)
				.filter(Type.LUMBERYARD::equals)
				.count();
			long countAcresWithTrees = Acre.neighbors(a)
				.stream()
				.map(Acre::type)
				.filter(Type.TREES::equals)
				.count();
			boolean isSurroundedBy1orMoreLumberYardAnd1OrMoreAcresWithTrees = countLumberYard >= 1
					&& countAcresWithTrees >= 1;
			return isLumberYard
					&& !isSurroundedBy1orMoreLumberYardAnd1OrMoreAcresWithTrees;
		};

		default Acre getNewValue() {
			Acre a = this;
			Coordinates c = a.getCoordinates();
			if (isGroundAndSurroundedBy3orMoreAcresWithTrees.test(a)) {
				a = new Trees(c);
			} else if (isTreesAndSurroundedBy3orMoreLumberYards.test(a)) {
				a = new LumberYard(c);
			} else if (isLumberYardAndNotSurroundedBy1orMoreLumberYardAnd1OrMoreAcresWithTrees
				.test(a)) {
				a = new Ground(c);
			}
			return a;
		}

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

		static List<Acre> neighbors(Acre a) {
			return a.getCoordinates()
				.getNeighbors()
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

		@Override
		public String toString() {
			return ".";
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

		@Override
		public String toString() {
			return "|";
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

		@Override
		public String toString() {
			return "#";
		}
	}

	@Override
	public String getInputFileName() {
		return "2018/input_18";
	}

	static enum Type {
		GROUND, TREES, LUMBERYARD
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
			return new Coordinates(x + 1, y - 1);
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

		int getX() {
			return x;
		}

		int getY() {
			return y;
		}

		@Override
		public int hashCode() {
			return Objects.hash(x, y);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Coordinates other = (Coordinates) obj;
			if (x != other.x) {
				return false;
			}
			if (y != other.y) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return x + "," + y;
		}
	}
}
