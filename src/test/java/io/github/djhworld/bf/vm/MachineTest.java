package io.github.djhworld.bf.vm;

import com.google.common.io.Resources;
import io.github.djhworld.bf.Operation;
import io.github.djhworld.bf.compile.CompileException;
import io.github.djhworld.bf.compile.Compiler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.io.Resources.getResource;
import static io.github.djhworld.bf.Instruction.*;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class MachineTest {
    @Mock
    private InputStream mockedInputStream;

    @Mock
    private PrintStream mockedPrintStream;

    private Machine machine;
    private byte[] machineMemory;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        this.machineMemory = new byte[30000];
        this.machine = new Machine(mockedInputStream, mockedPrintStream, machineMemory);
    }

    @Test
    public void shouldIncrementDataPointer() throws Exception {
        machine.execute(newArrayList(
                new Operation(INC_DP, 2)
        ));

        assertThat(machine.getDataPointer(), is(2));
    }

    @Test
    public void shouldDecrementDataPointer() throws Exception {
        machine.execute(newArrayList(
                new Operation(INC_DP, 10),
                new Operation(DEC_DP, 2)
        ));

        assertThat(machine.getDataPointer(), is(8));
    }

    @Test
    public void shouldIncrementCellAtDataPointer() throws Exception {
        machine.execute(newArrayList(
                new Operation(INC_DP, 10),
                new Operation(INC, 49)
        ));

        assertThat(machineMemory[10], is((byte) 49));
    }

    @Test
    public void shouldDecrementCellAtDataPointer() throws Exception {
        machine.execute(newArrayList(
                new Operation(INC_DP, 5),
                new Operation(INC, 49),
                new Operation(DEC, 4)
        ));

        assertThat(machineMemory[5], is((byte) 45));
    }

    @Test
    public void shouldPrintToOutputStream() throws Exception {
        machine.execute(newArrayList(
                new Operation(INC, 104),
                new Operation(PRINT, 2)
        ));

        verify(mockedPrintStream, times(2)).print(eq('h'));
        verifyNoMoreInteractions(mockedPrintStream);
    }

    @Test
    public void shouldReadFromInputStreamAndWriteToMemory() throws Exception {
        when(mockedInputStream.read())
                .thenReturn(105)
                .thenReturn(106);

        machine.execute(newArrayList(
                new Operation(INC_DP, 100),
                new Operation(READ, 2)
        ));

        assertEquals((byte) 106, machineMemory[100]);

        verify(mockedInputStream, times(2)).read();
        verifyNoMoreInteractions(mockedInputStream);
    }

    @Test
    public void shouldNotWriteToMemoryWhenInputStreamReturnsMinusOne() throws Exception {
        when(mockedInputStream.read())
                .thenReturn(-1);

        machine.execute(newArrayList(
                new Operation(READ, 1)
        ));

        assertEquals((byte) 0, machineMemory[0]);

        verify(mockedInputStream, times(1)).read();
        verifyNoMoreInteractions(mockedInputStream);
    }


    @Test
    public void shouldLoop() throws Exception {
        // [0] = loop counter, [1] = value to increment

        int loopCounter = 5;
        int value = 10;

        machine.execute(newArrayList(
                new Operation(INC, loopCounter),
                new Operation(INC_DP, 1),
                new Operation(INC, value),
                new Operation(DEC_DP, 1),
                new Operation(JUMP_IF_ZERO, 9),
                new Operation(INC_DP, 1),
                new Operation(INC, 1),
                new Operation(DEC_DP, 1),
                new Operation(DEC, 1),
                new Operation(JUMP_IF_NOT_ZERO, 4)
        ));

        assertEquals((byte) 0, machineMemory[0]);
        assertEquals((byte) 15, machineMemory[1]);
    }


    @Test
    public void shouldExecuteHelloWorld() throws Exception {
        runProgram("helloworld.b");

        InOrder inOrder = inOrder(mockedPrintStream);
        inOrder.verify(mockedPrintStream, times(1)).print(eq('H'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('e'));
        inOrder.verify(mockedPrintStream, times(2)).print(eq('l'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('o'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq(' '));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('W'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('o'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('r'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('l'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('d'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('!'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('\n'));
        verifyNoMoreInteractions(mockedPrintStream);
        verifyZeroInteractions(mockedInputStream);
    }

    @Test
    public void shouldExecuteIOTest() throws Exception {
        when(mockedInputStream.read())
                .thenReturn(10) // LF
                .thenReturn(-1); // EOF (i.e. Ctrl+D)

        runProgram("io-test-1.b");
        InOrder inOrder = inOrder(mockedPrintStream, mockedInputStream);
        inOrder.verify(mockedInputStream, times(2)).read();
        inOrder.verify(mockedPrintStream, times(1)).print(eq('L'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('K'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('\n'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('L'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('K'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('\n'));
        verifyNoMoreInteractions(mockedInputStream, mockedPrintStream);
    }

    @Test
    public void shouldExecuteMemorySizeCheck() throws Exception {
        runProgram("memory-size-check.b");
        InOrder inOrder = inOrder(mockedPrintStream);
        inOrder.verify(mockedPrintStream, times(1)).print(eq('#'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('\n'));
        verifyNoMoreInteractions(mockedPrintStream);
        verifyZeroInteractions(mockedInputStream);
    }

    @Test
    public void shouldExecuteObscureTest() throws Exception {
        runProgram("test-obscure.b");
        InOrder inOrder = inOrder(mockedPrintStream);
        inOrder.verify(mockedPrintStream, times(1)).print(eq('H'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('\n'));
        verifyNoMoreInteractions(mockedPrintStream);
        verifyZeroInteractions(mockedInputStream);
    }

    @Test
    public void shouldExecuteRot13() throws Exception {
        when(mockedInputStream.read())
                .thenReturn((int) '~')
                .thenReturn((int) 'm')
                .thenReturn((int) 'l')
                .thenReturn((int) 'k')
                .thenReturn((int) ' ')
                .thenReturn((int) 'z')
                .thenReturn((int) 'y')
                .thenReturn((int) 'x')
                .thenReturn(-1);


        runProgram("rot13.b");
        verify(mockedInputStream, times(9)).read();
        InOrder inOrder = inOrder(mockedPrintStream, mockedInputStream);
        inOrder.verify(mockedPrintStream, times(1)).print(eq('~'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('z'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('y'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('x'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq(' '));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('m'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('l'));
        inOrder.verify(mockedPrintStream, times(1)).print(eq('k'));
        verifyNoMoreInteractions(mockedPrintStream, mockedInputStream);
    }


    private void runProgram(String filename) throws IOException, CompileException {
        Compiler compiler = new Compiler();
        String helloWorldStr = Resources.toString(getResource(filename), UTF_8);

        this.machine.execute(compiler.compile(helloWorldStr));
    }
}