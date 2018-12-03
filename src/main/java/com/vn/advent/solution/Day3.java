package com.vn.advent.solution;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day3 implements Solution {

	public static void main(String[] args) {
		Solution solution = new Day3();
		solution.run();
	}

	@Override
	public void partOne(Stream<String> lines) {
		lines.map(this::extractParams).forEach(System.out::println);
	}

	private Claim extractParams(String str) {
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(str);
		List<String> groups = new ArrayList<>();
		while (m.find()) {
			groups.add(m.group());
		}
		int[] params = groups.stream().mapToInt(Integer::parseInt).toArray();
		Claim claim = new Claim(params[0], params[1], params[2], params[3],
				params[4]);
		return claim;
	}

	private static class Claim {
		final int id;
		final int x;
		final int y;
		final int width;
		final int height;
		private Claim(int id, int x, int y, int width, int height) {
			this.id = id;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		@Override
		public String toString() {
			return "Claim [id=" + id + ", x=" + x + ", y=" + y + ", width="
					+ width + ", height=" + height + "]";
		}
	}

	@Override
	public void partTwo(Stream<String> lines) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getInputFileName() {
		return "input_3";
	}

}
