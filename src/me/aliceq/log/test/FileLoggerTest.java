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
package me.aliceq.log.test;

import java.util.Random;
import me.aliceq.log.*;

/**
 *
 * @author Alice Quiros <email@aliceq.me>
 */
public class FileLoggerTest {

    public static void main(String[] args) {
        // Initialize the Log
        Log.initialize();

        // Start a loop timer
        for (int i = 0; i < 20; i++) {
            Random r = new Random();
            try {
                // Write info, warning, error with delays in between
                Log.logInfo(r.nextInt(1000000));
                System.out.println("+queue");
                Thread.sleep(r.nextFloat() < .9 ? r.nextInt(750) : 4500);

                Log.logWarning(r.nextInt(1000000));
                System.out.println("+queue");
                Thread.sleep(r.nextFloat() < .9 ? r.nextInt(750) : 4500);

                Log.logError( r.nextInt(1000000));
                System.out.println("+queue");
                Thread.sleep(r.nextFloat() < .9 ? r.nextInt(750) : 4500);

            } catch (Exception e) {
                Log.logException(e);
            }
        }

        // Exit, this should cause the writer to self-terminate
    }
}
