/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.math.random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import java.net.URL;

import org.apache.commons.math.stat.Univariate;
import org.apache.commons.math.stat.UnivariateImpl;
 
/**
 * Test cases for the ValueServer class.
 *
 * @version $Revision: 1.4 $ $Date: 2003/10/13 08:07:52 $
 */

public final class ValueServerTest extends TestCase {

    private ValueServer vs = new ValueServer();
    
    public ValueServerTest(String name) {
        super(name);
    }

    public void setUp() {
        vs.setMode(ValueServer.DIGEST_MODE);
        try {
            URL url = getClass().getResource("testData.txt");
            vs.setValuesFileURL(url.toExternalForm()); 
        } catch (Exception ex) {
            fail("malformed test URL");
        }
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ValueServerTest.class);
        suite.setName("ValueServer Tests");
        return suite;
    }

   
    /** 
      * Generate 1000 random values and make sure they look OK.<br>
      * Note that there is a non-zero (but very small) probability that
      * these tests will fail even if the code is working as designed.
      */
    public void testNextDigest() throws Exception{
        double next = 0.0;
        double tolerance = 0.1;
        vs.computeDistribution();
        assertTrue("empirical distribution property", 
            vs.getEmpiricalDistribution() != null);
        Univariate stats = new UnivariateImpl();
        for (int i = 1; i < 1000; i++) {
            next = vs.getNext();
            stats.addValue(next);
        }    
        assertEquals("mean", 5.069831575018909, stats.getMean(), tolerance);
        assertEquals
         ("std dev", 1.0173699343977738, stats.getStandardDeviation(), 
            tolerance);
        
        vs.computeDistribution(500);
        stats = new UnivariateImpl();
        for (int i = 1; i < 1000; i++) {
            next = vs.getNext();
            stats.addValue(next);
        }    
        assertEquals("mean", 5.069831575018909, stats.getMean(), tolerance);
        assertEquals
         ("std dev", 1.0173699343977738, stats.getStandardDeviation(), 
            tolerance);
        
    }
    
    /**
      * Make sure exception thrown if digest getNext is attempted
      * before loading empiricalDistribution.
      */
    public void testNextDigestFail() throws Exception {
        try {
            vs.getNext();
            fail("Expecting IllegalStateException");
        } catch (IllegalStateException ex) {;}
    }
    
    /**
      * Make sure exception thrown if nextReplay() is attempted
      * before opening replay file.
      */
    public void testNextReplayFail() throws Exception {
        try {
            vs.setMode(ValueServer.REPLAY_MODE);
            vs.getNext();
            fail("Expecting IllegalStateException");
        } catch (IllegalStateException ex) {;}
    }
    
    /**
     * Test ValueServer REPLAY_MODE using values in testData file.<br> 
     * Check that the values 1,2,1001,1002 match data file values 1 and 2.
     * the sample data file.
     */
    public void testReplay() throws Exception {
        double firstDataValue = 4.038625496201205;
        double secondDataValue = 3.6485326248346936;
        double tolerance = 10E-15;
        double compareValue = 0.0d;
        vs.setMode(ValueServer.REPLAY_MODE);
        vs.openReplayFile();
        compareValue = vs.getNext();
        assertEquals(compareValue,firstDataValue,tolerance);
        compareValue = vs.getNext();
        assertEquals(compareValue,secondDataValue,tolerance);
        for (int i = 3; i < 1001; i++) {
           compareValue = vs.getNext();
        }
        compareValue = vs.getNext();
        assertEquals(compareValue,firstDataValue,tolerance);
        compareValue = vs.getNext();
        assertEquals(compareValue,secondDataValue,tolerance);
        vs.closeReplayFile();
        // make sure no NPE
        vs.closeReplayFile();
    }
    
    /** 
     * Test other ValueServer modes
     */
    public void testModes() throws Exception {
        vs.setMode(ValueServer.CONSTANT_MODE);
        vs.setMu(0);
        assertEquals("constant mode test",vs.getMu(),vs.getNext(),Double.MIN_VALUE);
        vs.setMode(ValueServer.UNIFORM_MODE);
        vs.setMu(2);
        double val = vs.getNext();
        assertTrue(val > 0 && val < 4);
        vs.setSigma(1);
        vs.setMode(ValueServer.GAUSSIAN_MODE);
        val = vs.getNext();
        assertTrue("gaussian value close enough to mean",
            val < vs.getMu() + 100*vs.getSigma());
        vs.setMode(ValueServer.EXPONENTIAL_MODE);
        val = vs.getNext();
        assertTrue(val > 0);
        try {
            vs.setMode(1000);
            vs.getNext();
            fail("bad mode, expecting IllegalStateException");
        } catch (IllegalStateException ex) {
            ;
        }
    }
    
    /**
     * Test fill
     */
    public void testFill() throws Exception {
        vs.setMode(ValueServer.CONSTANT_MODE);
        vs.setMu(2);
        double[] val = new double[5];
        vs.fill(val);
        for (int i = 0; i < 5; i++) {
            assertEquals("fill test in place",2,val[i],Double.MIN_VALUE);
        }
        double v2[] = vs.fill(3);
        for (int i = 0; i < 3; i++) {
            assertEquals("fill test in place",2,v2[i],Double.MIN_VALUE);
        }
    }
    
    /**
     * Test getters to make Clover happy
     */
    public void testProperties() throws Exception {
        vs.setMode(ValueServer.CONSTANT_MODE);
        assertEquals("mode test",ValueServer.CONSTANT_MODE,vs.getMode());
        vs.setValuesFileURL("http://www.apache.org");
        String s = vs.getValuesFileURL();
        assertEquals("valuesFileURL test","http://www.apache.org",s);
    }
        
        
        
        
        
        
}
