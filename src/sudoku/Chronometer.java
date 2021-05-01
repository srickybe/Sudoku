/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ricky
 */
public class Chronometer implements Runnable {

    private final long start;
    private double elapsed;
    private final long sleepTime;
    private boolean exit;

    public Chronometer(long sleepTime) {
        start = System.currentTimeMillis();
        elapsed = 0;
        this.sleepTime = sleepTime;
        exit = true;
    }

    //https://ejrh.wordpress.com/2012/07/13/sleeping-in-loops-considered-harmful/
    @Override
    public void run() {
        /*while (true) {
            elapsed = (double) (System.currentTimeMillis() - start) / 1000.0;
            System.out.println("elapsed time = " + elapsed);

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }*/

        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        Runnable command = () -> {
            elapsed = (double) (System.currentTimeMillis() - start) / 1000.0;
            System.err.println("elapsed time = " + elapsed);

            if (!exit) {
                service.shutdown();
            }
        };

        service.scheduleAtFixedRate(command, 0, sleepTime, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        exit = true;
    }

    public static void main(String[] args) {
        long sleepTime = 5000;
        Thread thread = new Thread(new Chronometer(sleepTime));
        thread.start();
    }
}
