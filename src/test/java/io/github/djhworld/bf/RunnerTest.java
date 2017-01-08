package io.github.djhworld.bf;

import io.github.djhworld.bf.compile.CompileException;
import io.github.djhworld.bf.compile.Compiler;
import io.github.djhworld.bf.vm.Machine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RunnerTest {
    @Mock
    private Compiler mockedCompiler;

    @Mock
    private Machine mockedMachine;

    private Runner runner;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        this.runner = new Runner(mockedCompiler, mockedMachine);
    }

    @Test
    public void shouldCompileAndExecuteProgram() throws Exception {
        String program = "...";
        List<Operation> compiledProgram = new ArrayList<>();

        when(mockedCompiler.compile(anyString())).thenReturn(compiledProgram);
        runner.run(program);

        InOrder inOrder = inOrder(mockedCompiler, mockedMachine);
        inOrder.verify(mockedCompiler, times(1)).compile(eq(program));
        inOrder.verify(mockedMachine, times(1)).execute(eq(compiledProgram));
        verifyNoMoreInteractions(mockedCompiler, mockedMachine);
    }

    @Test(expected = CompileException.class)
    public void shouldPropagateErrorOnCompilationFailure() throws Exception {
        when(mockedCompiler.compile(anyString())).thenThrow(new CompileException("simulated"));
        runner.run("...");
    }

    @Test(expected = RuntimeException.class)
    public void shouldPropagateErrorOnRuntimeFailure() throws Exception {
        doThrow(new RuntimeException("simulated")).when(mockedMachine).execute(any(List.class));
        runner.run("...");
    }
}