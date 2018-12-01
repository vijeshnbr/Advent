package com.vn.advent.solution;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vn.advent.util.FileUtil;

public class Day1 {

	public static void main(String[] args) {
		FileUtil.runCodeForLinesInFile("input1_1", Day1::partOne);
		FileUtil.runCodeForLinesInFile("input1_1", Day1::partTwo);
	}

	private static void partOne(Stream<String> lines) {
		System.out.println(String.format("PART 1: %d",
				lines.mapToLong(Long::parseLong).sum()));
	}

	private static void partTwo(Stream<String> lines) {
		final long INIT_FREQ = 0;
		final Set<Long> frequencies = new HashSet<>();
		frequencies.add(INIT_FREQ);
		AtomicBoolean frequencyFoundTwice = new AtomicBoolean(false);
		List<Long> changeList = lines.mapToLong(Long::parseLong).boxed()
				.collect(Collectors.toList());
		long lastFrequency = INIT_FREQ;
		while (!frequencyFoundTwice.get()) {
			lastFrequency = changeList.stream().reduce(lastFrequency,
					(frequency, change) -> {
						if (frequencyFoundTwice.get()) {
							return frequency;
						}
						long newFreq = frequency + change;
						if (frequencies.contains(newFreq)) {
							frequencyFoundTwice.set(true);
						} else {
							frequencies.add(newFreq);
						}
						return newFreq;
					});
		}

		if (frequencyFoundTwice.get()) {
			System.out.println(String.format("PART 2: %d", lastFrequency));
		}
	}

}
