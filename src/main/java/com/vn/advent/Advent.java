package com.vn.advent;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Level;

import com.vn.advent.year_2018.solutions.*;

public class Advent {

    public static void main(String[] args) {

        Solution.LOGGER.setLevel(Level.OFF);

        //solve(getSolutions_2018());
        solve(getSolutions_2019());

    }

    private static Set<Supplier<Solution>> getSolutions_2019() {
        Set<Supplier<Solution>> solutions_2019 = new HashSet<>();
        solutions_2019.add(com.vn.advent.year_2019.solutions.Day1::new);
        return solutions_2019;
    }

    private static Set<Supplier<Solution>> getSolutions_2018() {
        Set<Supplier<Solution>> solutions_2018 = new HashSet<>();

        solutions_2018.add(Day1::new);
        solutions_2018.add(Day2::new);
        solutions_2018.add(Day3::new);
        solutions_2018.add(Day4::new);
        solutions_2018.add(Day5::new);
        solutions_2018.add(Day6::new);
        solutions_2018.add(Day7::new);
        solutions_2018.add(Day8::new);
        solutions_2018.add(Day9::new);
        solutions_2018.add(Day10::new);
        solutions_2018.add(Day12::new);
        solutions_2018.add(Day13::new);
        solutions_2018.add(Day14::new);
        solutions_2018.add(Day15::new);
        solutions_2018.add(Day16::new);
        solutions_2018.add(Day17::new);
        solutions_2018.add(Day18::new);
        solutions_2018.add(Day20::new);
        solutions_2018.add(Day22::new);
        solutions_2018.add(Day23::new);
        solutions_2018.add(Day24::new);
        solutions_2018.add(Day25::new);

        return solutions_2018;
    }

    private static void solve(Set<Supplier<Solution>> solutions) {
        System.out.println("RUNNING ALL SOLUTIONS..");
        Instant start = Instant.now();
        solutions
                .stream()
                .parallel()
                .map(Supplier::get)
                .map(Solution::run)
                .forEach(System.out::println);
        Instant end = Instant.now();
        System.out
                .println("Total execution time: " + Duration.between(start, end)
                        .toMillis() + " ms");
    }

}
