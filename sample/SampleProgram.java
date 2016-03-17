package me.aliceq.irc.test;

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

import me.aliceq.logging.Log;

/**
 * Sample of the logging framework
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class SampleProgram {

    public static void main(String[] args) {
        // The logging framework must be initialized by calling the initialize method
        Log.initialize("error.log");

        // Once the framework is initialized, messages are sent using the log- methods
        Log.logInfo("This is an info message.");
        Log.logWarning("This is a warning.");
        Log.logError("This is an error.");
        Log.logException(new RuntimeException("This is an exception."));

        // The framework adds a tag and date-time to each message. Custom tags are added through the log method
        Log.log("[TAG]", "This is a custom tag.");
        
        // A blank line method is included
        Log.blankLine();

        // The logging framework can intercept System.out by calling the interceptSystemOut method
        Log.interceptSystemOut(true);

        // System.out messages are now logged into the log file with the [PROG] tag
        System.out.println("This is a System.out message");
        System.out.println("This should display on console output");

        // When done, simply all Log.quit(). This forces the framework to do one 
        // final pass so if this isn't called the most recent messages may not 
        // get logged.
        Log.quit();

        /* ADVANCED */
        //
        // The logging framework functions by combining a message queue with a
        // second thread that occasionally clears it. This is done to reduce
        // system usage and file accesses. 
        //
        // The duration the logging thread waits  for is dependent on how many 
        // messages it picked up on the previous pass. At the default it scans
        // once every second, idling for five seconds at a time when the queue
        // is clear. You may change this timeout by calling Log.setTimeout
        // with a custom TimeoutMode. By default, TimeoutMode includes a few
        // predefined options.
    }
}
