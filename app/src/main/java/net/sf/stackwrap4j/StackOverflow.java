/**
 * StackWrap4J - A Java wrapper for the Stack Exchange API.
 * 
 * Copyright (c) 2010 Bill Cruise and Justin Nelson.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.sf.stackwrap4j;

/**
 * Represents the Stack Overflow API access point.
 * 
 * @author Bill Cruise
 * @author Justin Nelson
 */
public class StackOverflow extends StackWrapper {
	
    /** The universal version identifier for a Serializable class. */
    private static final long serialVersionUID = 16284580977850694L;
    
	/**
     * Creates a StackWrapper object with the URL set to the Stack Overflow API access point.
     */
	/*public StackOverflow() {
        super("http://api.stackoverflow.com/");
    }*/
    public StackOverflow() {
        super("http://api.stackexchange.com/");
    }

	/**
     * Creates a StackWrapper object with the URL set to the Stack Overflow API access point.
     * This constructor accepts the client's API key.
     * @param apiKey the client program's API key.
     */
    /*public StackOverflow(final String apiKey) {
        super("http://api.stackoverflow.com/", apiKey);
    }*/
    public StackOverflow(final String apiKey) {
        super("http://api.stackexchange.com/", apiKey);
    }
}
