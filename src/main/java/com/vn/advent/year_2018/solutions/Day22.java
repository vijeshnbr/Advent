package com.vn.advent.year_2018.solutions;

import com.vn.advent.Solution;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Stream;

public class Day22 implements Solution {

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day22();
		System.out.println(solution.run());
	}

	@Override
	public String partOne(Stream<String> lines) {
		for (int x = Cave.MOUTH.loc.x; x <= Cave.TARGET.loc.x; x++) {
			for (int y = Cave.MOUTH.loc.y; y <= Cave.TARGET.loc.y; y++) {
				Coordinates c = new Coordinates(x, y);
				Cave.map.put(c, new Region(c));
			}
		}

		return String.valueOf(Cave.map.values()
			.stream()
			.mapToInt(Region::getRiskLevel)
			.sum());
	}

	@Override
	public String partTwo(Stream<String> lines) {

		Queue<Node> queue = new PriorityQueue<>(
				Comparator.comparing(Node::getMinutes));

		Set<Node> visited = new HashSet<>();

		queue.offer(new Node(Cave.MOUTH, Tool.TORCH, 0));

		Node target = new Node(Cave.TARGET, Tool.TORCH, 0);

		Map<Node, Integer> mapOfNodeAndMinutes = new HashMap<>();

		while (!queue.isEmpty()) {
			Node curr = queue.poll();
			if (visited.contains(curr))
				continue;
			visited.add(curr);
			mapOfNodeAndMinutes.compute(curr, (k, v) -> {
				Integer minutes = v;
				if (minutes == null || curr.minutes < minutes) {
					minutes = curr.minutes;
				}
				return minutes;
			});
			// Terminate Dijkastra when target node is processed
			if (curr.equals(target))
				break;
			// Form nodes with other applicable tools for the current node
			curr.region.getType().setOfValidToolsForRegionType.stream()
				.filter(tool -> tool != curr.tool)
				.map(tool -> new Node(curr.region, tool, curr.minutes + 7))
				.forEach(queue::offer);
			// Get traversable neighbor regions and filter regions where current
			// tool is valid, create nodes with minutes +1
			curr.region.getNeighbors()
				.stream()
				.filter(Region::isTraversable)
				.filter(region -> region.getType().setOfValidToolsForRegionType
					.contains(curr.tool))
				.map(region -> new Node(region, curr.tool, curr.minutes + 1))
				.filter(node -> !visited.contains(node))
				.forEach(queue::offer);
		}
		return String.valueOf(mapOfNodeAndMinutes.get(target));
	}

	static class Cave {
		public static final Map<Coordinates, Region> map = new TreeMap<>(
				Comparator.comparing(Coordinates::getY)
					.thenComparing(Coordinates::getX));
		public static final int DEPTH = 4848;

		public static final Region MOUTH = new Region(new Coordinates(0, 0));
		public static final Region TARGET = new Region(
				new Coordinates(15, 700));

		static {
			map.put(MOUTH.loc, MOUTH);
			map.put(TARGET.loc, TARGET);
		}
	}

	static class Node {
		final Region region;
		final Tool tool;
		int minutes;

		Node(Region region, Tool tool, int minutes) {
			this.region = region;
			this.tool = tool;
			this.minutes = minutes;
		}

		@Override
		public int hashCode() {
			return Objects.hash(region, tool);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (region == null) {
				if (other.region != null)
					return false;
			} else if (!region.equals(other.region))
				return false;
			if (tool != other.tool)
				return false;
			return true;
		}

		int getMinutes() {
			return minutes;
		}
	}

	static class Region {

		private static final Map<Region, Set<Region>> neighbors = new HashMap<>();

		final Coordinates loc;

		Type type;
		Integer geologicIndex, erosionLevel, riskLevel;

		private static final Type[] types = new Type[]{Type.ROCKY, Type.WET,
				Type.NARROW};

		Region(Coordinates loc) {
			this.loc = loc;
		}

		Type getType() {
			if (type == null) {
				int t = getErosionLevel() % 3;
				type = types[t];
			}
			return type;
		}

		int getRiskLevel() {
			if (riskLevel == null) {
				Type t = getType();
				if (t == Type.ROCKY)
					riskLevel = 0;
				else if (t == Type.WET)
					riskLevel = 1;
				else if (t == Type.NARROW)
					riskLevel = 2;
			}
			return riskLevel;
		}

		int getErosionLevel() {
			if (erosionLevel == null) {
				erosionLevel = (getGeologicIndex() + Cave.DEPTH) % 20183;
			}
			return erosionLevel;
		}

		int getGeologicIndex() {
			if (geologicIndex == null) {
				if (this.equals(Cave.MOUTH))
					geologicIndex = 0;
				else if (this.equals(Cave.TARGET))
					geologicIndex = 0;
				else if (this.loc.y == 0) {
					geologicIndex = this.loc.x * 16807;
				} else if (this.loc.x == 0) {
					geologicIndex = this.loc.y * 48271;
				} else {
					Region left = Cave.map.computeIfAbsent(this.loc.left(),
							Region::new);
					Region up = Cave.map.computeIfAbsent(this.loc.up(),
							Region::new);
					geologicIndex = left.getErosionLevel()
							* up.getErosionLevel();
				}
			}
			return geologicIndex;
		}

		@Override
		public int hashCode() {
			return Objects.hash(loc);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Region other = (Region) obj;
			if (loc == null) {
				if (other.loc != null)
					return false;
			} else if (!loc.equals(other.loc))
				return false;
			return true;
		}

		Region left() {
			return Cave.map.computeIfAbsent(this.loc.left(), Region::new);
		}

		Region up() {
			return Cave.map.computeIfAbsent(this.loc.up(), Region::new);
		}

		Region right() {
			return Cave.map.computeIfAbsent(this.loc.right(), Region::new);
		}

		Region down() {
			return Cave.map.computeIfAbsent(this.loc.down(), Region::new);
		}

		Set<Region> getNeighbors() {
			return neighbors.computeIfAbsent(this, (val) -> {
				Set<Region> neighbors = new HashSet<>();
				neighbors.add(val.left());
				neighbors.add(val.right());
				neighbors.add(val.up());
				neighbors.add(val.down());
				return neighbors;
			});
		}

		boolean isTraversable() {
			return this.loc.x >= 0 && this.loc.y >= 0;
		}
	}

	static class Coordinates {
		final int x, y;

		Coordinates(int x, int y) {
			this.x = x;
			this.y = y;
		}

		Coordinates left() {
			return new Coordinates(x - 1, y);
		}

		Coordinates up() {
			return new Coordinates(x, y - 1);
		}

		Coordinates right() {
			return new Coordinates(x + 1, y);
		}

		Coordinates down() {
			return new Coordinates(x, y + 1);
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

	static enum Type {
		ROCKY(Tool.TORCH, Tool.GEAR), WET(Tool.GEAR,
				Tool.NONE), NARROW(Tool.NONE, Tool.TORCH);

		private Set<Tool> setOfValidToolsForRegionType;

		Type(Tool one, Tool two) {
			setOfValidToolsForRegionType = new HashSet<>();
			setOfValidToolsForRegionType.add(one);
			setOfValidToolsForRegionType.add(two);
		}
	}

	static enum Tool {
		TORCH, GEAR, NONE;
	}

	@Override
	public String getInputFileName() {
		return "2018/input_22";
	}

}
