/*
 * Copyright 2005 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.lang.text;

import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.lang.ArrayUtils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * Unit tests for {@link org.apache.commons.lang.text.StrBuilder}.
 * 
 * @author Michael Heuer
 * @version $Id$
 */
public class StrBuilderTest extends TestCase {

    /** Test subclass of Object, with a toString method. */
    private static Object FOO = new Object() {
        public String toString() {
            return "foo";
        }
    };

    /**
     * Main method.
     * 
     * @param args  command line arguments, ignored
     */
    public static void main(String[] args) {
        TestRunner.run(suite());
    }

    /**
     * Return a new test suite containing this test case.
     * 
     * @return a new test suite containing this test case
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(StrBuilderTest.class);
        suite.setName("StrBuilder Tests");
        return suite;
    }

    /**
     * Create a new test case with the specified name.
     * 
     * @param name
     *            name
     */
    public StrBuilderTest(String name) {
        super(name);
    }

    public void testIndexOfString_10() {
        StrBuilder sb = new StrBuilder("abab");
        assertEquals(-1, sb.indexOf((String) null));
    }
    
    public void testIndexOfStringInt_12() {
        StrBuilder sb = new StrBuilder("xyzabc");
        assertEquals(-1, sb.indexOf((String) null, 2));
    }

}
