package io.github.djhworld.bf.compile;

import io.github.djhworld.bf.Operation;

import java.util.ArrayList;
import java.util.List;

import static io.github.djhworld.bf.Instruction.*;
import static java.lang.Integer.MIN_VALUE;

public class Compiler {
    public List<Operation> compile(String input) throws CompileException {
        if(input == null || "".equals(input))
            throw new CompileException("No input provided");

        char[] tokens = input.toCharArray();

        List<Operation> operations = compileAndFoldTokens(tokens);
        optimiseJumps(operations);
        return operations;
    }

    private List<Operation> compileAndFoldTokens(char[] tokens) {
        List<Operation> operations = new ArrayList<>();
        int instructionPointer = 0;

        while (instructionPointer < tokens.length) {
            int count = 1;
            switch (tokens[instructionPointer]) {
                case '<':
                    count = foldToken('<', tokens, instructionPointer);
                    operations.add(new Operation(DEC_DP, count));
                    break;
                case '>':
                    count = foldToken('>', tokens, instructionPointer);
                    operations.add(new Operation(INC_DP, count));
                    break;
                case '-':
                    count = foldToken('-', tokens, instructionPointer);
                    operations.add(new Operation(DEC, count));
                    break;
                case '+':
                    count = foldToken('+', tokens, instructionPointer);
                    operations.add(new Operation(INC, count));
                    break;
                case '.':
                    count = foldToken('.', tokens, instructionPointer);
                    operations.add(new Operation(PRINT, count));
                    break;
                case ',':
                    count = foldToken(',', tokens, instructionPointer);
                    operations.add(new Operation(READ, count));
                    break;
                case '[':
                    operations.add(new Operation(JUMP_IF_ZERO, MIN_VALUE));
                    break;
                case ']':
                    operations.add(new Operation(JUMP_IF_NOT_ZERO, MIN_VALUE));
                    break;
            }
            instructionPointer += count;
        }

        return operations;
    }

    /**
     * For start loop ([) , find the position of the end of the loop and set it as the operation argument
     * For end loop (]), find the position of the start of the loop and set it as the operation argument
     *
     * @param operations
     * @throws CompileException
     */
    private void optimiseJumps(List<Operation> operations) throws CompileException {
        int instructionPointer = 0;
        while (instructionPointer < operations.size()) {
            Operation operation = operations.get(instructionPointer);

            switch (operation.instruction) {
                case JUMP_IF_ZERO:
                    if (operation.argument == MIN_VALUE) {
                        int endLoopPos = findEndOfLoop(instructionPointer, operations);
                        operations.set(instructionPointer, new Operation(operation.instruction, endLoopPos));
                    }
                    break;
                case JUMP_IF_NOT_ZERO:
                    if (operation.argument == MIN_VALUE) {
                        int startLoopPos = findStartOfLoop(instructionPointer, operations);
                        operations.set(instructionPointer, new Operation(operation.instruction, startLoopPos));
                    }
                    break;
            }
            instructionPointer++;
        }
    }

    private int foldToken(char token, char[] tokens, int currentPos) {
        int count = 1;

        if (currentPos == tokens.length-1)
            return count;

        for (int i = currentPos + 1; i < tokens.length; i++) {
            if (tokens[i] == token)
                count++;
            else
                break;
        }

        return count;
    }

    private int findEndOfLoop(int currentPosition, List<Operation> operations) throws CompileException {
        int depth = 1;

        while (depth != 0) {
            currentPosition++;

            if (currentPosition == operations.size())
                throw new CompileException("Invalid syntax, no ] found for start-loop");

            switch (operations.get(currentPosition).instruction) {
                case JUMP_IF_ZERO:
                    depth++;
                    break;
                case JUMP_IF_NOT_ZERO:
                    depth--;
                    break;
            }
        }

        return currentPosition;
    }

    private int findStartOfLoop(int currentPosition, List<Operation> operations) throws CompileException {
        int depth = 1;

        while (depth != 0) {
            currentPosition--;

            if (currentPosition == -1)
                throw new CompileException("Invalid syntax, no [ found for end-loop");

            switch (operations.get(currentPosition).instruction) {
                case JUMP_IF_NOT_ZERO:
                    depth++;
                    break;
                case JUMP_IF_ZERO:
                    depth--;
                    break;
            }
        }

        return currentPosition;
    }
}