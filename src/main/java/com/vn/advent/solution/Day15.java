package com.vn.advent.solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day15 implements Solution {

	private static final Map<Coordinates, OpenCavern> BATTLEFIELD = new TreeMap<>(
			Coordinates.compareLocations);
	private static final Map<Coordinates, Unit> ALL_INITIAL_UNITS = new TreeMap<>(
			Coordinates.compareLocations);
	private static final Predicate<Stats> ELVES_WON_AND_NO_ELVES_DIED = stats -> stats
		.winner() == Type.ELF && stats.elfNotDead();

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day15();
		System.out.println(solution.run());
	}

	@Override
	public String partOne(Stream<String> lines) {
		initializeBattlefieldAndUnits(lines);
		Stats stats = battle();
		LOGGER.log(Level.INFO, "{0}", stats);
		return String.valueOf(stats.outcome());
	}

	@Override
	public String partTwo(Stream<String> lines) {
		initializeBattlefieldAndUnits(lines);
		int elfPowerMin = 4;
		int elfPowerMax = Integer.MAX_VALUE - elfPowerMin;

		Stats statsMax;
		Stats statsMin;

		while (true) {
			Elf.AP = elfPowerMax;
			statsMax = battle();
			LOGGER.info("Max Stats ElfPower: " + elfPowerMax + ", " + statsMax
					+ ", elfNotDead: " + statsMax.elfNotDead());
			Elf.AP = elfPowerMin;
			statsMin = battle();
			LOGGER.info("Min Stats ElfPower: " + elfPowerMin + ", " + statsMin
					+ ", elfNotDead: " + statsMin.elfNotDead());
			if (ELVES_WON_AND_NO_ELVES_DIED.test(statsMin)) {
				break;
			} else {
				if (ELVES_WON_AND_NO_ELVES_DIED.test(statsMax)) {
					elfPowerMax = (elfPowerMax + elfPowerMin) / 2;
					elfPowerMin++;
				} else {
					elfPowerMin = elfPowerMax;
					elfPowerMax = elfPowerMax * 2;
				}
			}
		}
		return String.valueOf(statsMin.outcome());
	}

	private Stats battle() {
		boolean combat = true;
		int rounds = 0;

		Map<Coordinates, Unit> remainingUnits = copyOfTreeMapOfAllInitialUnits();

		// Collect dead Units here
		Set<Unit> deadUnits = new HashSet<>();

		while (combat) {
			// Do rounds

			// List of Units participating in round in reading order is
			// initialized from remaining units at the beginning of every round
			List<Unit> units = new ArrayList<>(remainingUnits.values());

			// Above units list is iterated for turns but the original TreeMap
			// (remainingUnits) is modified whenever unit moves, attacks, dies
			for (Unit u : units) {

				// If unit already died in this round, continue to next unit
				if (deadUnits.contains(u))
					continue;

				// Set of enemies of the current unit in its turn
				Set<Unit> enemyUnits = remainingUnits.values()
					.stream()
					.filter(unit -> unit.type() == u.type()
						.enemy())
					.collect(Collectors.toSet());

				if (enemyUnits == null || enemyUnits.isEmpty()) {
					// Combat ends as no enemy found
					combat = false;
					break;
				}

				// See if enemy in range and attack
				boolean attacked = attackIfEnemyInRange(remainingUnits,
						enemyUnits, deadUnits, u);

				if (!attacked) {
					// If not attacked an enemy - then move according to below
					// make one best move towards the closest range of
					// closest enemy

					// Get ranges of all enemies that are not occupied by units
					Set<Coordinates> rangesOfAllEnemyUnits = enemyUnits.stream()
						.map(Unit::range)
						.flatMap(List::stream)
						.filter(c -> !remainingUnits.containsKey(c))
						.collect(Collectors.toSet());

					// BFS from unit until an item in above set is reached. Then
					// backtrack and move to location whose parent location is
					// unit's location.
					Coordinates start = u.loc();
					Queue<Coordinates> queue = new LinkedList<>();
					queue.add(start);
					Map<Coordinates, Coordinates> locationParentMap = new HashMap<>();

					// Put start coordinates with parent as null
					locationParentMap.put(start, null);

					Map<Coordinates, Coordinates> mapOfMoveToAndEnemyRange = new HashMap<>();
					Map<Coordinates, Integer> mapOfMoveToAndDistance = new HashMap<>();
					int maxDistance = Integer.MAX_VALUE;
					while (!queue.isEmpty()) {
						Coordinates curr = queue.poll();
						if (rangesOfAllEnemyUnits.contains(curr)) {
							// Search has reached target, therefore -
							// Find coordinates to move to, by backtracking
							// parent of curr recursively until node whose
							// parent is start
							Coordinates move = curr;
							int distance = 1;
							while (locationParentMap.get(move) != start) {
								move = locationParentMap.get(move);
								distance++;
							}
							// Don't break from BFS yet even though search
							// target is reached. Instead keep searching until
							// there are no more ties with shortest distance
							// (BFS gives shortest distance when all edges of
							// graph are equal)
							if (distance > maxDistance)
								// Break out of BFS - only when no more ties
								// with shortest distance
								break;
							maxDistance = distance;
							mapOfMoveToAndEnemyRange.put(move, curr);
							mapOfMoveToAndDistance.put(move, distance);
						} else {
							// Continue to search neighbors (coordinates in
							// range of curr) - check if they are open caverns
							// (not occupied by remaining units), check if they
							// haven't been visited before, and add them to
							// queue in reading order. Update location-parent
							// map.
							curr.rangeOrdered()
								.stream()
								.filter(BATTLEFIELD::containsKey)
								.filter(c -> !locationParentMap.containsKey(c))
								.filter(c -> !remainingUnits.containsKey(c))
								.forEach(c -> {
									locationParentMap.put(c, curr);
									queue.offer(c);
								});
						}
					}

					// Move after break from BFS
					// To choose where to move has to be done carefully as ties
					// in shortest paths should be broken by choosing the path
					// that leads to a square in range of enemy - such that
					// square is first in reading order among all tied cases
					Optional<Coordinates> moveTo = mapOfMoveToAndEnemyRange
						.entrySet()
						.stream()
						.min(Map.Entry
							.comparingByValue(Coordinates.compareLocations))
						.map(Map.Entry::getKey);
					if (moveTo.isPresent()) {
						Coordinates move = moveTo.get();
						u.moveTo(move);
						remainingUnits.remove(start);
						remainingUnits.put(move, u);

						// Extra logic - if unit moves into range of enemy
						// in its current turn, then don't end turn yet, but
						// attack enemy and end turn
						if (move.equals(mapOfMoveToAndEnemyRange.get(move))) {
							attackIfEnemyInRange(remainingUnits, enemyUnits,
									deadUnits, u);
						}
					}
				}
			}
			if (combat)
				rounds++;
			else
				break;
			LOGGER.info(print(rounds, remainingUnits));
		}
		Stats stats = new Stats(rounds, remainingUnits, deadUnits);
		return stats;
	}

	static class Stats {
		final int rounds;
		final Map<Coordinates, Unit> remainingUnits;
		final Set<Unit> deadUnits;

		Stats(int rounds, Map<Coordinates, Unit> remainingUnits,
				Set<Unit> deadUnits) {
			this.rounds = rounds;
			this.remainingUnits = remainingUnits;
			this.deadUnits = deadUnits;
		}

		int outcome() {
			return rounds * remainingUnits.values()
				.stream()
				.mapToInt(Unit::hp)
				.sum();
		}

		boolean elfNotDead() {
			return deadUnits.stream()
				.filter(u -> u.type() == Type.ELF)
				.count() == 0;
		}

		Type winner() {
			return remainingUnits.values()
				.parallelStream()
				.findAny()
				.map(Unit::type)
				.get();
		}

		@Override
		public String toString() {
			return "Stats [rounds=" + rounds + ", winner=" + winner()
					+ ", outcome=" + outcome() + "]";
		}
	}

	private boolean attackIfEnemyInRange(Map<Coordinates, Unit> remainingUnits,
			Set<Unit> enemyUnits, Set<Unit> deadUnits, Unit u) {
		// Get closest enemy unit in range of current unit, if multiple
		// get enemy with fewest hit points, if tied - get first
		// in reading order. It is an optional as enemy may not be
		// present in unit's range.
		Optional<Unit> targetEnemy = u.range()
			.stream()
			.filter(remainingUnits::containsKey)
			.map(remainingUnits::get)
			.filter(enemyUnits::contains)
			.min(Unit.compareUnits);

		// if present attack target enemy
		targetEnemy.ifPresent(enemy -> {
			u.attack(enemy);
			if (enemy.hp() <= 0) {
				// Enemy died - add to set of dead units. Remove
				// from remainingUnits
				deadUnits.add(enemy);
				remainingUnits.remove(enemy.loc());
			}
		});
		return targetEnemy.isPresent();
	}

	private String print(int rounds, Map<Coordinates, Unit> remainingUnits) {
		StringBuilder s = new StringBuilder();
		s.append("Round " + rounds)
			.append("\n");
		for (int y = 0; y < 32; y++) {
			List<Unit> unitsInRow = new ArrayList<>();
			for (int x = 0; x < 32; x++) {
				Coordinates c = new Coordinates(x, y);
				if (!BATTLEFIELD.containsKey(c))
					s.append('#');
				else {
					if (!remainingUnits.containsKey(c)) {
						s.append('.');
					} else {
						Unit unit = remainingUnits.get(c);
						unitsInRow.add(unit);
						if (unit.type() == Type.GOBLIN)
							s.append('G');
						else if (unit.type() == Type.ELF)
							s.append('E');
					}
				}
			}
			s.append("\t" + unitsInRow)
				.append("\n");
		}
		return s.toString();
	}

	static interface Unit {

		Comparator<Unit> compareUnits = Comparator.comparing(Unit::hp)
			.thenComparing(Unit::loc, Coordinates.compareLocations);

		Type type();

		void moveTo(Coordinates move);

		Coordinates loc();

		int hp();

		void defend(Unit enemy);

		int getAP();

		// Return copy object of Unit
		Unit copyOf();

		default List<Coordinates> range() {
			return loc().range()
				.stream()
				.collect(Collectors.toList());
		}

		default void attack(Unit enemy) {
			enemy.defend(this);
		}
	}

	static class Goblin implements Unit {
		private static final int AP = 3;
		Coordinates loc;
		// id field used to identify Unit is the initial coordinates of the Unit
		// when the Battlefield was initialized
		final Coordinates id;

		final Type t;

		int hp = 200;

		Goblin(Coordinates id) {
			this.id = id;
			// Initial location is same as id
			this.loc = id;
			this.t = Type.GOBLIN;
		}

		@Override
		public Unit copyOf() {
			Goblin copy = new Goblin(id);
			copy.loc = loc;
			copy.hp = hp;
			return copy;
		}

		@Override
		public Type type() {
			return t;
		}

		@Override
		public Coordinates loc() {
			return loc;
		}

		@Override
		public int hp() {
			return hp;
		}

		@Override
		public void defend(Unit enemy) {
			hp = hp - enemy.getAP();
		}

		@Override
		public void moveTo(Coordinates move) {
			this.loc = move;
		}

		@Override
		public int hashCode() {
			return Objects.hash(id);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Goblin other = (Goblin) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "G<(" + loc + ")," + hp + ">";
		}

		@Override
		public int getAP() {
			return AP;
		}

	}

	static class Elf implements Unit {
		public static int AP = 3;
		Coordinates loc;

		final Coordinates id;

		final Type t;

		int hp = 200;

		Elf(Coordinates id) {
			this.id = id;
			// Initial location is same as id
			this.loc = id;
			this.t = Type.ELF;
		}

		@Override
		public Unit copyOf() {
			Elf copy = new Elf(id);
			copy.loc = loc;
			copy.hp = hp;
			return copy;
		}

		@Override
		public Type type() {
			return t;
		}

		@Override
		public Coordinates loc() {
			return loc;
		}

		@Override
		public int hp() {
			return hp;
		}

		@Override
		public void defend(Unit enemy) {
			hp = hp - enemy.getAP();
		}

		@Override
		public void moveTo(Coordinates move) {
			this.loc = move;
		}

		@Override
		public int hashCode() {
			return Objects.hash(id);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Elf other = (Elf) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "E<(" + loc + ")," + hp + ">";
		}

		@Override
		public int getAP() {
			return AP;
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

	static class OpenCavern {
		Coordinates c;

		OpenCavern(Coordinates c) {
			this.c = c;
		}
	}

	static class Coordinates {

		public static final Comparator<Coordinates> compareLocations = Comparator
			.comparing(Coordinates::getY)
			.thenComparing(Coordinates::getX);

		final int x, y;

		Coordinates(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public Coordinates up() {
			return new Coordinates(x, y - 1);
		}

		public Coordinates left() {
			return new Coordinates(x - 1, y);
		}

		public Coordinates right() {
			return new Coordinates(x + 1, y);
		}

		public Coordinates down() {
			return new Coordinates(x, y + 1);
		}

		int getX() {
			return x;
		}

		int getY() {
			return y;
		}

		public Set<Coordinates> range() {
			Set<Coordinates> s = new HashSet<>();
			s.add(up());
			s.add(left());
			s.add(right());
			s.add(down());
			return s;
		}

		public List<Coordinates> rangeOrdered() {
			List<Coordinates> l = new ArrayList<>();
			l.add(up());
			l.add(left());
			l.add(right());
			l.add(down());
			return l;
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

	private Map<Coordinates, Unit> copyOfTreeMapOfAllInitialUnits() {
		Map<Coordinates, Unit> remainingUnits = new TreeMap<>(
				Coordinates.compareLocations);
		ALL_INITIAL_UNITS.entrySet()
			.stream()
			.forEach(e -> remainingUnits.put(e.getKey(), e.getValue()
				.copyOf()));
		return remainingUnits;
	}

	private void initializeBattlefieldAndUnits(Stream<String> lines) {
		BATTLEFIELD.clear();
		ALL_INITIAL_UNITS.clear();
		List<String> inputLines = lines.map(String::trim)
			.map(s -> s.substring(0, (s.lastIndexOf('#') + 1)))
			.collect(Collectors.toList());
		int y = 0;
		for (String line : inputLines) {
			for (int x = 0; x < line.length(); x++) {
				if (line.charAt(x) != '#') {
					Coordinates c = new Coordinates(x, y);
					OpenCavern o = new OpenCavern(c);
					BATTLEFIELD.put(c, o);
					if (line.charAt(x) == 'G') {
						Unit u = new Goblin(c);
						ALL_INITIAL_UNITS.put(c, u);
					} else if (line.charAt(x) == 'E') {
						Unit u = new Elf(c);
						ALL_INITIAL_UNITS.put(c, u);
					}
				}
			}
			y++;
		}
	}

	@Override
	public String getInputFileName() {
		return "input_15";
	}
}
