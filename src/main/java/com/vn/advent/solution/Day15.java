package com.vn.advent.solution;

import java.util.Collection;
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
		boolean combat = true;
		int rounds = 0;
		while (combat) {
			// Do rounds
			Collection<Unit> units = YuddhBhoomi.units.values();
			Map<Type, List<Unit>> unitsByType = units.stream()
				.collect(Collectors.groupingBy(Unit::type));
			for (Unit u : units) {
				// Take turns in order - values of TreeMap will be ordered by
				// their keys which are ordered according to reading order as
				// specified in problem
				List<Unit> enemyUnits = unitsByType.get(u.type()
					.enemy());
				// check if enemy unit in range
				u.range()
					.stream()
					.map(YuddhBhoomi.units::get)
					.filter(Objects::nonNull)
					.filter(it -> it.type() == u.type()
						.enemy())
					.min(Comparator.comparing(Unit::hp)
						.thenComparing(Unit::loc));

				Set<Coordinates> rangesOfAllEnemyUnits = enemyUnits.stream()
					.map(Unit::range)
					.flatMap(Set::stream)
					.collect(Collectors.toSet());

			}
			rounds++;
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
		final Map<Coordinates, OpenCavern> field = YuddhBhoomi.field;

		final int AP = 3;

		Type type();

		Coordinates loc();

		int hp();

		void defend(Unit enemy);

		default Set<Coordinates> range() {
			return loc().range()
				.stream()
				.filter(field.keySet()::contains)
				.collect(Collectors.toSet());
		}

		default void attack(Unit enemy) {
			enemy.defend(this);
		}
	}

	static class Goblin implements Unit {
		Coordinates c;

		int hp = 200;

		Goblin(Coordinates c) {
			this.c = c;
		}

		@Override
		public Type type() {
			return Type.GOBLIN;
		}

		@Override
		public Coordinates loc() {
			return c;
		}

		@Override
		public int hp() {
			return hp;
		}

		@Override
		public void defend(Unit enemy) {
			hp = hp - enemy.AP;
		}

	}

	static class Elf implements Unit {
		Coordinates c;

		int hp = 200;

		Elf(Coordinates c) {
			this.c = c;
		}

		@Override
		public Type type() {
			return Type.ELF;
		}

		@Override
		public Coordinates loc() {
			return c;
		}

		@Override
		public int hp() {
			return hp;
		}

		@Override
		public void defend(Unit enemy) {
			hp = hp - enemy.AP;
		}

	}

	static enum Type {
		GOBLIN, ELF;
		Type enemy() {
			Type enemy = null;
			if (this == GOBLIN)
				enemy = ELF;
			if (this == ELF)
				enemy = GOBLIN;
			return enemy;
		}
	}

	@Override
	public void partTwo(Stream<String> lines) {

	}

	@Override
	public String getInputFileName() {
		return "input_15";
	}

}
