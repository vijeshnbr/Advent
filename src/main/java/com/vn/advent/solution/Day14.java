package com.vn.advent.solution;

import java.util.logging.Level;
import java.util.stream.Stream;

public class Day14 implements Solution {

	private static final int INPUT = 170641;

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day14();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {

		Circle<Integer> recipeOneScore = new Circle<>(3);
		Circle<Integer> recipeTwoScore = recipeOneScore.add(7);
		// below 3 are running heads and need to be maintained
		Circle<Integer> last = recipeTwoScore;
		Circle<Integer> elfOne = recipeOneScore;
		Circle<Integer> elfTwo = recipeTwoScore;
		int recipeCounter = 2;

		int tenPlusInput = INPUT + 10;
		while (true) {
			// calculate new recipe score and add to end of scores
			Integer total = elfOne.current + elfTwo.current;
			if (total / 10 == 1) {
				last = last.add(1);
				recipeCounter++;
				if (recipeCounter > INPUT && recipeCounter <= tenPlusInput) {
					System.out.print(last.current);
				}
				if (recipeCounter == tenPlusInput)
					break;
			}
			last = last.add(total % 10);
			recipeCounter++;
			if (recipeCounter > INPUT && recipeCounter <= tenPlusInput) {
				System.out.print(last.current);
			}
			if (recipeCounter == tenPlusInput)
				break;

			// pick new recipes
			elfOne = elfOne.getElementAfterNSteps(1 + elfOne.current);
			elfTwo = elfTwo.getElementAfterNSteps(1 + elfTwo.current);
		}
	}

	@Override
	public void partTwo(Stream<String> lines) {
		Circle<Integer> recipeOneScore = new Circle<>(3);
		Circle<Integer> recipeTwoScore = recipeOneScore.add(7);
		String initialScore = "37";
		String input = String.valueOf(INPUT);
		int length = input.length();
		// below 3 are running heads and need to be maintained
		Circle<Integer> last = recipeTwoScore;
		Circle<Integer> elfOne = recipeOneScore;
		Circle<Integer> elfTwo = recipeTwoScore;
		int recipeCounter = 2;

		StringBuilder lastNoOfScoresAsDigitsInInput = new StringBuilder(initialScore);
		while (true) {
			// calculate new recipe score and add to end of scores
			Integer total = elfOne.current + elfTwo.current;
			if (total / 10 == 1) {
				last = last.add(1);
				recipeCounter++;
				lastNoOfScoresAsDigitsInInput.append(Character.forDigit(last.current, 10));
				if (lastNoOfScoresAsDigitsInInput.length() > length) {
					lastNoOfScoresAsDigitsInInput = new StringBuilder(lastNoOfScoresAsDigitsInInput.substring(1));
				}
				if (input.equals(lastNoOfScoresAsDigitsInInput.toString())) {
					System.out.print(recipeCounter - length);
					break;
				}
			}
			last = last.add(total % 10);
			recipeCounter++;
			lastNoOfScoresAsDigitsInInput.append(Character.forDigit(last.current, 10));
			if (lastNoOfScoresAsDigitsInInput.length() > length) {
				lastNoOfScoresAsDigitsInInput = new StringBuilder(lastNoOfScoresAsDigitsInInput.substring(1));
			}
			if (input.equals(lastNoOfScoresAsDigitsInInput.toString())) {
				System.out.print(recipeCounter - length);
				break;
			}
			// pick new recipes
			elfOne = elfOne.getElementAfterNSteps(1 + elfOne.current);
			elfTwo = elfTwo.getElementAfterNSteps(1 + elfTwo.current);
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
			Circle<T> last;
			if (current == null) {
				current = element;
				previous = this;
				next = this;
				last = this;
			} else {
				Circle<T> add = new Circle<>(element);
				add.next = this.next;
				add.previous = this;
				add.next.previous = add;
				this.next = add;
				last = add;
			}
			return last;
		}

		Circle<T> getElementAfterNSteps(int n) {
			Circle<T> current = this;
			for (int i = 1; i <= n; i++) {
				current = current.next;
			}
			return current;
		}

		/**
		 * Use this method to debug - it will print the circle as in problem and
		 * also highlight the input firstHead and secondHead
		 * 
		 * @param currentHead
		 * @return
		 */
		String toString(Circle<T> firstHead, Circle<T> secondHead) {
			Circle<T> printHead = this;
			StringBuilder sb = new StringBuilder();
			if (printHead == firstHead)
				sb.append('(');
			else if (printHead == secondHead)
				sb.append('[');
			sb.append(printHead.current);
			if (printHead == firstHead)
				sb.append(')');
			else if (printHead == secondHead)
				sb.append(']');
			sb.append(" ");
			printHead = this.next;
			while (printHead != this) {
				if (printHead == firstHead)
					sb.append('(');
				else if (printHead == secondHead)
					sb.append('[');
				sb.append(printHead.current);
				if (printHead == firstHead)
					sb.append(')');
				else if (printHead == secondHead)
					sb.append(']');
				sb.append(" ");
				printHead = printHead.next;
			}
			return sb.toString();
		}

	}

	@Override
	public String getInputFileName() {
		return "input_12";
	}

}
