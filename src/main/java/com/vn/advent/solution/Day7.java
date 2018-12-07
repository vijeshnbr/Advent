package com.vn.advent.solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day7 implements Solution {

	private static final Pattern PATTERN_UPPER_CHAR = Pattern
			.compile(".+([A-Z]).+([A-Z])");

	public static void main(String[] args) {
		Solution solution = new Day7();
		solution.run();
	}

	public void partOne(Stream<String> lines) {
		final Map<String, Step> mapOfIdAndStep = new ConcurrentHashMap<>();
		final Queue<Step> queue = new PriorityQueue<>(
				Comparator.comparing(Step::getId));
		lines.forEach(str -> {
			Matcher m = PATTERN_UPPER_CHAR.matcher(str);
			if (m.find()) {
				String stepId = m.group(2);
				String dependentStepId = m.group(1);
				Step step = mapOfIdAndStep.computeIfAbsent(stepId, Step::new);
				Step dependentStep = mapOfIdAndStep
						.computeIfAbsent(dependentStepId, Step::new);
				step.dependentSteps.add(dependentStep);
				dependentStep.parentSteps.add(step);
			}
		});

		mapOfIdAndStep.values().stream().filter(Step::isAvailable)
				.forEach(queue::offer);

		while (!queue.isEmpty()) {
			Step toExecute = queue.poll();
			toExecute.execute();
			toExecute.parentSteps.stream().forEach(parent -> {
				if (parent.isAvailable()) {
					queue.offer(parent);
				}
			});
		}
	}

	public void partTwo(Stream<String> lines) {

	}

	static class Step {
		final String id;
		Set<Step> dependentSteps = new HashSet<>();
		List<Step> parentSteps = new ArrayList<>();
		Step(String id) {
			this.id = id;
		}
		boolean isAvailable() {
			return this.dependentSteps.isEmpty();
		}
		void execute() {
			System.out.print(this);
			this.parentSteps.stream()
					.forEach(step -> step.dependentSteps.remove(this));
		}
		@Override
		public int hashCode() {
			return Objects.hash(id);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Step other = (Step) obj;
			if (id != other.id)
				return false;
			return true;
		}
		@Override
		public String toString() {
			return id;
		}
		public String getId() {
			return id;
		}
	}

	@Override
	public String getInputFileName() {
		return "input_7";
	}

}
