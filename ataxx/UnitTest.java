/* Skeleton code copyright (C) 2008, 2022 Paul N. Hilfinger and the
 * Regents of the University of California.  Do not distribute this or any
 * derivative work without permission. */

package ataxx;

import org.junit.Test;
import ucb.junit.textui;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/** The suite of all JUnit tests for the ataxx package.
 *  @author P. N. Hilfinger
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(CommandTest.class, MoveTest.class,
                          BoardTest.class);
    }

    @Test
    public void workshop() {
        ArrayList<Character> test = new ArrayList<>();
        for (char c = 'a'; c <= 'g'; c += 1) {
            test.add(c);
        }
        assertEquals("a", test.get(0).toString());
    }
}



