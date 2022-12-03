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

        solve(getSolutions_2018());
        //solve(getSolutions_2019());

    }

    private static Set<Supplier<Solution>> getSolutions_2019() {
        Set<Supplier<Solution>> solutions_2019 = new HashSet<>();
        solutions_2019.add(com.vn.advent.year_2019.solutions.Day1::new);
        return solutions_2019;
    }

    private static Set<Supplier<Solution>> getSolutions_2018() {
        return Set.of(
                Day1::new,
                Day2::new,
                Day3::new,
                Day4::new,
                Day5::new,
                Day6::new,
                Day7::new,
                Day8::new,
                Day9::new,
                Day10::new,
                //Day11::new,
                Day12::new,
                Day13::new,
                Day14::new,
                Day15::new,
                Day16::new,
                Day17::new,
                Day18::new,
                //Day19::new,
                Day20::new,
                //Day21::new,
                Day22::new,
                Day23::new,
                Day24::new,
                Day25::new
        );
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
