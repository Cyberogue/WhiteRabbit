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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayDeque;

/**
 * Runnable which writes to a log file from a queue on a separate thread before
 * delaying for a short duration
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public final class LogWriter implements Runnable {

    public static final String DEFAULT_LOGFILE = "output.log";

    private final ArrayDeque<String> queue;

    private OutputStreamWriter writer;
    private TimeoutMode timeout;
    private boolean idle;

    /**
     *
     * @param timeout milliseconds to wait between iterations
     * @param queue reference to a queue to monitor
     */
    public LogWriter(TimeoutMode timeout, ArrayDeque<String> queue) {
        this(timeout, queue, DEFAULT_LOGFILE);
    }

    public LogWriter(TimeoutMode timeout, ArrayDeque<String> queue, String logpath) {
        this.timeout = timeout;
        this.queue = queue;
        this.idle = true;
        initLogWriter(logpath);
    }

    /**
     * Sets the interval between writer iterations
     *
     * @param mode Timeout mode instance
     */
    public void setTimeout(TimeoutMode mode) {
        this.timeout = mode;
    }

    public synchronized boolean initLogWriter(String logpath) {
        // Correct path separators
        String filepath = logpath.replace('\\', File.separatorChar).replace('/', File.separatorChar);

        try {
            // Create a new file
            File f = new File(filepath);
            if (f.isFile()) {
                f.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(f, true);
            writer = new OutputStreamWriter(fos);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Writes the contents of the queue to a file then flushes the file
     *
     * @return The number of items cleared
     */
    private synchronized int clearQueue() {
        int n = queue.size();

        try {
            while (!queue.isEmpty()) {
                writer.write(queue.pollLast() + '\n');
            }
            writer.flush();
        } catch (IOException e) {
        }

        return n;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Clear queue
                int n = clearQueue();

                // Timeout
                try {

                    synchronized (this) {
                        idle = true;
                        wait(timeout.getTimeout(n));
                        idle = false;
                    }
                } catch (Exception e) {
                    // Wake up and continue
                    idle = false;
                }

            }
        } finally {
            exit();
        }
    }

    /**
     * Returns true if the writer is idle or waiting
     *
     * @return true if the writer is idle or waiting
     */
    public boolean isIdle() {
        return idle;
    }

    /**
     * Force the writer to dump its queue and exit
     */
    public void exit() {
        // write everything that's left
        clearQueue();

        // Close output stream
        try {
            writer.close();
        } catch (IOException e) {
        }
    }
}
