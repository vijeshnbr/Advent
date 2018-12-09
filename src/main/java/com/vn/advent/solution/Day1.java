package com.vn.advent.solution;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.vn.advent.util.FileUtil;

public class Day1 implements Solution {

	public static void main(String[] args) {
		Solution solution = new Day1();
		solution.run();
		FileUtil.runCodeForLinesInFile(solution.getInputFileName(),
				Day1::partTwo_Alt);
	}

	public void partOne(Stream<String> lines) {
		System.out.print(lines.mapToLong(Long::parseLong)
			.sum());
	}

	public void partTwo(Stream<String> lines) {
		final long INIT_FREQ = 0;
		final Set<Long> frequencies = new HashSet<>();
		frequencies.add(INIT_FREQ);
		AtomicBoolean frequencyFoundTwice = new AtomicBoolean(false);
		List<Long> changeList = lines.mapToLong(Long::parseLong)
			.boxed()
			.collect(Collectors.toList());
		long lastFrequency = INIT_FREQ;
		while (!frequencyFoundTwice.get()) {
			lastFrequency = changeList.stream()
				.reduce(lastFrequency, (frequency, change) -> {
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
			System.out.print(lastFrequency);
		}
	}

	private static void partTwo_Alt(Stream<String> lines) {
		List<Long> changeList = lines.mapToLong(Long::parseLong)
			.boxed()
			.collect(Collectors.toList());
		final long INIT_FREQ = 0;
		final Set<Long> frequencies = new HashSet<>();
		frequencies.add(INIT_FREQ);
		FrequenciesStack frequenciesStack = new FrequenciesStack(INIT_FREQ,
				frequencies);
		applyChangeListToFrequencies(Collections.unmodifiableList(changeList),
				frequenciesStack);
	}

	private static void applyChangeListToFrequencies(
			final List<Long> changeList, FrequenciesStack frequenciesStack) {

		Optional<Long> firstDuplicateFrequency = changeList.stream()
			.map(mapToNewFrequencyAndSetLastFrequency(frequenciesStack))
			.filter(newFrequency -> !frequenciesStack.getFrequencies()
				.add(newFrequency))
			.findFirst();

		if (firstDuplicateFrequency.isPresent()) {
			System.out.print(String.format("Alternate solution to PART 2: %d",
					firstDuplicateFrequency.get()));
		} else {
			applyChangeListToFrequencies(changeList, frequenciesStack);
		}

	}

	private static Function<Long, Long> mapToNewFrequencyAndSetLastFrequency(
			final FrequenciesStack frequenciesStack) {
		return change -> {
			long newFrequency = frequenciesStack.getLastFrequency() + change;
			frequenciesStack.setLastFrequency(newFrequency);
			return newFrequency;
		};
	}

	private static class FrequenciesStack {
		Long lastFrequency;
		final Set<Long> frequencies = new HashSet<>();
		private FrequenciesStack(Long lastFrequency, Set<Long> frequencies) {
			this.lastFrequency = lastFrequency;
			this.frequencies.addAll(frequencies);
		}
		private Long getLastFrequency() {
			return lastFrequency;
		}
		private void setLastFrequency(Long lastFrequency) {
			this.lastFrequency = lastFrequency;
		}
		private Set<Long> getFrequencies() {
			return frequencies;
		}
	}

	@Override
	public String getInputFileName() {
		return "input_1";
	}

}
