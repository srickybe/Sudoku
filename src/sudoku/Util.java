/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.util.ArrayList;

/**
 *
 * @author ricky
 */
public class Util {

    public static boolean isPerfectSquare(int number) {
        int sqrt = (int) Math.sqrt(number);

        return sqrt * sqrt == number;
    }
    
    public static <T> String printArray(T[] array) {
        String res = "";

        if (array != null) {
            if (array.length > 0) {
                res += "[" + array[0];

                for (int i = 1; i < array.length; ++i) {
                    res += ", " + array[i];
                }

                res += "]";

                return res;
            } else {
                return "[]";
            }
        } else {
            return null;
        }
    }

    public static <T> String printArray(T[][] array) {
        String res = "";

        if (array != null) {
            if (array.length > 0) {
                res += "[" + printArray(array[0]);

                for (int i = 1; i < array.length; ++i) {
                    res += ", " + printArray(array[i]);
                }

                res += "]";

                return res;
            } else {
                return "[]";
            }
        } else {
            return null;
        }
    }

    public static <T> String printArray(T[][][] array) {
        String res = "";

        if (array != null) {
            if (array.length > 0) {
                res += "[" + printArray(array[0]);

                for (int i = 1; i < array.length; ++i) {
                    res += ", " + printArray(array[i]);
                }

                res += "]";

                return res;
            } else {
                return "[]";
            }
        } else {
            return null;
        }
    }
    
    public static <T> void shuffle(ArrayList<T> array) {
        int size = array.size();

        if (size < 2) {
            return;
        }

        for (int i = 0; i < size; ++i) {
            int choice = Rand.getInstance().nextInt(size);

            while (choice == i) {
                choice = Rand.getInstance().nextInt(size);
            }

            T tmp = array.get(i);
            array.set(i, array.get(choice));
            array.set(choice, tmp);
        }
    }
    
    public static <T> void shuffle(T[] array) {
        int size = array.length;

        if (size < 2) {
            return;
        }

        for (int i = 0; i < size; ++i) {
            int choice = Rand.getInstance().nextInt(size);

            /*while (choice == i) {
                choice = Rand.getInstance().nextInt(size);
            }*/

            T tmp = array[i];
            array[i] = array[choice];
            array[choice] = tmp;
        }
    }
}
