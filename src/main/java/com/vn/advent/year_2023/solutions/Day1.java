package com.vn.advent.year_2023.solutions;

import com.vn.advent.Solution;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Day1 implements Solution {

    private static final Pattern PATTERN = Pattern.compile("(?<first>\\d).*(?<last>\\d)|(?<single>\\d)");
    private static final Pattern PATTERN_PART_TWO = Pattern.compile("(?<first>(one)|(two)|(three)|(four)|(five)|(six)|(seven)|(eight)|(nine)|\\d).*(?<last>(one)|(two)|(three)|(four)|(five)|(six)|(seven)|(eight)|(nine)|\\d)|(?<singleword>(one)|(two)|(three)|(four)|(five)|(six)|(seven)|(eight)|(nine))|(?<singledigit>\\d)");
    private static final Map<String, String> WORD_TO_DIGIT_MAP = Map.of("one", "1", "two", "2", "three", "3", "four", "4", "five", "5", "six", "6", "seven", "7", "eight", "8", "nine", "9");


    public static void main(String[] args) {
        Solution solution = new Day1();
        System.out.println(solution.run());
    }

    public String partOne(Stream<String> lines) {
        return String.valueOf(lines.map(PATTERN::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group("single") != null ? matcher.group("single") + matcher.group("single") : matcher.group("first") + matcher.group("last"))
                .mapToInt(Integer::parseInt)
                .sum());
    }

    public String partTwo(Stream<String> lines) {
        return String.valueOf(lines.map(PATTERN_PART_TWO::matcher)
                .filter(Matcher::find)
                .map(matcher -> {
                    final String singleDigit = matcher.group("singledigit");
                    if(singleDigit!=null)
                        return singleDigit + "" + singleDigit;
                    final String singleWord = matcher.group("singleword");
                    if(singleWord!=null) {
                        if(matcher.find(matcher.start()+1)) {
                            final String overlappedWord = matcher.group("singleword");
                            if(overlappedWord != null) {
                                return WORD_TO_DIGIT_MAP.get(singleWord) + WORD_TO_DIGIT_MAP.get(overlappedWord);
                            }
                        }
                    }
                    final String firstWordOrDigit = matcher.group("first");
                    final String lastWordOrDigit = matcher.group("last");
                    return WORD_TO_DIGIT_MAP.getOrDefault(firstWordOrDigit, firstWordOrDigit) + WORD_TO_DIGIT_MAP.getOrDefault(lastWordOrDigit, lastWordOrDigit);
                })
                .mapToInt(Integer::parseInt)
                .sum());
    }

    @Override
    public String getInputFileName() {
        return "2023/input_1";
    }

}
