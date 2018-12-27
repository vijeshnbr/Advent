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
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day15 implements Solution {

	private static final Map<Coordinates, OpenCavern> BATTLEFIELD = new TreeMap<>(Coordinates.compareLocations);

	private static final Map<Coordinates, Unit> ALL_INITIAL_UNITS = new TreeMap<>(Coordinates.compareLocations);

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day15();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {
		System.out.println();
		initializeBattlefieldAndUnits(lines);

		boolean combat = true;
		int rounds = 0;
		int sumOfHPsOfUnitsRemaining = 0;

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
					sumOfHPsOfUnitsRemaining = remainingUnits.values()
						.stream()
						.mapToInt(Unit::hp)
						.sum();
					break;
				}

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

				if (!targetEnemy.isPresent()) {
					// else make one best move towards the closest range of
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
					Set<Coordinates> visited = new HashSet<>();
					while (!queue.isEmpty()) {
						Coordinates curr = queue.poll();
						visited.add(curr);
						if (rangesOfAllEnemyUnits.contains(curr)) {
							// Search has reached target, therefore -
							// Find coordinates to move to, by backtracking
							// parent of curr recursively until node whose
							// parent is start
							Coordinates move = curr;
							while (locationParentMap.get(move) != start) {
								move = locationParentMap.get(move);
							}
							u.moveTo(move);
							remainingUnits.remove(start);
							remainingUnits.put(move, u);

							// Extra logic - if unit moves into range of enemy
							// in its current turn, then don't end turn yet, but
							// attack enemy and end turn
							if (move == curr) {
								// Get closest enemy unit in range of current
								// unit, if multiple
								// get enemy with fewest hit points, if tied -
								// get first
								// in reading order. It is an optional as enemy
								// may not be
								// present in unit's range.
								Optional<Unit> target = u.range()
									.stream()
									.filter(remainingUnits::containsKey)
									.map(remainingUnits::get)
									.filter(enemyUnits::contains)
									.min(Unit.compareUnits);

								// if present attack target enemy
								target.ifPresent(enemy -> {
									u.attack(enemy);
									if (enemy.hp() <= 0) {
										// Enemy died - add to set of dead
										// units. Remove
										// from remainingUnits
										deadUnits.add(enemy);
										remainingUnits.remove(enemy.loc());
									}
								});
							}
							// Break out of BFS
							break;
						} else {
							// Continue to search neighbors (coordinates in
							// range of curr) - check if they are open caverns
							// (not occupied by remaining units), check if they
							// haven't been visited before, and add them to
							// queue in reading order. Update location-parent
							// map.
							curr.range()
								.stream()
								.filter(BATTLEFIELD::containsKey)
								.filter(c -> !remainingUnits.containsKey(c))
								.filter(c -> !visited.contains(c))
								.sorted(Coordinates.compareLocations)
								.forEach(c -> {
									locationParentMap.put(c, curr);
									queue.offer(c);
								});
						}
					}
				}
			}
			if (combat)
				rounds++;
			else
				break;
			System.out.println("Round " + rounds + " Units " + remainingUnits);
		}
		System.out.print(rounds * sumOfHPsOfUnitsRemaining);
	}

	private Map<Coordinates, Unit> copyOfTreeMapOfAllInitialUnits() {
		Map<Coordinates, Unit> remainingUnits = new TreeMap<>(Coordinates.compareLocations);
		ALL_INITIAL_UNITS.entrySet()
			.stream()
			.forEach(e -> remainingUnits.put(e.getKey(), e.getValue()
				.copyOf()));
		return remainingUnits;
	}

	private void initializeBattlefieldAndUnits(Stream<String> lines) {
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

	static class OpenCavern {
		Coordinates c;

		OpenCavern(Coordinates c) {
			this.c = c;
		}
	}

	static class Coordinates {

		public static final Comparator<Coordinates> compareLocations = Comparator.comparing(Coordinates::getY)
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

	static interface Unit {

		final int AP = 3;

		Comparator<Unit> compareUnits = Comparator.comparing(Unit::hp)
			.thenComparing(Unit::loc, Coordinates.compareLocations);

		Type type();

		void moveTo(Coordinates move);

		Coordinates loc();

		int hp();

		void defend(Unit enemy);

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
			hp = hp - enemy.AP;
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

	}

	static class Elf implements Unit {
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
			hp = hp - enemy.AP;
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
