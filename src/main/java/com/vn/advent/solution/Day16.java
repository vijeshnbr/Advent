package com.vn.advent.solution;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day16 implements Solution {

	private static final Eval addr = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		res.registers[c] = res.registers[a] + res.registers[b];
		return res;
	};

	private static final Eval addi = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		res.registers[c] = res.registers[a] + b;
		return res;
	};

	private static final Eval mulr = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		res.registers[c] = res.registers[a] * res.registers[b];
		return res;
	};

	private static final Eval muli = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		res.registers[c] = res.registers[a] * b;
		return res;
	};

	private static final Eval banr = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		res.registers[c] = res.registers[a] & res.registers[b];
		return res;
	};

	private static final Eval bani = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		res.registers[c] = res.registers[a] & b;
		return res;
	};

	private static final Eval borr = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		res.registers[c] = res.registers[a] | res.registers[b];
		return res;
	};

	private static final Eval bori = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		res.registers[c] = res.registers[a] | b;
		return res;
	};

	private static final Eval setr = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		res.registers[c] = res.registers[a];
		return res;
	};

	private static final Eval seti = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		res.registers[c] = a;
		return res;
	};

	private static final Eval gtir = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		Boolean val = a > res.registers[b];
		res.registers[c] = val ? 1 : 0;
		return res;
	};

	private static final Eval gtri = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		Boolean val = res.registers[a] > b;
		res.registers[c] = val ? 1 : 0;
		return res;
	};

	private static final Eval gtrr = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		Boolean val = res.registers[a] > res.registers[b];
		res.registers[c] = val ? 1 : 0;
		return res;
	};

	private static final Eval eqir = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		Boolean val = a == res.registers[b];
		res.registers[c] = val ? 1 : 0;
		return res;
	};

	private static final Eval eqri = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		Boolean val = res.registers[a] == b;
		res.registers[c] = val ? 1 : 0;
		return res;
	};

	private static final Eval eqrr = (registers, a, b, c) -> {
		Registers res = registers.getCopyOf();
		Boolean val = res.registers[a] == res.registers[b];
		res.registers[c] = val ? 1 : 0;
		return res;
	};

	private static final Eval[] OPS = new Eval[]{addr, addi, mulr, muli, banr,
			bani, borr, bori, setr, seti, gtir, gtri, gtrr, eqir, eqri, eqrr};

	private static final Pattern REGISTER = Pattern.compile("\\[(.*)\\]");

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day16();
		System.out.println(solution.run());
	}

	@Override
	public String partOne(Stream<String> lines) {
		String[] parts = lines.collect(Collectors.joining("SEPARATOR"))
			.split("(SEPARATOR){4}");
		String inputPartOne = parts[0];
		String[] samplesInput = inputPartOne.split("(SEPARATOR){2}");
		List<Sample> samples = Stream.of(samplesInput)
			.map(this::mapToSample)
			.collect(Collectors.toList());

		return String.valueOf(samples.stream()
			.filter(this::behavesLike3OrMoreOpCodes)
			.count());

	}

	@Override
	public String partTwo(Stream<String> lines) {
		String[] parts = lines.collect(Collectors.joining("SEPARATOR"))
			.split("(SEPARATOR){4}");
		String inputPartOne = parts[0];
		String[] samplesInput = inputPartOne.split("(SEPARATOR){2}");
		List<Sample> samples = Stream.of(samplesInput)
			.map(this::mapToSample)
			.collect(Collectors.toList());

		// Group samples by op code
		Map<Integer, List<Sample>> allSamplesGrouped = samples.stream()
			.collect(Collectors.groupingBy(Sample::getOpCode));

		Map<Integer, Eval> knownMapOfOpCodeAndOperation = new HashMap<>();

		while (true) {
			// stream allSamplesGrouped and try to find opcodes. Populate above
			// map
			// knownMapOfOpCodeAndOperation with codes and operation.
			Map<Integer, Eval> opCodesFound = allSamplesGrouped.values()
				.stream()
				.flatMap(List::stream)
				.map(s -> findOpCodeAndOpThatBehavesLikeExactlyOneOperation(s,
						knownMapOfOpCodeAndOperation.values()))
				.filter(m -> !m.isEmpty())
				.distinct()
				.flatMap(m -> m.entrySet()
					.stream())
				.collect(Collectors.toMap(Map.Entry::getKey,
						Map.Entry::getValue));

			// remove entry for the opcodes that have already been found for
			// performance
			opCodesFound.keySet()
				.stream()
				.forEach(allSamplesGrouped::remove);

			knownMapOfOpCodeAndOperation.putAll(opCodesFound);
			if (knownMapOfOpCodeAndOperation.size() == 16)
				break;
		}

		String inputPartTwo = parts[1];
		Registers initial = Registers.of(4);

		String[] programInput = inputPartTwo.split("SEPARATOR");

		List<Instruction> intructions = Stream.of(programInput)
			.map(Instruction::new)
			.collect(Collectors.toList());

		for (Instruction i : intructions) {
			initial = knownMapOfOpCodeAndOperation.get(i.opCode)
				.eval(initial, i.a, i.b, i.c);
		}

		return String.valueOf(initial.registers[0]);
	}

	Map<Integer, Eval> findOpCodeAndOpThatBehavesLikeExactlyOneOperation(
			Sample s, Collection<Eval> setOfAlreadyFoundOpName) {
		int matchCounter = 0;
		int opsCounter = 0;
		Integer lastMatchedOp = null;
		Map<Integer, Eval> knownOpCodeAndName = new HashMap<>();

		while (opsCounter < OPS.length) {
			Eval op = OPS[opsCounter];
			if (!setOfAlreadyFoundOpName.contains(op)) {
				Registers res = op.eval(s.before, s.i.a, s.i.b, s.i.c);
				if (res.equals(s.after)) {
					matchCounter++;
					lastMatchedOp = opsCounter;
				}
			}
			opsCounter++;
		}

		if (matchCounter == 1) {
			knownOpCodeAndName.put(s.i.opCode, OPS[lastMatchedOp]);
		}
		return knownOpCodeAndName;
	}

	boolean behavesLike3OrMoreOpCodes(Sample s) {
		int matchCounter = 0;
		int opsCounter = 0;

		while (matchCounter < 3 && opsCounter < OPS.length) {
			Registers res = OPS[opsCounter].eval(s.before, s.i.a, s.i.b, s.i.c);
			if (res.equals(s.after)) {
				matchCounter++;
			}
			opsCounter++;
		}

		return matchCounter == 3;
	}

	private Sample mapToSample(String str) {
		String[] s = str.split("SEPARATOR");
		Matcher m = REGISTER.matcher(s[0]);
		m.find();
		Registers before = new Registers(m.group(1));
		Instruction i = new Instruction(s[1]);
		m = REGISTER.matcher(s[2]);
		m.find();
		Registers after = new Registers(m.group(1));
		return new Sample(before, i, after);
	}

	static class Sample {
		Registers before, after;
		Instruction i;

		Sample(Registers before, Instruction i, Registers after) {
			this.before = before;
			this.i = i;
			this.after = after;
		}

		int getOpCode() {
			return i.opCode;
		}

		@Override
		public String toString() {
			return "before=" + before + ", inst=" + i + ", after=" + after;
		}

	}

	static class Registers {
		final int[] registers;

		Registers(String s) {
			this.registers = Stream.of(s.split(", "))
				.mapToInt(Integer::parseInt)
				.toArray();
		}

		Registers(int[] registers) {
			this.registers = registers;
		}

		Registers getCopyOf() {
			return new Registers(registers.clone());
		}

		static Registers of(int size) {
			return new Registers(new int[size]);
		}

		@Override
		public String toString() {
			return "[" + Arrays.toString(registers) + "]";
		}

		@Override
		public int hashCode() {
			return Objects.hash(registers);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Registers other = (Registers) obj;
			if (!Arrays.equals(registers, other.registers))
				return false;
			return true;
		}
	}

	static class Instruction {
		final int a, b, c;
		final int opCode;

		Instruction(String str) {
			String[] s = str.split(" ");
			this.opCode = Integer.parseInt(s[0]);
			this.a = Integer.parseInt(s[1]);
			this.b = Integer.parseInt(s[2]);
			this.c = Integer.parseInt(s[3]);
		}

		@Override
		public String toString() {
			return "[" + String.valueOf(opCode) + " " + String.valueOf(a) + " "
					+ String.valueOf(b) + " " + String.valueOf(c) + "]";
		}

	}

	/**
	 * Functional Interface to define a evaluator takes 4 inputs and returns 1
	 * output, specifically input Registers, a, b, c and output Registers
	 */
	static interface Eval {
		Registers eval(Registers r, int a, int b, int c);
	}

	@Override
	public String getInputFileName() {
		return "input_16";
	}

}
