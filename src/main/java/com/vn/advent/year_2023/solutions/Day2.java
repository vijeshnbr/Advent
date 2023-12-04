package com.vn.advent.year_2023.solutions;

import com.vn.advent.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day2 implements Solution {
    private static final Pattern PATTERN_ID = Pattern.compile("(?<id>\\d+):");
    private static final Pattern PATTERN_CUBE = Pattern.compile("(?<quantity>\\d+) (?<color>red|green|blue)");

    public static void main(String[] args) {
        Solution solution = new Day2();
        System.out.println(solution.run());
    }

    public String partOne(Stream<String> lines) {
        return String.valueOf(lines.map(this::lineToGame)
                .filter(Game::isPossible)
                .mapToInt(Game::id)
                .sum());
    }

    public String partTwo(Stream<String> lines) {
        return String.valueOf(lines.map(this::lineToGame)
                .mapToInt(Game::powerOfMinimumSet)
                .sum());
    }

    public record Game(Integer id, List<Cube> cubes) {
        public boolean isPossible() {
            return !cubes.stream()
                    .anyMatch(Cube::isUnAvailable);
        }

        public Integer powerOfMinimumSet() {
            return cubes.stream()
                    .collect(Collectors.groupingBy(Cube::color, Collectors.mapping(Cube::quantity, Collectors.maxBy(Integer::compareTo))))
                    .values()
                    .stream()
                    .filter(Optional::isPresent)
                    .mapToInt(Optional::get)
                    .reduce((i, j) -> i * j)
                    .getAsInt();
        }
    }

    private record Cube(Color color, Integer quantity) {
        public boolean isUnAvailable() {
            return switch (color) {
                case red -> quantity > 12;
                case green -> quantity > 13;
                case blue -> quantity > 14;
            };
        }
    }

    private enum Color {red, green, blue}

    private Game lineToGame(String line) {
        final Integer id = Optional.ofNullable(PATTERN_ID.matcher(line))
                .filter(Matcher::find)
                .map(m -> m.group("id"))
                .map(Integer::parseInt)
                .get();
        final Matcher cubeMatcher = PATTERN_CUBE.matcher(line);
        final List<Cube> cubes = new ArrayList<>();
        while (cubeMatcher.find()) {
            cubes.add(new Cube(Color.valueOf(cubeMatcher.group("color")), Integer.parseInt(cubeMatcher.group("quantity"))));
        }
        return new Game(id, cubes);
    }

    @Override
    public String getInputFileName() {
        return "2023/input_2";
    }

}
