package io.github.djhworld.bf.vm;

import io.github.djhworld.bf.Operation;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

public class Machine {
    private final byte[] memory;
    private final InputStream in;
    private final PrintStream out;
    private int dataPointer;
    private int instructionPointer;

    public Machine(InputStream in, PrintStream out, byte[] memory) {
        this.in = in;
        this.out = out;
        this.memory = memory;
        this.dataPointer = 0;
        this.instructionPointer = 0;
    }

    public void execute(List<Operation> operations) throws IllegalArgumentException, IOException {
        while (instructionPointer < operations.size()) {
            Operation operation = operations.get(instructionPointer);

            switch (operation.instruction) {
                case INC_DP:
                    dataPointer += operation.argument;
                    break;
                case DEC_DP:
                    dataPointer -= operation.argument;
                    break;
                case INC:
                    increment(operation.argument);
                    break;
                case DEC:
                    decrement(operation.argument);
                    break;
                case PRINT:
                    putChar(operation.argument);
                    break;
                case READ:
                    readChar(operation.argument);
                    break;
                case JUMP_IF_ZERO:
                    if (read() == 0) jump(operation.argument);
                    break;
                case JUMP_IF_NOT_ZERO:
                    if (read() != 0) jump(operation.argument);
                    break;
            }

            instructionPointer++;
        }
    }

    int getDataPointer() {
        return dataPointer;
    }

    private void readChar(int times) throws IOException {
        for (int i = 0; i < times; i++) {
            int read = in.read();
            if (read != -1) {
                write((byte) read);
            }
        }
    }

    private void putChar(int times) {
        for (int i = 0; i < times; i++) {
            out.print((char) read());
        }
    }

    private void write(byte value) {
        memory[dataPointer] = value;
    }

    private byte read() {
        return memory[dataPointer];
    }

    private void increment(int by) {
        memory[dataPointer] += by;
    }

    private void decrement(int by) {
        memory[dataPointer] -= by;
    }

    private void jump(int to) {
        instructionPointer = to;
    }
}