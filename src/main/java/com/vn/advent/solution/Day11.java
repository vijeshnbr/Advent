package com.vn.advent.solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Stream;

import com.vn.advent.solution.Day11.Coordinates.Diagonal;

public class Day11 implements Solution {

	private static final int GRID_SERIAL_NO = 1133;

	private static final Map<Diagonal, Integer> CACHE_GRID_TOTALPOWERLEVEL = new HashMap<>();
	private static final Map<Coordinates, Integer> CACHE_POWERLEVEL = new HashMap<>();

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day11();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {
		List<Coordinates> listOfCoordinatesThatFormSquareGrids = new ArrayList<>();
		int size = 3;
		int xyMax = 300 - size + 1;
		for (int y = 1; y <= xyMax; y++) {
			for (int x = 1; x <= xyMax; x++) {
				Coordinates c = new Coordinates(x, y, size);
				listOfCoordinatesThatFormSquareGrids.add(c);
			}
		}

		listOfCoordinatesThatFormSquareGrids.stream()
			.map(Coordinates::calcPowerLevel)
			.max((c1, c2) -> Integer.compare(c1.totalPower, c2.totalPower))
			.ifPresent(System.out::print);
	}

	static class Holder {
		Coordinates c;
		Holder() {
			c = new Coordinates(1, 1);
		}
	}

	@Override
	public void partTwo(Stream<String> lines) {
		List<Coordinates> listOfCoordinatesThatFormSquareGrids = new ArrayList<>();
		for (int size = 1; size <= 300; size++) {
			int xyMax = 300 - size + 1;
			for (int y = 1; y <= xyMax; y++) {
				for (int x = 1; x <= xyMax; x++) {
					Coordinates c = new Coordinates(x, y, size);
					listOfCoordinatesThatFormSquareGrids.add(c);
				}
			}
		}

		Holder max = new Holder();
		listOfCoordinatesThatFormSquareGrids.stream()
			.map(Coordinates::calcPowerLevel)
			.filter(c -> c.totalPower > max.c.totalPower)
			.peek(System.out::println)
			.forEach(c -> max.c = c);
	}

	static class Coordinates {
		final int x, y, size, power;
		int totalPower;

		// A compute function that can be used to compute value to be cached if
		// key not in cache
		private static final Function<Coordinates, Integer> computePowerLevelOfOne = c -> {
			int rackId = c.getRackId();
			int power = c.y * rackId + GRID_SERIAL_NO;
			power *= rackId;
			power = (power / 100) % 10;
			power -= 5;
			return power;
		};

		Coordinates(int x, int y, int size) {
			this.x = x;
			this.y = y;
			this.size = size;
			this.power = getPowerLevelOfOne();
		}
		Coordinates(int x, int y) {
			this.x = x;
			this.y = y;
			this.size = 0;
			this.power = getPowerLevelOfOne();
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

		private int getRackId() {
			return x + 10;
		}

		private int getPowerLevelOfOne() {
			return CACHE_POWERLEVEL.computeIfAbsent(this,
					computePowerLevelOfOne);
		}

		Coordinates calcPowerLevel() {
			if (size == 1) {
				this.totalPower = power;
			} else {
				Coordinates diagonallyOpposite = new Coordinates(
						this.x + this.size - 1, this.y + this.size - 1);
				Diagonal diagonal = new Diagonal(this, diagonallyOpposite);
				this.totalPower = getSumGivenDiagonal(diagonal);
			}
			return this;
		}
		private Integer getSumGivenDiagonal(Diagonal diagonal) {
			return CACHE_GRID_TOTALPOWERLEVEL.computeIfAbsent(diagonal,
					key -> calculateSumGivenDiagonals(key));
		}

		@Override
		public String toString() {
			return "Coordinates [x=" + x + ", y=" + y + ", size=" + size
					+ ", power=" + power + ", totalPower=" + totalPower + "]";
		}
		private Integer calculateSumGivenDiagonals(Diagonal key) {
			// Recursively divide into four if even
			// If odd, thn total the extras and add to recursive call to same
			// func with diagonal size - 1
			int diffX = key.bottomRight.x - key.topLeft.x;
			Coordinates topRight = new Coordinates(key.bottomRight.x,
					key.topLeft.y);
			Coordinates bottomLeft = new Coordinates(key.topLeft.x,
					key.bottomRight.y);
			if (diffX == 1) {
				return key.topLeft.power + key.bottomRight.power
						+ topRight.power + bottomLeft.power;
			}
			if (diffX % 2 == 0) {
				// odd - cannot be divided
				int sumOfLeftOver = 0;
				for (int i = key.topLeft.x; i < key.bottomRight.x; i++) {
					sumOfLeftOver += new Coordinates(i,
							key.bottomRight.y).power;
				}
				for (int j = key.topLeft.y; j < key.bottomRight.y; j++) {
					sumOfLeftOver += new Coordinates(key.bottomRight.x,
							j).power;
				}
				sumOfLeftOver += key.bottomRight.power;
				Coordinates newBottomRight = new Coordinates(
						key.bottomRight.x - 1, key.bottomRight.y - 1);
				return sumOfLeftOver + getSumGivenDiagonal(
						new Diagonal(key.topLeft, newBottomRight));
			}
			// even - so create four diagonals and sum
			int diffBy2 = diffX / 2;
			int diffBy2Plus1 = diffBy2 + 1;
			Diagonal leftTop = new Diagonal(key.topLeft, new Coordinates(
					key.topLeft.x + diffBy2, key.topLeft.y + diffBy2));
			Diagonal leftBottom = new Diagonal(
					new Coordinates(key.topLeft.x,
							key.topLeft.y + diffBy2Plus1),
					new Coordinates(key.topLeft.x + diffBy2,
							key.bottomRight.y));
			Diagonal rightTop = new Diagonal(
					new Coordinates(key.topLeft.x + diffBy2Plus1,
							key.topLeft.y),
					new Coordinates(key.bottomRight.x,
							key.bottomRight.y - diffBy2Plus1));
			Diagonal rightBottom = new Diagonal(
					new Coordinates(key.topLeft.x + diffBy2Plus1,
							key.topLeft.y + diffBy2Plus1),
					key.bottomRight);
			return getSumGivenDiagonal(leftTop)
					+ getSumGivenDiagonal(leftBottom)
					+ getSumGivenDiagonal(rightTop)
					+ getSumGivenDiagonal(rightBottom);
		}

		static class Diagonal {
			final Coordinates topLeft;
			final Coordinates bottomRight;
			Diagonal(Coordinates topLeft, Coordinates bottomRight) {
				this.topLeft = topLeft;
				this.bottomRight = bottomRight;
			}
			@Override
			public int hashCode() {
				return Objects.hash(topLeft, bottomRight);
			}
			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				Diagonal other = (Diagonal) obj;
				if (bottomRight == null) {
					if (other.bottomRight != null)
						return false;
				} else if (!bottomRight.equals(other.bottomRight))
					return false;
				if (topLeft == null) {
					if (other.topLeft != null)
						return false;
				} else if (!topLeft.equals(other.topLeft))
					return false;
				return true;
			}
			@Override
			public String toString() {
				return "Diagonal [topLeft=" + topLeft + ", bottomRight="
						+ bottomRight + "]";
			}
		}

	}
	@Override
	public String getInputFileName() {
		return "input_11";
	}

}
