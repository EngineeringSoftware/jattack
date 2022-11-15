package jattack.log;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LogTest {

    private static final String msg = "Message";
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private static Log.Level originalLevel;
    private static PrintStream originalOut;

    @BeforeClass
    public static void setUpClass() {
        originalOut = System.out;
        originalLevel = Log.getLevel();
        System.setOut(new PrintStream(outContent));
    }

    @Before
    public void setUp() {
        outContent.reset();
    }

    @AfterClass
    public static void tearDown() {
        Log.setLevel(originalLevel);
        System.setOut(originalOut);
    }

    @Test
    public void testLogDebugAtDebugLevel() {
        Log.setLevel(Log.Level.DEBUG);
        Log.debug(msg);
        assertEquals(Log.getFullLogMessage(msg, Log.Level.DEBUG),
                outContent.toString());
    }

    @Test
    public void testLogInfoAtDebugLevel() {
        Log.setLevel(Log.Level.DEBUG);
        Log.info(msg);
        assertEquals(Log.getFullLogMessage(msg, Log.Level.INFO),
                outContent.toString());
    }

    @Test
    public void testLogErrorAtDebugLevel() {
        Log.setLevel(Log.Level.DEBUG);
        Log.error(msg);
        assertEquals(Log.getFullLogMessage(msg, Log.Level.ERROR),
                outContent.toString());
    }

    @Test
    public void testLogDebugAtInfoLevel() {
        Log.setLevel(Log.Level.INFO);
        Log.debug(msg);
        assertEquals("", outContent.toString());
    }

    @Test
    public void testLogInfoAtInfoLevel() {
        Log.setLevel(Log.Level.INFO);
        Log.info(msg);
        assertEquals(Log.getFullLogMessage(msg, Log.Level.INFO),
                outContent.toString());
    }

    @Test
    public void testLogErrorAtInfoLevel() {
        Log.setLevel(Log.Level.INFO);
        Log.error(msg);
        assertEquals(Log.getFullLogMessage(msg, Log.Level.ERROR),
                outContent.toString());
    }

    @Test
    public void testLogDebugAtErrorLevel() {
        Log.setLevel(Log.Level.ERROR);
        Log.debug(msg);
        assertEquals("", outContent.toString());
    }

    @Test
    public void testLogInfoAtErrorLevel() {
        Log.setLevel(Log.Level.ERROR);
        Log.info(msg);
        assertEquals("", outContent.toString());
    }

    @Test
    public void testLogErrorAtErrorLevel() {
        Log.setLevel(Log.Level.ERROR);
        Log.error(msg);
        assertEquals(Log.getFullLogMessage(msg, Log.Level.ERROR),
                outContent.toString());
    }
}
