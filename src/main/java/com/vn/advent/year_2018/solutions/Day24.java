package com.vn.advent.year_2018.solutions;

import com.vn.advent.Solution;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24 implements Solution {

	private static final Pattern GROUP = Pattern.compile(
			"(\\d+) units each with (\\d+) hit points\\s?(\\((.+)\\))?\\s?with an attack that does (\\d+) (.+) damage at initiative (\\d+)");

	private static final Pattern REACTION = Pattern
		.compile("(?:(\\w+) to (\\w+(?:, \\w+)*)){1,2}");

	private static final Pattern ATTACKTYPE = Pattern.compile("(\\w+)");

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day24();
		System.out.println(solution.run());
	}

	@Override
	public String partOne(Stream<String> lines) {
		String input = lines.collect(Collectors.joining("SEPARATOR"));
		String[] armies = input.split("SEPARATORSEPARATOR");
		List<Group> inputImmuneSystems = Stream.of(armies[0].split("SEPARATOR"))
			.map(l -> makeGroup(l, Army.IMMUNESYSTEM))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
		List<Group> inputInfections = Stream.of(armies[1].split("SEPARATOR"))
			.map(l -> makeGroup(l, Army.INFECTION))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
		Outcome outcome = battle(inputImmuneSystems, inputInfections);
		LOGGER.log(Level.INFO, "Outcome: {0}", outcome);
		return String.valueOf(outcome.unitsInWinningArmy);
	}

	@Override
	public String partTwo(Stream<String> lines) {
		String input = lines.collect(Collectors.joining("SEPARATOR"));
		String[] armies = input.split("SEPARATORSEPARATOR");
		List<Group> inputImmuneSystems = Stream.of(armies[0].split("SEPARATOR"))
			.map(l -> makeGroup(l, Army.IMMUNESYSTEM))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
		List<Group> inputInfections = Stream.of(armies[1].split("SEPARATOR"))
			.map(l -> makeGroup(l, Army.INFECTION))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
		boolean experiment = true;
		int minBoost = 0;
		int maxBoost = 2000;
		Outcome minOutcome = null;
		Outcome maxOutcome = null;
		while (experiment) {
			ImmuneSystem.boost = minBoost;
			minOutcome = battle(inputImmuneSystems, inputInfections);
			LOGGER.info("Min Boost: " + minBoost + ", outcome: " + minOutcome);
			ImmuneSystem.boost = maxBoost;
			maxOutcome = battle(inputImmuneSystems, inputInfections);
			LOGGER.info("Max Boost: " + maxBoost + ", outcome: " + maxOutcome);

			if (minOutcome.winner == Army.IMMUNESYSTEM) {
				break;
			}

			if (maxOutcome.winner == Army.IMMUNESYSTEM) {
				maxBoost = (maxBoost + minBoost) / 2;
				minBoost++;
			} else if (maxOutcome.winner == Army.INFECTION) {
				minBoost = maxBoost;
				maxBoost *= 2;
			}
		}
		LOGGER.log(Level.INFO, "Outcome: {0}", minOutcome);
		return minOutcome != null
				? String.valueOf(minOutcome.unitsInWinningArmy)
				: "";
	}

	private Outcome battle(List<Group> immuneSystemsInput,
			List<Group> infectionsInput) {
		// Make copy of input so we have original state of armies for records.
		List<Group> immuneSystems = immuneSystemsInput.stream()
			.map(Group::copyOf)
			.collect(Collectors.toList());
		List<Group> infections = infectionsInput.stream()
			.map(Group::copyOf)
			.collect(Collectors.toList());

		Outcome outcome = null;

		while (true) {
			// Target selection phase - process will populate below treemap
			// with
			// keys(attackers) ordered by decreasing initiatives
			Map<Group, Group> attackerDefender = new TreeMap<>(
					Group.compareInitiative.reversed());
			attackerDefender.putAll(selectTargetsForAttackingArmyFromDefenders(
					immuneSystems, infections));
			attackerDefender.putAll(selectTargetsForAttackingArmyFromDefenders(
					infections, immuneSystems));

			// Attack phase
			attackerDefender.entrySet()
				.stream()
				.forEach(e -> e.getKey()
					.attack(e.getValue()));

			int sumOfUnitsInImmuneSystems = immuneSystems.stream()
				.mapToInt(Group::units)
				.sum();
			int sumOfUnitsInInfections = infections.stream()
				.mapToInt(Group::units)
				.sum();

			// Extra condition (if you want to programmatically binary search
			// for Part 2) - If all attacking groups have effective power less
			// than their chosen target's hp, then declare draw as battle will
			// go on infinitely according to rules

			if (attackerDefender.entrySet()
				.stream()
				.allMatch(e -> e.getKey()
					.effectivePower() < e.getValue()
						.id().hp)) {
				// Break from battle with draw
				outcome = new Outcome(sumOfUnitsInInfections, Army.NONE);
				break;
			}

			if (sumOfUnitsInImmuneSystems <= 0) {
				outcome = new Outcome(sumOfUnitsInInfections, Army.INFECTION);
				break;
			} else if (sumOfUnitsInInfections <= 0) {
				outcome = new Outcome(sumOfUnitsInImmuneSystems,
						Army.IMMUNESYSTEM);
				break;
			}
		}
		return outcome;
	}

	static class Outcome {
		final int unitsInWinningArmy;
		final Army winner;

		Outcome(int unitsInWinningArmy, Army winner) {
			this.unitsInWinningArmy = unitsInWinningArmy;
			this.winner = winner;
		}

		@Override
		public String toString() {
			return "winner: " + winner + ", units remaining: "
					+ unitsInWinningArmy;
		}

	}

	private Map<Group, Group> selectTargetsForAttackingArmyFromDefenders(
			List<Group> attackers, List<Group> defenders) {

		Map<Group, Group> attackerDefender = new HashMap<>();

		// Sort army doing target selection by decreasing order of effective
		// power and
		// decreasing order of initiatives
		attackers.sort(Group.compareEffectivePower.reversed()
			.thenComparing(Group.compareInitiative.reversed()));
		Set<Group> setOfTargetedTargets = new HashSet<>();

		attackers.stream()
			.filter(Group::containsUnits)
			.forEach(attacker -> {
				Function<Group, Integer> responseFactor = g -> g
					.responseTo(attacker.id().at);
				Comparator<Group> compareGroupsByDamageResponseToGivenAttack = Comparator
					.comparing(responseFactor);
				Optional<Group> groupChosenForAttack = defenders.stream()
					.filter(Group::containsUnits)
					.filter(g -> !setOfTargetedTargets.contains(g))
					.filter(g -> g.responseTo(attacker.id().at) != 0)
					.max(compareGroupsByDamageResponseToGivenAttack
						.thenComparing(Group.compareEffectivePower)
						.thenComparing(Group.compareInitiative));
				groupChosenForAttack.ifPresent(g -> {
					setOfTargetedTargets.add(g);
					attackerDefender.put(attacker, g);
				});
			});

		return attackerDefender;
	}

	static interface Group {

		Comparator<Group> compareEffectivePower = Comparator
			.comparing(Group::effectivePower);
		Comparator<Group> compareInitiative = Comparator
			.comparing(Group::initiative);

		Army type();

		int units();

		Id id();

		void defend(Group enemy);

		default void attack(Group enemy) {
			LOGGER.info(this + "ATTACKING " + enemy);
			enemy.defend(this);
		}

		default int effectivePower() {
			return units() * ad();
		}

		default int initiative() {
			return id().initiative;
		}

		default int responseTo(AttackType at) {
			return id().reaction.getOrDefault(at, Response.DEFAULT)
				.factor();
		}

		default boolean containsUnits() {
			return units() > 0;
		}

		default int ad() {
			return id().ad;
		}

		static Group copyOf(Group toBeCopied) {
			Group copy = null;
			if (toBeCopied.type() == Army.IMMUNESYSTEM) {
				copy = new ImmuneSystem(toBeCopied.units(), toBeCopied.id());
			} else if (toBeCopied.type() == Army.INFECTION) {
				copy = new Infection(toBeCopied.units(), toBeCopied.id());
			}
			return copy;
		}

		static class Id {
			final int hp, ad, initiative;
			final AttackType at;
			final Map<AttackType, Response> reaction = new EnumMap<>(
					AttackType.class);
			final Army type;

			Id(int hp, int ad, int initiative, AttackType at,
					Map<AttackType, Response> reaction, Army type) {
				this.hp = hp;
				this.ad = ad;
				this.initiative = initiative;
				this.at = at;
				this.reaction.putAll(reaction);
				this.type = type;
			}

			@Override
			public String toString() {
				return "Id [type=" + type + ", hp=" + hp + ", ad=" + ad
						+ ", initiative=" + initiative + ", at=" + at + "]";
			}

			@Override
			public int hashCode() {
				return Objects.hash(hp, ad, initiative, at, reaction, type);
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				Id other = (Id) obj;
				if (ad != other.ad)
					return false;
				if (at != other.at)
					return false;
				if (hp != other.hp)
					return false;
				if (initiative != other.initiative)
					return false;
				if (reaction == null) {
					if (other.reaction != null)
						return false;
				} else if (!reaction.equals(other.reaction))
					return false;
				if (type != other.type)
					return false;
				return true;
			}
		}
	}

	static class ImmuneSystem implements Group {
		int units;
		final Id id;

		public static int boost = 0;

		ImmuneSystem(int units, Id id) {
			this.units = units;
			this.id = id;
		}

		@Override
		public Army type() {
			return Army.IMMUNESYSTEM;
		}

		@Override
		public String toString() {
			return "ImmuneSystem [units=" + units + ", id=" + id + "]";
		}

		@Override
		public int units() {
			return units;
		}

		@Override
		public Id id() {
			return id;
		}

		@Override
		public void defend(Group enemy) {
			int factor = responseTo(enemy.id().at);
			units = units - ((enemy.effectivePower() * factor) / id.hp);
			units = (units < 0) ? 0 : units;
		}

		@Override
		public int ad() {
			return id().ad + boost;
		}

	}

	static class Infection implements Group {
		int units;
		final Id id;

		Infection(int units, Id id) {
			this.units = units;
			this.id = id;
		}

		@Override
		public Army type() {
			return Army.INFECTION;
		}

		@Override
		public String toString() {
			return "Infection [units=" + units + ", id=" + id + "]";
		}

		@Override
		public int units() {
			return units;
		}

		@Override
		public Id id() {
			return id;
		}

		@Override
		public void defend(Group enemy) {
			int factor = responseTo(enemy.id().at);
			units = units - ((enemy.effectivePower() * factor) / id.hp);
			units = (units < 0) ? 0 : units;
		}

	}

	static enum AttackType {
		FIRE, COLD, RADIATION, SLASHING, BLUDGEONING
	}

	static enum Response {
		IMMUNE(0), WEAK(2), DEFAULT(1);
		private int factor;

		Response(int factor) {
			this.factor = factor;
		}

		int factor() {
			return this.factor;
		}
	}

	static enum Army {
		IMMUNESYSTEM, INFECTION, NONE
	}

	@Override
	public String getInputFileName() {
		return "2018/input_24";
	}

	private Group makeGroup(String line, Army type) {
		Group group = null;
		Matcher m = GROUP.matcher(line);
		if (m.find()) {
			int count = m.groupCount();
			if (count == 7) {
				String g1 = m.group(1);
				String g2 = m.group(2);
				String g3 = m.group(3);
				String g4 = m.group(4);
				String g5 = m.group(5);
				String g6 = m.group(6);
				String g7 = m.group(7);

				int units = Integer.parseInt(g1);
				int hp = Integer.parseInt(g2);
				Map<AttackType, Response> reactionMap = new EnumMap<>(
						AttackType.class);
				if (g3 != null) {
					m = REACTION.matcher(g4);
					while (m.find()) {
						String reaction = m.group(1)
							.toUpperCase();
						String attackTypes = m.group(2);
						Matcher m2 = ATTACKTYPE.matcher(attackTypes);
						while (m2.find()) {
							String at = m2.group()
								.toUpperCase();
							reactionMap.put(AttackType.valueOf(at),
									Response.valueOf(reaction));
						}
					}
				}
				int ad = Integer.parseInt(g5);
				AttackType at = AttackType.valueOf(g6.toUpperCase());
				int initiative = Integer.parseInt(g7);

				Group.Id id = new Group.Id(hp, ad, initiative, at, reactionMap,
						type);
				if (type == Army.IMMUNESYSTEM)
					group = new ImmuneSystem(units, id);
				else if (type == Army.INFECTION)
					group = new Infection(units, id);
			}
		}
		return group;
	}

}
