package tests;

import main.Token;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Modified by Julie on 16.04.2017.
 */
public class EnumTest {
    @Test
    void testTokenMatch()  {
        assertTrue(Token.BLUE.matches(Token.BLUE));
        assertFalse(Token.BLUE.matches(Token.RED));
        assertTrue(Token.SKULL_5.matches(Token.SKULL));
        assertFalse(Token.STAR.matches(Token.GREEN));
        assertTrue(Token.BLUE.matches(Token.WILDCARD));
        assertTrue(Token.WILDCARD.matches(Token.WILDCARD));
    }

}
