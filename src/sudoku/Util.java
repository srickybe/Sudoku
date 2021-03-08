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
public class Util {

    private static final Random RANDOM = new Random(0);

    public static int randomInt(int n) {
        return RANDOM.nextInt(n);
    }

    /*
    returns an integer with a minimum value equal to min 
    and maximum value equal to max-1
     */
    public static int randomInt(int min, int max) {
        return min + Util.randomInt(max - min);
    }

    public static boolean randomBoolean() {
        return RANDOM.nextBoolean();
    }

    public static boolean[] randomBooleans(int nBases) {
        boolean[] bases = new boolean[nBases];

        for (int i = 0; i < bases.length; ++i) {
            bases[i] = randomBoolean();
        }

        return bases;
    }

    public static Double randomDouble() {
        return RANDOM.nextDouble();
    }

    public static boolean mutateBase(boolean base) {
        return !base;
    }
}
