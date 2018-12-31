package com.vn.advent.solution;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day4 implements Solution {

	private static final Pattern PATTERN_ID = Pattern.compile("[#](\\d+)");
	private static final Pattern PATTERN_SLEEPTIME = Pattern
		.compile("(\\d+)]\\sf");
	private static final Pattern PATTERN_WAKETIME = Pattern
		.compile("(\\d+)]\\sw");

	public static void main(String[] args) {
		Solution solution = new Day4();
		System.out.println(solution.run());
	}

	@Override
	public String partOne(Stream<String> lines) {
		List<Guard> listOfGuards = makeListOfGuards(lines);

		Optional<Guard> guardWhoSleptMost = listOfGuards.stream()
			.max(Comparator.comparing(Guard::getSleepTime));

		return guardWhoSleptMost.flatMap(guard -> {
			int id = guard.getId();
			return getMinuteWhichWasSleptMostTimesForAGuard(guard)
				.map(minuteWhichWasSleptMostTimes -> id
						* minuteWhichWasSleptMostTimes)
				.map(String::valueOf);
		})
			.get();
	}

	@Override
	public String partTwo(Stream<String> lines) {
		List<Guard> listOfGuards = makeListOfGuards(lines);

		Optional<Guard> guardWhoSleptMostForSameMinute = listOfGuards.stream()
			.filter(guard -> guard.getMaxOfSleepTime()
				.isPresent())
			.max(Comparator.comparing(guard -> guard.getMaxOfSleepTime()
				.get()));

		return guardWhoSleptMostForSameMinute.flatMap(guard -> {
			int id = guard.getId();
			return getMinuteWhichWasSleptMostTimesForAGuard(guard)
				.map(minuteWhichWasSleptMostTimes -> id
						* minuteWhichWasSleptMostTimes)
				.map(String::valueOf);
		})
			.get();
	}

	private Optional<Integer> getMinuteWhichWasSleptMostTimesForAGuard(
			Guard guard) {
		return guard.getMaxOfSleepTime()
			.flatMap(mostSleptForSameMinute -> getFirstIndexOfElementInArray(
					guard.midnightHourAccumulated, mostSleptForSameMinute));
	}

	private List<Guard> makeListOfGuards(Stream<String> lines) {
		Deque<Shift> stackOfShifts = new ArrayDeque<>();

		lines.sorted()
			.forEach(line -> processLine(line, stackOfShifts));

		Map<Integer, List<Shift>> mapOfGuardIdAndShifts = stackOfShifts.stream()
			.collect(Collectors.groupingBy(Shift::getGuardId));

		List<Guard> listOfGuards = mapOfGuardIdAndShifts.entrySet()
			.stream()
			.map(this::mapShiftsOfGuardToGuard)
			.collect(Collectors.toList());
		return listOfGuards;
	}

	private void processLine(String str, Deque<Shift> stackOfShifts) {
		Optional<Integer> guardId = extractNumber(str, PATTERN_ID, 1);
		guardId.map(Shift::new)
			.ifPresent(stackOfShifts::push);
		if (!guardId.isPresent()) {
			Shift lastShift = stackOfShifts.peek();
			Optional<Integer> sleepTime = extractNumber(str, PATTERN_SLEEPTIME,
					1);
			sleepTime.ifPresent(minute -> setValueFromIndexInArray(
					lastShift.midnightHour, minute, 1));
			if (!sleepTime.isPresent()) {
				Optional<Integer> wakeTime = extractNumber(str,
						PATTERN_WAKETIME, 1);
				wakeTime.ifPresent(minute -> setValueFromIndexInArray(
						lastShift.midnightHour, minute, 0));
			}
		}
	}

	private Guard mapShiftsOfGuardToGuard(
			Map.Entry<Integer, List<Shift>> shiftsOfGuard) {
		int id = shiftsOfGuard.getKey();
		List<Shift> shifts = shiftsOfGuard.getValue();
		int[] arr = new int[60];
		int[] midnightHourAccumulated = shifts.stream()
			.map(shift -> shift.midnightHour)
			.reduce(arr, this::addIntArrays);
		return new Guard(id, midnightHourAccumulated);
	}

	private Optional<Integer> getFirstIndexOfElementInArray(int[] arr,
			int val) {
		Optional<Integer> index = Optional.empty();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == val) {
				index = Optional.of(i);
				break;
			}
		}
		return index;
	}

	private int[] addIntArrays(int[] arr1, int[] arr2) {
		int[] arr = new int[60];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = arr1[i] + arr2[i];
		}
		return arr;
	}

	private void setValueFromIndexInArray(int[] midnightHour, int minute,
			int val) {
		for (int i = minute; i < midnightHour.length; i++) {
			midnightHour[i] = val;
		}
	}

	private Optional<Integer> extractNumber(String str, Pattern p, int group) {
		Optional<Integer> number = Optional.empty();
		Matcher m = p.matcher(str);
		boolean numberFound = m.find();
		if (numberFound) {
			number = Optional.of(Integer.parseInt(m.group(group)));
		}
		return number;
	}

	static class Shift {
		final int guardId;
		int[] midnightHour = new int[60];

		Shift(int guardId) {
			this.guardId = guardId;
		}

		@Override
		public String toString() {
			return "Shift [guardId=" + guardId + ", midnightHour="
					+ Arrays.toString(midnightHour) + "]";
		}

		public int getGuardId() {
			return guardId;
		}
	}

	private static class Guard {
		final int id;
		final int[] midnightHourAccumulated;

		Guard(int id, int[] midnightHourAccumulated) {
			this.id = id;
			this.midnightHourAccumulated = midnightHourAccumulated;
		}

		@Override
		public String toString() {
			return "Guard [id=" + id + ", midnightHourAccumulated="
					+ Arrays.toString(midnightHourAccumulated) + "]";
		}

		public int getId() {
			return id;
		}

		public int getSleepTime() {
			return Arrays.stream(this.midnightHourAccumulated)
				.sum();
		}

		public Optional<Integer> getMaxOfSleepTime() {
			return Arrays.stream(this.midnightHourAccumulated)
				.boxed()
				.max(Integer::compare);
		}
	}

	@Override
	public String getInputFileName() {
		return "input_4";
	}

}
