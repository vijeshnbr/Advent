package com.vn.advent.solution;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24 implements Solution {

	private static final Pattern GROUP = Pattern.compile(
			"(\\d+) units each with (\\d+) hit points\\s?(\\((.+)\\))?\\s?with an attack that does (\\d+) (.+) damage at initiative (\\d+)");

	private static final Pattern REACTION = Pattern.compile("(?:(\\w+) to (\\w+(?:, \\w+)*)){1,2}");

	private static final Pattern ATTACKTYPE = Pattern.compile("(\\w+)");

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day24();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {
		String input = lines.collect(Collectors.joining("SEPARATOR"));
		String[] armies = input.split("SEPARATORSEPARATOR");
		List<Group> immuneSystems = Stream.of(armies[0].split("SEPARATOR"))
			.map(l -> makeGroup(l, Army.IMMUNESYSTEM))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
		List<Group> infections = Stream.of(armies[1].split("SEPARATOR"))
			.map(l -> makeGroup(l, Army.INFECTON))
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	@Override
	public void partTwo(Stream<String> lines) {
		// TODO Auto-generated method stub

	}

	static interface Group {

		Comparator<Group> compareEffectivePowerReversed = Comparator.comparing(Group::effectivePower)
			.reversed();
		Comparator<Group> compareInitiativeReversed = Comparator.comparing(Group::initiative)
			.reversed();

		Comparator<Group> compareGroups = compareEffectivePowerReversed.thenComparing(compareInitiativeReversed);

		Army type();

		int units();

		Id id();

		void defend(Group enemy);

		default void attack(Group enemy) {
			enemy.defend(this);
		}

		default int effectivePower() {
			return units() * id().ad;
		}

		default int initiative() {
			return id().initiative;
		}

		static class Id {
			final int hp, ad, initiative;
			final AttackType at;
			final Map<AttackType, ReactionToAttackType> reaction = new EnumMap<>(AttackType.class);
			final Army type;

			Id(int hp, int ad, int initiative, AttackType at, Map<AttackType, ReactionToAttackType> reaction,
					Army type) {
				this.hp = hp;
				this.ad = ad;
				this.initiative = initiative;
				this.at = at;
				this.reaction.putAll(reaction);
				this.type = type;
			}

			@Override
			public String toString() {
				return "Id [type=" + type + ", hp=" + hp + ", ad=" + ad + ", initiative=" + initiative + ", at=" + at
						+ "]";
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
		final int units;
		final Id id;

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
			// TODO Auto-generated method stub

		}

	}

	static class Infection implements Group {
		final int units;
		final Id id;

		Infection(int units, Id id) {
			this.units = units;
			this.id = id;
		}

		@Override
		public Army type() {
			return Army.INFECTON;
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
			// TODO Auto-generated method stub

		}

	}

	static enum AttackType {
		FIRE, COLD, RADIATION, SLASHING, BLUDGEONING
	}

	static enum ReactionToAttackType {
		IMMUNE, WEAK, DEFAULT
	}

	static enum Army {
		IMMUNESYSTEM, INFECTON
	}

	@Override
	public String getInputFileName() {
		return "input_24";
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
				Map<AttackType, ReactionToAttackType> reactionMap = new EnumMap<>(AttackType.class);
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
							reactionMap.put(AttackType.valueOf(at), ReactionToAttackType.valueOf(reaction));
						}
					}
				}
				int ad = Integer.parseInt(g5);
				AttackType at = AttackType.valueOf(g6.toUpperCase());
				int initiative = Integer.parseInt(g7);

				Group.Id id = new Group.Id(hp, ad, initiative, at, reactionMap, type);
				if (type == Army.IMMUNESYSTEM)
					group = new ImmuneSystem(units, id);
				else if (type == Army.INFECTON)
					group = new Infection(units, id);
			}
		}
		return group;
	}

}
