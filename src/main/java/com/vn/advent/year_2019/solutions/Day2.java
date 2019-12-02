package com.vn.advent.year_2019.solutions;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import com.vn.advent.Solution;

public class Day2 implements Solution {

    public static void main(String[] args) {
        Solution solution = new Day2();
        System.out.println(solution.run());
    }

    public String partOne(Stream<String> lines) {

        String[] firstLine = lines
                .map(line -> line.split(","))
                .findFirst().get();

        int[] positions = Arrays.stream(firstLine)
                .mapToInt(Integer::parseInt)
                .toArray();

        int lastOpCodePos = 0;
        for (int i = 0; i < positions.length; i++) {
            if (i % 4 == 0) {
                lastOpCodePos = i;
            }
            int opCodePos = lastOpCodePos;
            int opCode = positions[opCodePos];

            Optional<Integer> result = Optional.empty();
            if (opCode == 99) {
                break;
            }

            if ((opCode + 3) < positions.length) {
                int operandOnePosition = positions[opCodePos + 1];
                int operandTwoPosition = positions[opCodePos + 2];
                int outputPosition = positions[opCodePos + 3];
                if (outputPosition < positions.length) {
                    if (opCode == 1) {
                        result = Optional.of(positions[operandOnePosition] + positions[operandTwoPosition]);
                    } else if (opCode == 2) {
                        result = Optional.of(positions[operandOnePosition] + positions[operandTwoPosition]);
                    }
                    result.ifPresent(v -> positions[outputPosition] = v);
                }
            }
            System.out.println(Arrays.toString(positions));
        }
        return String.valueOf(positions[0]);
    }

    public String partTwo(Stream<String> lines) {
        return "Solving part 2";
    }


    @Override
    public String getInputFileName() {
        return "2019/input_2";
    }

}