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
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 implements Solution {

	private static final Pattern PATTERN_UPPER_CHAR = Pattern
		.compile(".+([A-Z]).+([A-Z])");

	final Map<String, Step> mapOfIdAndStep = new ConcurrentHashMap<>();
	final Queue<Step> stepQueue = new PriorityQueue<>(
			Comparator.comparing(Step::getId));
	final Queue<Worker> workerQueue = new PriorityQueue<>(
			Comparator.comparing(Worker::getWorkTimeRemaining));

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day7();
		solution.run();
	}

	public void partOne(Stream<String> lines) {
		inititalize(lines);
		while (!stepQueue.isEmpty()) {
			Step step = stepQueue.poll();
			System.out.print(step.execute());
			step.parentSteps.stream()
				.forEach(parent -> {
					if (parent.isAvailable()) {
						stepQueue.offer(parent);
					}
				});
		}
	}

	public void partTwo(Stream<String> lines) {
		inititalize(lines);
		int totalTime = 0;
		while (!stepQueue.isEmpty()) {
			LOGGER.info("Step queue " + stepQueue);
			while (workerQueue.size() < 5 && !stepQueue.isEmpty()) {
				Worker worker = new Worker(stepQueue.poll());
				workerQueue.offer(worker);
			}
			LOGGER.info("Worker queue " + workerQueue);
			while (!workerQueue.isEmpty()) {
				while (workerQueue.size() < 5 && !stepQueue.isEmpty()) {
					Worker worker = new Worker(stepQueue.poll());
					workerQueue.offer(worker);
				}
				Worker doneWorker = workerQueue.poll();
				Integer timeTaken = doneWorker.getWorkTimeRemaining();
				String stepExecuted = doneWorker.step.execute();
				LOGGER.info("Executed : " + stepExecuted);
				List<Step> parentSteps = doneWorker.step.parentSteps;
				parentSteps.stream()
					.forEach(parent -> {
						if (parent.isAvailable()) {
							LOGGER.info("Parent " + parent + " available");
							stepQueue.offer(parent);
						}
					});
				totalTime += timeTaken;
				LOGGER.info("Time spent " + totalTime);
				List<Worker> remainingWorkers = workerQueue.stream()
					.collect(Collectors.toList());
				workerQueue.clear();
				remainingWorkers.stream()
					.map(remainingWorker -> {
						remainingWorker.addWorkedTime(timeTaken);
						return remainingWorker;
					})
					.forEach(workerQueue::offer);

			}
		}
		System.out.print(totalTime);
	}

	public static class Worker {
		final Step step;
		int workedTime;
		Worker(Step step) {
			this.step = step;
		}
		public Integer getWorkTimeRemaining() {
			return this.step.getDuration() - this.workedTime;
		}
		public void addWorkedTime(int time) {
			this.workedTime += time;
		}
		public void work() {
			step.execute();
		}
		@Override
		public String toString() {
			return "Worker [step=" + step + ", workedTime=" + workedTime
					+ ", toDo=" + getWorkTimeRemaining() + "]";
		}
	}

	public static class Step {
		final String id;
		Set<Step> dependentSteps = new HashSet<>();
		List<Step> parentSteps = new ArrayList<>();
		Step(String id) {
			this.id = id;
		}
		boolean isAvailable() {
			return this.dependentSteps.isEmpty();
		}
		public String execute() {
			this.parentSteps.stream()
				.forEach(step -> step.dependentSteps.remove(this));
			return id;
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
			return "Step [id=" + id + "]";
		}
		public String getId() {
			return id;
		}
		public Integer getDuration() {
			return id.chars()
				.sum() - 4;
		}
	}

	private void inititalize(Stream<String> lines) {
		mapOfIdAndStep.clear();
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

		mapOfIdAndStep.values()
			.stream()
			.filter(Step::isAvailable)
			.forEach(stepQueue::offer);
	}

	@Override
	public String getInputFileName() {
		return "input_7";
	}

}
