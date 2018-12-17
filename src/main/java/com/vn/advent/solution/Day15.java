package com.vn.advent.solution;

import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Stream;

public class Day15 implements Solution {

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day15();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {

	}

	static interface Arena {

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

	@Override
	public void partTwo(Stream<String> lines) {

	}

	@Override
	public String getInputFileName() {
		return "input_15";
	}

}
