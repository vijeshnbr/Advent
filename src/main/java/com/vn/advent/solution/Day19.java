package com.vn.advent.solution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19 implements Solution {
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

	private static final Map<String, Eval> OPS = new HashMap<>();
	static {
		OPS.put("addr", addr);
		OPS.put("addi", addi);
		OPS.put("mulr", mulr);
		OPS.put("muli", muli);
		OPS.put("banr", banr);
		OPS.put("bani", bani);
		OPS.put("setr", setr);
		OPS.put("seti", seti);
		OPS.put("eqrr", eqrr);
		OPS.put("eqri", eqri);
		OPS.put("eqir", eqir);
		OPS.put("borr", borr);
		OPS.put("bori", bori);
		OPS.put("gtrr", gtrr);
		OPS.put("gtri", gtri);
		OPS.put("gtir", gtir);
	}

	public static void main(String[] args) {
		LOGGER.setLevel(Level.OFF);
		Solution solution = new Day19();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {
		List<String> allInput = lines.collect(Collectors.toList());
		String ipString = allInput.remove(0);
		int ipIndex = Integer.parseInt(ipString.split(" ")[1]);

		Map<Integer, Instruction> mapOfInstructions = new HashMap<>();
		int ip = 0;
		for (String str : allInput) {
			Instruction i = new Instruction(str);
			mapOfInstructions.put(ip, i);
			ip++;
		}

		Registers initial = Registers.of(6);

		while (mapOfInstructions.containsKey(initial.registers[ipIndex])) {
			int nextInstruction = initial.registers[ipIndex];
			Instruction toRun = mapOfInstructions.get(nextInstruction);
			Registers afterRun = OPS.get(toRun.op)
				.eval(initial, toRun.a, toRun.b, toRun.c);
			afterRun.registers[ipIndex] += 1;

			// System.out.println( afterRun );

			initial = afterRun;
		}

		System.out.print(initial.registers[0]);
	}

	@Override
	public void partTwo(Stream<String> lines) {
		List<String> allInput = lines.collect(Collectors.toList());
		String ipString = allInput.remove(0);
		int ipIndex = Integer.parseInt(ipString.split(" ")[1]);

		Map<Integer, Instruction> mapOfInstructions = new HashMap<>();
		int ip = 0;
		for (String str : allInput) {
			Instruction i = new Instruction(str);
			mapOfInstructions.put(ip, i);
			ip++;
		}

		Registers initial = Registers.of(6);
		initial.registers[0] = 1;

		char[] labels = new char[6];
		labels[0] = 'A';
		labels[1] = 'B';
		labels[2] = 'C';
		labels[3] = '#';
		labels[4] = 'D';
		labels[5] = 'E';

		int ic = 1;

		while (mapOfInstructions.containsKey(initial.registers[ipIndex])) {
			int nextInstruction = initial.registers[ipIndex];
			Instruction toRun = mapOfInstructions.get(nextInstruction);
			Registers afterRun = OPS.get(toRun.op)
				.eval(initial, toRun.a, toRun.b, toRun.c);
			afterRun.registers[ipIndex] += 1;
			if (ic >= 1 && ic < 1000) {
				System.out.println(toRun);
				System.out.print(afterRun);
				if (toRun.c == 3) {
					System.out.print(" * ");
					if (toRun.op.equals("seti")) {
						System.out.print(labels[toRun.c] + " = " + toRun.a);
					}
				}
				System.out.println();
			}
			initial = afterRun;
			ic++;
		}

		System.out.print(initial.registers[0]);
	}

	static class Registers {
		final int[] registers;

		Registers(String s) {
			this.registers = Stream.of(s.split(", "))
				.mapToInt(Integer::parseInt)
				.toArray();
		}

		static Registers of(int size) {
			return new Registers(new int[size]);
		}

		Registers(int[] registers) {
			this.registers = registers;
		}

		Registers getCopyOf() {
			return new Registers(registers.clone());
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
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Registers other = (Registers) obj;
			if (!Arrays.equals(registers, other.registers)) {
				return false;
			}
			return true;
		}
	}

	static class Instruction {
		final int a, b, c;
		final String op;

		Instruction(String str) {
			String[] s = str.split(" ");
			this.op = s[0];
			this.a = Integer.parseInt(s[1]);
			this.b = Integer.parseInt(s[2]);
			this.c = Integer.parseInt(s[3]);
		}

		@Override
		public String toString() {
			return "[" + op + " " + String.valueOf(a) + " " + String.valueOf(b) + " " + String.valueOf(c) + "]";
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
		return "input_19";
	}

}
