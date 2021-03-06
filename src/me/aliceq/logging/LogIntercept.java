/*
 * The MIT License
 *
 * Copyright 2016 Alice Quiros <email@aliceq.me>.
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
package me.aliceq.logging;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * PrintStream class which intercepts another PrintStream and sends messages to
 * the logging framework before passing them as normal.
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public final class LogIntercept extends PrintStream {

    private String tag = "[LOGX]";
    private boolean passthrough = true;

    /**
     * Constructor
     *
     * @param out The OutputStream to intercept
     * @param passthrough when true, messages get passed to the intercepted
     * OutputStream
     */
    public LogIntercept(OutputStream out, boolean passthrough) {
        super(out);
        this.passthrough = passthrough;
    }

    /**
     * Constructor
     *
     * @param out the OutputStream to intercept
     * @param autoFlush flush OutputStream automatically
     * @param passthrough when true, messages get passed to the intercepted
     * OutputStream
     */
    public LogIntercept(OutputStream out, boolean autoFlush, boolean passthrough) {
        super(out, autoFlush);
        this.passthrough = passthrough;
    }

    /**
     * Sets the tag to affix to messages
     *
     * @param tag the tag to affix to messages
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public void print(String s) {
        Log.log(tag, s);
        if (passthrough) {
            super.print(s);
        }
    }
}
