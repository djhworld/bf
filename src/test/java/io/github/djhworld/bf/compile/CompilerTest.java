package io.github.djhworld.bf.compile;

import io.github.djhworld.bf.Operation;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static io.github.djhworld.bf.Instruction.*;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;

public class CompilerTest {
    private Compiler compiler;

    @Before
    public void setUp() throws Exception {
        compiler = new Compiler();
    }

    @Test
    public void shouldCompileProgram() throws Exception {
        String program = "<>+-.,++++[..,,]>>----<<";

        List<Operation> compiled = compiler.compile(program);
        assertThat(compiled, contains(
                operationMatcher(new Operation(DEC_DP, 1)),
                operationMatcher(new Operation(INC_DP, 1)),
                operationMatcher(new Operation(INC, 1)),
                operationMatcher(new Operation(DEC, 1)),
                operationMatcher(new Operation(PRINT, 1)),
                operationMatcher(new Operation(READ, 1)),
                operationMatcher(new Operation(INC, 4)),
                operationMatcher(new Operation(JUMP_IF_ZERO, 10)),
                operationMatcher(new Operation(PRINT, 2)),
                operationMatcher(new Operation(READ, 2)),
                operationMatcher(new Operation(JUMP_IF_NOT_ZERO, 7)),
                operationMatcher(new Operation(INC_DP, 2)),
                operationMatcher(new Operation(DEC, 4)),
                operationMatcher(new Operation(DEC_DP, 2))
        ));
    }

    @Test
    public void shouldCompileOneInstructionProgram() throws Exception {
        String program = "+";

        List<Operation> compiled = compiler.compile(program);
        assertThat(compiled, contains(
                operationMatcher(new Operation(INC, 1))
        ));
    }


    @Test
    public void shouldFoldAllTokens() throws Exception {
        String program = "----";

        List<Operation> compiled = compiler.compile(program);
        assertThat(compiled, contains(
                operationMatcher(new Operation(DEC, 4))
        ));
    }

    @Test
    public void shouldCompileProgramWithNestedLoops() throws Exception {
        String program = "[[[[[.]]]]]";

        List<Operation> compiled = compiler.compile(program);
        assertThat(compiled, contains(
                operationMatcher(new Operation(JUMP_IF_ZERO, 10)),
                operationMatcher(new Operation(JUMP_IF_ZERO, 9)),
                operationMatcher(new Operation(JUMP_IF_ZERO, 8)),
                operationMatcher(new Operation(JUMP_IF_ZERO, 7)),
                operationMatcher(new Operation(JUMP_IF_ZERO, 6)),
                operationMatcher(new Operation(PRINT, 1)),
                operationMatcher(new Operation(JUMP_IF_NOT_ZERO, 4)),
                operationMatcher(new Operation(JUMP_IF_NOT_ZERO, 3)),
                operationMatcher(new Operation(JUMP_IF_NOT_ZERO, 2)),
                operationMatcher(new Operation(JUMP_IF_NOT_ZERO, 1)),
                operationMatcher(new Operation(JUMP_IF_NOT_ZERO, 0))
        ));
    }


    @Test
    public void shouldCompileProgramWithUnknownTokens() throws Exception {
        String program = "this is a test\n++++---\nto see if it extracts the code >>>\n.";

        List<Operation> compiled = compiler.compile(program);
        assertThat(compiled, contains(
                operationMatcher(new Operation(INC, 4)),
                operationMatcher(new Operation(DEC, 3)),
                operationMatcher(new Operation(INC_DP, 3)),
                operationMatcher(new Operation(PRINT, 1))
        ));
    }

    @Test(expected = CompileException.class)
    public void shouldRaiseErrorOnEmptyProgram() throws Exception {
        compiler.compile("");
    }

    @Test(expected = CompileException.class)
    public void shouldRaiseErrorOnNullProgram() throws Exception {
        compiler.compile(null);
    }

    @Test(expected = CompileException.class)
    public void shouldRaiseErrorWhenNoEndLoopFound() throws Exception {
        compiler.compile("[----------");
    }

    @Test(expected = CompileException.class)
    public void shouldRaiseErrorWhenNoStartLoopFound() throws Exception {
        compiler.compile("----------]");
    }

    private Matcher<Operation> operationMatcher(final Operation expected) {
        return new TypeSafeMatcher<Operation>() {
            @Override
            protected boolean matchesSafely(Operation actual) {
                return expected.argument == actual.argument
                        && expected.instruction == actual.instruction;
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}