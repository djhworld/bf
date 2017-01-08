package io.github.djhworld.bf;

public enum Instruction {
    INC('+'),
    DEC('-'),
    INC_DP('>'),
    DEC_DP('<'),
    PRINT('.'),
    READ(','),
    JUMP_IF_ZERO('['),
    JUMP_IF_NOT_ZERO(']');

    public final char token;

    Instruction(char token) {
        this.token = token;
    }
}
