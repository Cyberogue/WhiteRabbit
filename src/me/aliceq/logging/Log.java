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

import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Date;

/**
 * Main logging class which functions as a message queue
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class Log {

    public static final int DEFAULT_TIMEOUT = 1000;

    public static String INFO_TAG = "[INFO]";
    public static String WARN_TAG = "[WARN]";
    public static String ERROR_TAG = "[ERR0]";
    public static String EXCEPTION_TAG = "[EXEP]";

    private static Log singleton = null;

    private final LogWriter writer;
    private final Thread wrThread;
    private final ArrayDeque<String> queue;

    /**
     *
     * @return a Log instance or null if one couldn't be created
     */
    public static Log getInstance() {
        if (singleton == null) {
            singleton = new Log();
            singleton.wrThread.start();
        }
        return singleton;
    }

    /**
     * Initializes the logging systems. This is normally done automatically but
     * if possible should be initialized during setup for better control.
     *
     * Note that this makes the default logfile "output.log"
     *
     * @return the created log instance or null if one already exists
     */
    public static Log initialize() {
        return initialize(TimeoutMode.dynamic(DEFAULT_TIMEOUT * 5, DEFAULT_TIMEOUT), LogWriter.DEFAULT_LOGFILE);

    }

    /**
     * Initializes the logging systems. This is normally done automatically but
     * if possible should be initialized during setup for better control.
     *
     * @param logpath the path of the logfile to write
     * @return the created log instance or null if one already exists
     */
    public static Log initialize(String logpath) {
        return initialize(TimeoutMode.dynamic(DEFAULT_TIMEOUT * 5, DEFAULT_TIMEOUT), logpath);
    }

    /**
     * Initializes the logging systems. This is normally done automatically but
     * if possible should be initialized during setup for better control.
     *
     * @param mode the timeout mode for the writer thread
     * @param logpath the path of the logfile to write
     * @return the created log instance or null if one already exists
     */
    public static Log initialize(TimeoutMode mode, String logpath) {
        if (singleton == null) {
            singleton = new Log(mode, logpath);
            singleton.wrThread.start();
            return singleton;
        }
        return null;
    }

    /**
     * Sets the interval between writer iterations
     *
     * @param mode Timeout mode instance
     */
    public static void setTimeout(TimeoutMode mode) {
        singleton.writer.setTimeout(mode);
    }

    /**
     * Pushes a message into the queue
     *
     * @param message
     * @param tag
     */
    public synchronized static void log(String tag, Object message) {
        singleton.queue.push(new Date().toString() + " | " + tag + " | " + message.toString());
    }

    /**
     * Pushes a message into the queue
     *
     * @param info
     */
    public synchronized static void logInfo(Object info) {
        singleton.queue.push(new Date().toString() + " | " + INFO_TAG + " | " + info.toString());
    }

    /**
     * Pushes a warning into the queue
     *
     * @param warning
     */
    public synchronized static void logWarning(Object warning) {
        singleton.queue.push(new Date().toString() + " | " + WARN_TAG + " | " + warning.toString());
    }

    /**
     * Pushes an error into the queue
     *
     * @param error
     */
    public synchronized static void logError(Object error) {
        singleton.queue.push(new Date().toString() + " | " + ERROR_TAG + " | " + error.toString());
    }

    /**
     * Pushes an exception into the queue
     *
     * @param e
     */
    public synchronized static void logException(Exception e) {
        singleton.queue.push(new Date().toString() + " | " + EXCEPTION_TAG + " | " + e);
    }

    /**
     * Forces the writer to flush if it is waiting
     */
    public static void flush() {
        {
            synchronized (singleton.writer) {
                if (singleton.writer.isIdle()) {
                    singleton.writer.notify();
                }
            }
        }
    }

    /**
     * Marks the logging framework for exit. Warning, if this message is not
     * called the most recent messages may not get logged.
     */
    public synchronized static void quit() {
        singleton.writer.exit();
    }

    /**
     * Creates an interceptor to intercept and log System.out messages
     *
     * @param passthrough when true, messages get passed through to System.out
     * as normal
     */
    public static void interceptSystemOut(boolean passthrough) {
        PrintStream inter = new LogIntercept(System.out, passthrough);
        System.setOut(inter);
    }

    /**
     * Constructor
     *
     * Note; default timeout mode for writer is Dynamic
     */
    private Log() {
        this(TimeoutMode.dynamic(DEFAULT_TIMEOUT * 5, DEFAULT_TIMEOUT), LogWriter.DEFAULT_LOGFILE);
    }

    private Log(TimeoutMode mode, String logpath) {
        queue = new ArrayDeque();
        writer = new LogWriter(mode, queue, logpath);
        wrThread = new Thread(writer);
        wrThread.setDaemon(true);
    }
}
