package com.vn.advent.year_2022.solutions;

import com.vn.advent.Solution;

import java.util.Map;
import java.util.stream.Stream;

public class Day2 implements Solution {

	private static final Map<String, Rps> INPUT_MAP_SHAPE = Map.of(
			"A", Rps.ROCK,
			"X", Rps.ROCK,
			"B", Rps.PAPER,
			"Y", Rps.PAPER,
			"C", Rps.SCISSORS,
			"Z", Rps.SCISSORS);

	private static final Map<String, Round> INPUT_MAP_OUTCOME = Map.of(
			"X", Round.LOSS,
			"Y", Round.DRAW,
			"Z", Round.WIN);

	public static void main(String[] args) {
		Solution solution = new Day2();
		System.out.println(solution.run());
	}

	public String partOne(Stream<String> lines) {
		return String.valueOf(lines
				.mapToInt(line -> {
					final String[] inputArray = line.split(" ");
					final Rps opponentShape = INPUT_MAP_SHAPE.get(inputArray[0]);
					final Rps myShape = INPUT_MAP_SHAPE.get(inputArray[1]);
					return myShape.shapeScore + myShape.play(opponentShape).roundScore;
				})
				.sum());
	}

	public String partTwo(Stream<String> lines) {
		return String.valueOf(lines
				.mapToInt(line -> {
					final String[] inputArray = line.split(" ");
					final Rps opponentShape = INPUT_MAP_SHAPE.get(inputArray[0]);
					final Round myRoundOutcome = INPUT_MAP_OUTCOME.get(inputArray[1]);
					return myRoundOutcome.roundScore + myRoundOutcome.withInputShape(opponentShape).shapeScore;
				})
				.sum());
	}


	@Override
	public String getInputFileName() {
		return "2022/input_2";
	}

	private static enum Rps {
		ROCK(1), PAPER(2), SCISSORS(3);
		final Integer shapeScore;
		Rps(Integer shapeScore){
			this.shapeScore = shapeScore;
		}

		public Round play(Rps opponentShape) {
			if (this == ROCK && opponentShape == PAPER || this == SCISSORS && opponentShape == ROCK || this == PAPER && opponentShape == SCISSORS)
				return Round.LOSS;
			if (this == PAPER && opponentShape == ROCK || this == ROCK && opponentShape == SCISSORS || this == SCISSORS && opponentShape == PAPER)
				return Round.WIN;
			return Round.DRAW;
		}
	}

	private static enum Round {
		WIN(6), LOSS(0), DRAW(3);
		final Integer roundScore;
		Round(Integer roundScore){
			this.roundScore = roundScore;
		}

		public Rps withInputShape(Rps opponentInput) {
			if(this == WIN && opponentInput == Rps.ROCK || this == LOSS && opponentInput == Rps.SCISSORS)
				return Rps.PAPER;
			if(this == WIN && opponentInput == Rps.SCISSORS || this == LOSS && opponentInput == Rps.PAPER)
				return Rps.ROCK;
			if(this == WIN && opponentInput == Rps.PAPER || this == LOSS && opponentInput == Rps.ROCK)
				return Rps.SCISSORS;
			return opponentInput;
		}
	}
}
