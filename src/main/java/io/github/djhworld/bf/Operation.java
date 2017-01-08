package io.github.djhworld.bf;

public class Operation {
    public final Instruction instruction;
    public final int argument;

    public Operation(Instruction instruction, int argument) {
        this.instruction = instruction;
        this.argument = argument;
    }
}
