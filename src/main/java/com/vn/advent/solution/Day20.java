package com.vn.advent.solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day20 implements Solution {

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day20();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {
		String regex = lines.findFirst()
			.get();
		Room origin = new Room(new Coordinates(0, 0));
		Building.map.put(new Coordinates(0, 0), origin);

		Node<Function<Room, Room>> start = new Node<>(Function.identity());
		formMappingPipeline(start, regex);
		formMap(start, origin);

		// System.out.println();
		// Building.map.entrySet()
		// .stream()
		// .forEach(System.out::println);

		shortestPathsFrom(origin).values()
			.stream()
			.max(Integer::compare)
			.ifPresent(System.out::print);

	}

	@Override
	public void partTwo(Stream<String> lines) {
		String regex = lines.findFirst()
			.get();
		Room origin = new Room(new Coordinates(0, 0));
		Building.map.put(new Coordinates(0, 0), origin);

		Node<Function<Room, Room>> start = new Node<>(Function.identity());
		formMappingPipeline(start, regex);
		formMap(start, origin);

		System.out.print(shortestPathsFrom(origin).values()
			.stream()
			.filter(doors -> doors >= 1000)
			.count());
	}

	private Map<Room, Integer> shortestPathsFrom(Room origin) {
		Queue<Room> queue = new LinkedList<>();
		queue.offer(origin);
		Map<Room, Integer> roomAndDistance = new HashMap<>();
		roomAndDistance.put(origin, 0);
		while (!queue.isEmpty()) {
			Room curr = queue.poll();
			for (Room neighbor : curr.neighbors()) {
				if (!roomAndDistance.containsKey(neighbor)) {
					roomAndDistance.put(neighbor, roomAndDistance.get(curr) + 1);
					queue.offer(neighbor);
				}
			}
		}
		return roomAndDistance;
	}

	private void formMap(Node<Function<Room, Room>> root, Room origin) {
		Room after = root.data.apply(origin);
		for (Node<Function<Room, Room>> child : root.children) {
			formMap(child, after);
		}
	}

	void formMappingPipeline(Node<Function<Room, Room>> start, String str) {
		List<Node<Function<Room, Room>>> children = new ArrayList<>();
		start.children = children;
		Function<Room, Room> next = Function.identity();
		Branches branches = null;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (c) {
			case 'E':
				next = next.andThen(Building::east);
				break;
			case 'W':
				next = next.andThen(Building::west);
				break;
			case 'N':
				next = next.andThen(Building::north);
				break;
			case 'S':
				next = next.andThen(Building::south);
				break;
			case '(':
				branches = findChildren(i + 1, str);
				break;
			}
			if (branches != null)
				break;
		}
		start.data = next;
		if (branches != null) {
			for (String branch : branches.branches) {
				Node<Function<Room, Room>> branchStart = new Node<>(Function.identity());
				children.add(branchStart);
				formMappingPipeline(branchStart, branch + str.substring(branches.endIndex, str.length()));
			}
		}
	}

	static class Branches {
		List<String> branches;
		int endIndex;

		Branches(List<String> branches, int endIndex) {
			this.branches = branches;
			this.endIndex = endIndex;
		}
	}

	private Branches findChildren(int fromIndex, String str) {
		List<String> children = new ArrayList<>();
		int currIndex = fromIndex;
		boolean allChildrenFound = false;
		Branches branch = null;
		while (true) {
			LinkedList<Character> stackOfParenthesis = new LinkedList<>();
			for (int i = currIndex; i < str.length(); i++) {
				char curr = str.charAt(i);
				if (curr == '(') {
					stackOfParenthesis.push(curr);
				} else if (curr == ')') {
					if (!stackOfParenthesis.isEmpty())
						stackOfParenthesis.pop();
					else {
						allChildrenFound = true;
						String child = str.substring(currIndex, i);
						children.add(child);
						currIndex = i + 1;
						break;
					}
				} else if (curr == '|' && stackOfParenthesis.isEmpty()) {
					String child = str.substring(currIndex, i);
					children.add(child);
					currIndex = i + 1;
					break;
				}
			}
			if (allChildrenFound) {
				branch = new Branches(children, currIndex);
				break;
			} else if (str.charAt(currIndex) == ')') {
				// Right after | there is a close parenthesis means all children
				// were optional routes so will concat them
				String join = children.stream()
					.collect(Collectors.joining());
				children.clear();
				children.add(join);
				branch = new Branches(children, currIndex + 1);
				break;
			}
		}

		return branch;
	}

	public static class Node<T> {
		private T data;
		private List<Node<T>> children;

		Node(T data) {
			this.data = data;
		}
	}

	@Override
	public String getInputFileName() {
		return "input_20";
	}

	static class Building {
		public static Map<Coordinates, Room> map = new TreeMap<>(Comparator.comparing(Coordinates::getY)
			.thenComparing(Coordinates::getX));

		public static Room east(Room r) {
			Room east = map.computeIfAbsent(r.loc.east(), Room::new);
			r.east = east;
			east.west = r;
			return east;
		}

		public static Room west(Room r) {
			Room west = map.computeIfAbsent(r.loc.west(), Room::new);
			r.west = west;
			west.east = r;
			return west;
		}

		public static Room north(Room r) {
			Room north = map.computeIfAbsent(r.loc.north(), Room::new);
			r.north = north;
			north.south = r;
			return north;
		}

		public static Room south(Room r) {
			Room south = map.computeIfAbsent(r.loc.south(), Room::new);
			r.south = south;
			south.north = r;
			return south;
		}
	}

	static class Room {

		final Coordinates loc;

		Room east, west, north, south;

		Room(Coordinates c) {
			this.loc = c;
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
			Room other = (Room) obj;
			if (loc == null) {
				if (other.loc != null)
					return false;
			} else if (!loc.equals(other.loc))
				return false;
			return true;
		}

		List<Room> neighbors() {
			List<Room> neighbors = new ArrayList<>();
			if (east != null)
				neighbors.add(east);
			if (west != null)
				neighbors.add(west);
			if (north != null)
				neighbors.add(north);
			if (south != null)
				neighbors.add(south);
			return neighbors;
		}

		@Override
		public String toString() {
			return "Room [loc=" + loc + ", east=" + (east != null ? "Y" : "N") + ", west=" + (west != null ? "Y" : "N")
					+ ", north=" + (north != null ? "Y" : "N") + ", south=" + (south != null ? "Y" : "N") + "]";
		}
	}

	static class Coordinates {
		final int x, y;

		Coordinates(int x, int y) {
			this.x = x;
			this.y = y;
		}

		Coordinates east() {
			return new Coordinates(x + 1, y);
		}

		Coordinates west() {
			return new Coordinates(x - 1, y);
		}

		Coordinates north() {
			return new Coordinates(x, y - 1);
		}

		Coordinates south() {
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

}