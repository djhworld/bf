package io.github.djhworld.bf;

import io.github.djhworld.bf.compile.CompileException;
import io.github.djhworld.bf.compile.Compiler;
import io.github.djhworld.bf.vm.Machine;

import java.io.IOException;

public class Runner {
    private final Compiler compiler;
    private final Machine machine;

    public Runner(Compiler compiler, Machine machine) {
        this.compiler = compiler;
        this.machine = machine;
    }


    public void run(String program) throws CompileException, IOException {
        machine.execute(compiler.compile(program));
    }
}
