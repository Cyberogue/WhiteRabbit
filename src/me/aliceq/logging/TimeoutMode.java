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

/**
 * Factory for the three different timeout modes
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public abstract class TimeoutMode {

    /**
     * Method which is called to determine how many milliseconds to wait
     *
     * @param n The number of queued up messages
     * @return How many milliseconds to wait based on n
     */
    public abstract int getTimeout(int n);

    /**
     * Implementation for a constant timeout
     *
     * @param millis Number of milliseconds to wait
     * @return
     */
    public static TimeoutMode constant(final int millis) {
        return new TimeoutMode() {
            @Override
            public int getTimeout(int n) {
                return millis;
            }
        };
    }

    /**
     * Implementation for a simple dynamic timeout.
     *
     * @param millisIdle Milliseconds to wait when there are no messages
     * @param millis Milliseconds to wait when there are messages
     * @return
     */
    public static TimeoutMode dynamic(final int millisIdle, final int millis) {
        return new TimeoutMode() {
            @Override
            public int getTimeout(int n) {
                return n > 0 ? millis : millisIdle;
            }
        };
    }

    /**
     * Implementation for a simple dynamic timeout.
     *
     * @param millisIdle Milliseconds to wait when message count <= threshold
     * @param threshold
     * @param millis Milliseconds to wait when message count > threshold
     * @return
     */
    public static TimeoutMode dynamic(final int millisIdle, final int millis, final int threshold) {
        return new TimeoutMode() {
            @Override
            public int getTimeout(int n) {
                return n > threshold ? millis : millisIdle;
            }
        };
    }

    /**
     * 2-stage dynamic implementation. When the number of messages exceeds
     * threshold1, millis1 is used. When the number of messages exceeds
     * threshold2, millis2 is used.
     *
     * @param millisIdle
     * @param millis1
     * @param threshold1
     * @param millis2
     * @param threshold2
     * @return
     */
    public static TimeoutMode dynamic2(final int millisIdle, final int millis1, final int threshold1, final int millis2, final int threshold2) {
        return new TimeoutMode() {
            @Override
            public int getTimeout(int n) {
                if (n <= threshold1) {
                    return millisIdle;
                } else if (n <= threshold2) {
                    return millis1;
                } else {
                    return millis2;
                }
            }
        };
    }

    /**
     * Implementation for an inverse linear timeout
     *
     * @param millisMin Minimum milliseconds to wait, reached when messages >=
     * threshold
     * @param millisMax Maximum milliseconds to wait, reached when messages = 0
     * @param threshold The point at which the minimum wait time is reached
     * @return
     */
    public static TimeoutMode linear(final int millisMin, final int millisMax, final int threshold) {
        return new TimeoutMode() {
            @Override
            public int getTimeout(int n) {
                if (n >= threshold) {
                    return millisMin;
                }

                float factor = 1 - n / threshold;
                return (int) (millisMin + factor * (millisMax - millisMin));
            }
        };
    }
}
