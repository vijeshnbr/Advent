package com.vn.advent.solution;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Stream;

public class Day9 implements Solution {

	private static final int TOTAL_PLAYERS = 400;
	private static final int LAST_MARBLE = 71864;
	private static final Map<Integer, Long> playerAndScore = new HashMap<>();

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day9();
		System.out.println(solution.run());
	}

	@Override
	public String partOne(Stream<String> lines) {
		int lastMarble = LAST_MARBLE;
		int totalPlayers = TOTAL_PLAYERS;
		// BEGIN GAME
		playerAndScore.clear();
		playGame(lastMarble, totalPlayers);

		// TODO: Refactor code to return the score - return copy of player and
		// score map

		// GET SCORE
		return String.valueOf(playerAndScore.entrySet()
			.stream()
			.filter(e -> Objects.nonNull(e.getValue()))
			.max(Map.Entry.comparingByValue())
			.map(Map.Entry::getValue)
			.get());
	}

	@Override
	public String partTwo(Stream<String> lines) {
		int lastMarble = LAST_MARBLE * 100;
		int totalPlayers = TOTAL_PLAYERS;
		// BEGIN GAME
		playerAndScore.clear();
		playGame(lastMarble, totalPlayers);

		// TODO: Refactor code to return the score - return copy of player and
		// score map

		// GET SCORE
		return String.valueOf(playerAndScore.entrySet()
			.stream()
			.filter(e -> Objects.nonNull(e.getValue()))
			.max(Map.Entry.comparingByValue())
			.map(Map.Entry::getValue)
			.get());
	}

	private void playGame(int lastMarble, int totalPlayers) {
		Circle<Marble> originalHead = new Circle<>(new Marble(0));
		Circle<Marble> currentHead = originalHead;
		for (int i = 1; i <= lastMarble; i++) {
			final int playerId = (i % totalPlayers == 0)
					? totalPlayers
					: i % totalPlayers;
			final Marble marbleToPlay = new Marble(i);
			if (marbleToPlay.isMultipleOf23()) {
				playerAndScore.compute(playerId,
						(k, v) -> (v == null)
								? marbleToPlay.value
								: v + marbleToPlay.value);
				Circle<Marble> seventhPrevious = currentHead.findPrevious(7);
				playerAndScore.compute(playerId,
						(k, v) -> (v == null)
								? seventhPrevious.current.value
								: v + seventhPrevious.current.value);
				currentHead = seventhPrevious.remove();
			} else {
				currentHead = currentHead.add(marbleToPlay);
			}
		}
	}

	/**
	 * A circular doubly linked list collection basic implementation with only
	 * methods I need
	 */
	static class Circle<T> {
		T current;
		Circle<T> previous;
		Circle<T> next;

		Circle(T element) {
			this.current = element;
			previous = this;
			next = this;
		}

		Circle<T> add(T element) {
			Circle<T> head;
			if (current == null) {
				current = element;
				previous = this;
				next = this;
				head = this;
			} else {
				Circle<T> add = new Circle<>(element);
				add.next = this.next.next;
				add.previous = this.next;
				add.next.previous = add;
				this.next.next = add;
				head = add;
			}
			return head;
		}

		Circle<T> findPrevious(int num) {
			Circle<T> head = this;
			for (int i = 0; i < num; i++) {
				head = head.previous;
			}
			return head;
		}

		Circle<T> remove() {
			Circle<T> previous = this.previous;
			Circle<T> next = this.next;
			previous.next = next;
			next.previous = previous;
			return next;
		}

		/**
		 * Use this method to debug - it will print the circle as in problem and
		 * also highlight the input currentHead
		 * 
		 * @param currentHead
		 * @return
		 */
		String toString(Circle<T> currentHead) {
			Circle<T> printHead = this;
			StringBuilder sb = new StringBuilder();
			if (printHead == currentHead)
				sb.append('(');
			sb.append(printHead.current);
			if (printHead == currentHead)
				sb.append(')');
			sb.append(" ");
			printHead = this.next;
			while (printHead != this) {
				if (printHead == currentHead)
					sb.append('(');
				sb.append(printHead.current);
				if (printHead == currentHead)
					sb.append(')');
				sb.append(" ");
				printHead = printHead.next;
			}
			return sb.toString();
		}

	}

	static class Marble {
		final int value;

		Marble(int value) {
			this.value = value;
		}

		boolean isMultipleOf23() {
			return value % 23 == 0;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}

	@Override
	public String getInputFileName() {
		return "input_9";
	}

}
