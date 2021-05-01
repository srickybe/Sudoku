/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.util.Random;

/**
 *
 * @author ricky
 */
public class Rand {

    private static final Rand INSTANCE = new Rand();
    private java.util.Random random;
    private long seed;

    private Rand() {
        random = new Random();
        seed = -1;
    }

    public static Rand getInstance() {
        return INSTANCE;
    }
    
    public long getSeed() {
        return seed;
    }
    
    /*
    returns an integer with a minimum value equal to min 
    and maximum value equal to max-1
     */
    public int nextInt(int min, int max) {
        return min + random.nextInt(max - min);
    }

    public double nextDouble() {
        return random.nextDouble();
    }

    public int nextInt(int n) {
        return random.nextInt(n);
    }
    
    public void setSeed(long seed) {
        this.seed = seed;
        
        if (this.seed < 0) {
            random = new Random();
        } else {
            random.setSeed(this.seed);
        }
    }
}
