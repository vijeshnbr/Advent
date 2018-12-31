package com.vn.advent.solution;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day13 implements Solution {

	private static final Map<Coordinates, Track> map = new HashMap<>();
	private static final Map<Coordinates, Vehicle> vehicles = new TreeMap<>(
			Comparator.comparing(Coordinates::getY)
				.thenComparing(Coordinates::getX));

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day13();
		System.out.println(solution.run());
	}

	@Override
	public String partOne(Stream<String> lines) {
		List<String> input = lines.collect(Collectors.toList());
		initializeTrack(input);
		boolean collisionDetected = false;
		Coordinates collision = null;
		while (!collisionDetected) {
			List<Vehicle> copyOfvehicles = vehicles.values()
				.stream()
				.collect(Collectors.toList());
			// System.out.println(copyOfvehicles);
			for (Vehicle v : copyOfvehicles) {
				vehicles.remove(v.getLocation());
				Vehicle afterMove = v.move();
				Coordinates newLocOfVehicle = afterMove.getLocation();
				if (vehicles.keySet()
					.contains(newLocOfVehicle)) {
					// Collision detected
					LOGGER.info("COLLISION : " + newLocOfVehicle);
					collisionDetected = true;
					collision = newLocOfVehicle;
					break;
				} else {
					vehicles.put(newLocOfVehicle, afterMove);
				}
			}
		}
		return collision != null
				? collision.toString()
				: "No collision detected";
	}

	@Override
	public String partTwo(Stream<String> lines) {
		List<String> input = lines.collect(Collectors.toList());
		initializeTrack(input);
		Coordinates locationOfLastVehicle = null;
		while (true) {
			List<Vehicle> copyOfvehicles = vehicles.values()
				.stream()
				.collect(Collectors.toList());
			LOGGER.log(Level.INFO, "Vehicles: {0}", copyOfvehicles);
			Set<Coordinates> allCrashesInTick = new HashSet<>();
			for (Vehicle v : copyOfvehicles) {
				Coordinates locOfCurrentVehicle = v.getLocation();
				if (!allCrashesInTick.contains(locOfCurrentVehicle)) {
					vehicles.remove(locOfCurrentVehicle);
					Vehicle afterMove = v.move();
					Coordinates newLocOfVehicle = afterMove.getLocation();
					if (vehicles.keySet()
						.contains(newLocOfVehicle)) {
						// Collision detected
						allCrashesInTick.add(newLocOfVehicle);

					} else {
						vehicles.put(newLocOfVehicle, afterMove);
						if (vehicles.size() == 1) {
							locationOfLastVehicle = newLocOfVehicle;
						}
					}
				}
			}
			for (Coordinates c : allCrashesInTick) {
				vehicles.remove(c);
			}
			if (vehicles.size() == 1) {
				break;
			}
		}
		return vehicles.toString();
	}

	static class Vehicle {

		// A vehicle will have a location and direction it is facing or
		// intending to travel

		Coordinates location;
		Direction direction;

		int atIntersectionCounter;

		Vehicle(Coordinates location, Direction d) {
			this.location = location;
			this.direction = d;
		}

		@Override
		public String toString() {
			return "Vehicle [" + location + ", going " + direction + "]";
		}

		Vehicle move() {
			Track track = map.get(this.location);
			Type currentlyOn = track.getType();
			Track newLoc = track;
			if (currentlyOn == Type.HORIZONTAL) {
				if (this.direction == Direction.EAST) {
					newLoc = track.getEast();
				} else if (this.direction == Direction.WEST) {
					newLoc = track.getWest();
				}
			} else if (currentlyOn == Type.VERTICAL) {
				if (this.direction == Direction.SOUTH) {
					newLoc = track.getSouth();
				} else if (this.direction == Direction.NORTH) {
					newLoc = track.getNorth();
				}
			} else if (currentlyOn == Type.PLUS) {
				atIntersectionCounter++;
				Direction newDirection = this.direction;
				int choiceAtIntersection = atIntersectionCounter % 3;
				if (choiceAtIntersection == 1) {
					newDirection = this.direction.valueOf(Turn.LEFT);
				} else if (choiceAtIntersection == 0) {
					newDirection = this.direction.valueOf(Turn.RIGHT);
				}
				if (newDirection == Direction.EAST) {
					newLoc = track.getEast();
				} else if (newDirection == Direction.WEST) {
					newLoc = track.getWest();
				} else if (newDirection == Direction.SOUTH) {
					newLoc = track.getSouth();
				} else if (newDirection == Direction.NORTH) {
					newLoc = track.getNorth();
				}
				this.direction = newDirection;
			} else if (currentlyOn == Type.FRONT_SLASH) {
				if (this.direction == Direction.SOUTH) {
					newLoc = track.getWest();
					this.direction = Direction.WEST;
				} else if (this.direction == Direction.NORTH) {
					newLoc = track.getEast();
					this.direction = Direction.EAST;
				} else if (this.direction == Direction.EAST) {
					newLoc = track.getNorth();
					this.direction = Direction.NORTH;
				} else if (this.direction == Direction.WEST) {
					newLoc = track.getSouth();
					this.direction = Direction.SOUTH;
				}
			} else if (currentlyOn == Type.BACK_SLASH) {
				if (this.direction == Direction.SOUTH) {
					newLoc = track.getEast();
					this.direction = Direction.EAST;
				} else if (this.direction == Direction.NORTH) {
					newLoc = track.getWest();
					this.direction = Direction.WEST;
				} else if (this.direction == Direction.EAST) {
					newLoc = track.getSouth();
					this.direction = Direction.SOUTH;
				} else if (this.direction == Direction.WEST) {
					newLoc = track.getNorth();
					this.direction = Direction.NORTH;
				}
			}
			this.location = newLoc.location();
			return this;
		}

		public Coordinates getLocation() {
			return this.location;
		}

	}

	static interface Track {
		int length();

		void setEast(Track east);

		void setWest(Track west);

		void setNorth(Track north);

		void setSouth(Track south);

		Track getEast();

		Track getWest();

		Track getNorth();

		Track getSouth();

		Coordinates location();

		Type getType();
	}

	static class Road implements Track {
		int length;
		Track east, west, north, south;
		Coordinates location;
		final Type t;

		Road(Coordinates location, Type t) {
			this.location = location;
			this.t = t;
		}

		@Override
		public int length() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Coordinates location() {
			return location;
		}

		@Override
		public Type getType() {
			return this.t;
		}

		@Override
		public void setEast(Track east) {
			this.east = east;
		}

		@Override
		public void setWest(Track west) {
			this.west = west;

		}

		@Override
		public void setNorth(Track north) {
			this.north = north;
		}

		@Override
		public void setSouth(Track south) {
			this.south = south;
		}

		@Override
		public Track getEast() {
			return this.east;
		}

		@Override
		public Track getWest() {
			return this.west;
		}

		@Override
		public Track getNorth() {
			return this.north;
		}

		@Override
		public Track getSouth() {
			return this.south;
		}

		@Override
		public String toString() {
			return String.valueOf(t.getVal());
		}
	}

	static class Intersection implements Track {
		Track east, west, north, south;
		final Coordinates location;
		final Type t;

		Intersection(Coordinates location, Type t) {
			this.location = location;
			this.t = t;
		}

		@Override
		public int length() {
			return 1;
		}

		@Override
		public Coordinates location() {
			return location;
		}

		@Override
		public Type getType() {
			return this.t;
		}

		@Override
		public void setEast(Track east) {
			this.east = east;
		}

		@Override
		public void setWest(Track west) {
			this.west = west;

		}

		@Override
		public void setNorth(Track north) {
			this.north = north;
		}

		@Override
		public void setSouth(Track south) {
			this.south = south;
		}

		@Override
		public Track getEast() {
			return this.east;
		}

		@Override
		public Track getWest() {
			return this.west;
		}

		@Override
		public Track getNorth() {
			return this.north;
		}

		@Override
		public Track getSouth() {
			return this.south;
		}

		@Override
		public String toString() {
			return String.valueOf(t.getVal());
		}
	}

	static class Curve implements Track {

		Track east, west, north, south;
		final Coordinates location;
		final Type t;

		Curve(Coordinates location, Type t) {
			this.location = location;
			this.t = t;
		}

		@Override
		public int length() {
			return 1;
		}

		@Override
		public Coordinates location() {
			return this.location;
		}

		@Override
		public Type getType() {
			return this.t;
		}

		@Override
		public void setEast(Track east) {
			this.east = east;
		}

		@Override
		public void setWest(Track west) {
			this.west = west;

		}

		@Override
		public void setNorth(Track north) {
			this.north = north;
		}

		@Override
		public void setSouth(Track south) {
			this.south = south;
		}

		@Override
		public Track getEast() {
			return this.east;
		}

		@Override
		public Track getWest() {
			return this.west;
		}

		@Override
		public Track getNorth() {
			return this.north;
		}

		@Override
		public Track getSouth() {
			return this.south;
		}

		@Override
		public String toString() {
			return String.valueOf(t.getVal());
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

		public Coordinates top() {
			return new Coordinates(x, y - 1);
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

	static enum Type {
		VERTICAL('|'), HORIZONTAL('-'), FRONT_SLASH('/'), BACK_SLASH(
				'\\'), PLUS('+'), VEHICLE_WEST('<'), VEHICLE_EAST(
						'>'), VEHICLE_NORTH(
								'^'), VEHICLE_SOUTH('v'), UNPAVED(' ');
		private char c;

		private static final Map<Character, Type> ENUM_MAP;
		static {
			Map<Character, Type> map = new HashMap<>();
			for (Type instance : Type.values()) {
				map.put(instance.getVal(), instance);
			}
			ENUM_MAP = Collections.unmodifiableMap(map);
		}

		private Type(char c) {
			this.c = c;
		}

		public char getVal() {
			return this.c;
		}

		public static Type valueOf(char name) {
			return ENUM_MAP.get(name);
		}
	}

	static enum Turn {
		LEFT, STRAIGHT, RIGHT
	}

	static enum Direction {
		EAST, WEST, NORTH, SOUTH;
		public Direction valueOf(Turn turn) {
			Direction newDirection = this;
			if (this == EAST) {
				if (turn == Turn.LEFT) {
					newDirection = NORTH;
				} else if (turn == Turn.RIGHT) {
					newDirection = SOUTH;
				}
			} else if (this == WEST) {
				if (turn == Turn.LEFT) {
					newDirection = SOUTH;
				} else if (turn == Turn.RIGHT) {
					newDirection = NORTH;
				}
			} else if (this == NORTH) {
				if (turn == Turn.LEFT) {
					newDirection = WEST;
				} else if (turn == Turn.RIGHT) {
					newDirection = EAST;
				}
			} else if (this == SOUTH) {
				if (turn == Turn.LEFT) {
					newDirection = EAST;
				} else if (turn == Turn.RIGHT) {
					newDirection = WEST;
				}
			}
			return newDirection;
		}
	}

	private void initializeTrack(List<String> input) {
		map.clear();
		vehicles.clear();
		int currY = 0;
		for (String line : input) {
			int y = currY;
			IntStream.range(0, line.length())
				.forEach(i -> {
					int x = i;
					Coordinates c = new Coordinates(x, y);
					Type type = Type.valueOf(line.charAt(i));
					switch (type) {
						case FRONT_SLASH :
						case BACK_SLASH : {

							Track curve = new Curve(c, type);
							map.put(c, curve);
							Track west = map.get(c.left());
							if (west != null
									&& (west.getType() == Type.HORIZONTAL
											|| west.getType() == Type.PLUS)) {
								west.setEast(curve);
								curve.setWest(west);
							}
							Track north = map.get(c.top());
							if (north != null
									&& (north.getType() == Type.VERTICAL
											|| north.getType() == Type.PLUS)) {
								north.setSouth(curve);
								curve.setNorth(north);
							}
							break;
						}
						case HORIZONTAL : {
							Track road = new Road(c, type);
							map.put(c, road);
							Track west = map.get(c.left());
							if (west != null && (west
								.getType() == Type.HORIZONTAL
									|| west.getType() == Type.PLUS
									|| west.getType() == Type.FRONT_SLASH
									|| west.getType() == Type.BACK_SLASH)) {
								west.setEast(road);
								road.setWest(west);
							}
							Track north = map.get(c.top());
							if (north != null
									&& (north.getType() == Type.PLUS)) {
								north.setSouth(road);
								road.setNorth(north);
							}
							break;
						}
						case VERTICAL : {
							Track road = new Road(c, type);
							map.put(c, road);
							Track west = map.get(c.left());
							if (west != null && (west.getType() == Type.PLUS)) {
								west.setEast(road);
								road.setWest(west);
							}
							Track north = map.get(c.top());
							if (north != null && (north.getType() == Type.PLUS
									|| north.getType() == Type.VERTICAL
									|| north.getType() == Type.BACK_SLASH
									|| north.getType() == Type.FRONT_SLASH)) {
								north.setSouth(road);
								road.setNorth(north);
							}
							break;
						}
						case PLUS : {
							Track intersection = new Intersection(c, type);
							map.put(c, intersection);
							Track west = map.get(c.left());
							if (west != null && (west
								.getType() == Type.HORIZONTAL
									|| west.getType() == Type.PLUS
									|| west.getType() == Type.FRONT_SLASH
									|| west.getType() == Type.BACK_SLASH)) {
								west.setEast(intersection);
								intersection.setWest(west);
							}
							Track north = map.get(c.top());
							if (north != null && (north.getType() == Type.PLUS
									|| north.getType() == Type.VERTICAL
									|| north.getType() == Type.BACK_SLASH
									|| north.getType() == Type.FRONT_SLASH)) {
								north.setSouth(intersection);
								intersection.setNorth(north);
							}
							break;
						}
						case VEHICLE_EAST : {
							Track road = new Road(c, Type.HORIZONTAL);
							map.put(c, road);
							Track west = map.get(c.left());
							if (west != null && (west
								.getType() == Type.HORIZONTAL
									|| west.getType() == Type.PLUS
									|| west.getType() == Type.FRONT_SLASH
									|| west.getType() == Type.BACK_SLASH)) {
								west.setEast(road);
								road.setWest(west);
							}
							Track north = map.get(c.top());
							if (north != null
									&& (north.getType() == Type.PLUS)) {
								north.setSouth(road);
								road.setNorth(north);
							}

							Vehicle vehicle = new Vehicle(c, Direction.EAST);
							vehicles.put(c, vehicle);
							break;
						}
						case VEHICLE_WEST : {
							Track road = new Road(c, Type.HORIZONTAL);
							map.put(c, road);
							Track west = map.get(c.left());
							if (west != null && (west
								.getType() == Type.HORIZONTAL
									|| west.getType() == Type.PLUS
									|| west.getType() == Type.FRONT_SLASH
									|| west.getType() == Type.BACK_SLASH)) {
								west.setEast(road);
								road.setWest(west);
							}
							Track north = map.get(c.top());
							if (north != null
									&& (north.getType() == Type.PLUS)) {
								north.setSouth(road);
								road.setNorth(north);
							}

							Vehicle vehicle = new Vehicle(c, Direction.WEST);
							vehicles.put(c, vehicle);
							break;
						}

						case VEHICLE_NORTH : {
							Track road = new Road(c, Type.VERTICAL);
							map.put(c, road);
							Track west = map.get(c.left());
							if (west != null && (west.getType() == Type.PLUS)) {
								west.setEast(road);
								road.setWest(west);
							}
							Track north = map.get(c.top());
							if (north != null && (north.getType() == Type.PLUS
									|| north.getType() == Type.VERTICAL
									|| north.getType() == Type.BACK_SLASH
									|| north.getType() == Type.FRONT_SLASH)) {
								north.setSouth(road);
								road.setNorth(north);
							}

							Vehicle vehicle = new Vehicle(c, Direction.NORTH);
							vehicles.put(c, vehicle);
							break;
						}
						case VEHICLE_SOUTH : {
							Track road = new Road(c, Type.VERTICAL);
							map.put(c, road);
							Track west = map.get(c.left());
							if (west != null && (west.getType() == Type.PLUS)) {
								west.setEast(road);
								road.setWest(west);
							}
							Track north = map.get(c.top());
							if (north != null && (north.getType() == Type.PLUS
									|| north.getType() == Type.VERTICAL
									|| north.getType() == Type.BACK_SLASH
									|| north.getType() == Type.FRONT_SLASH)) {
								north.setSouth(road);
								road.setNorth(north);
							}
							Vehicle vehicle = new Vehicle(c, Direction.SOUTH);
							vehicles.put(c, vehicle);
							break;
						}
						default :
							break;

					}
				});
			currY++;
		}
	}

	@Override
	public String getInputFileName() {
		return "input_13";
	}

}
