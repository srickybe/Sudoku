/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.util.ArrayList;

/**
 *
 * @author Ricky
 */
public class Container {

    private final ArrayList<Integer> elements;

    public Container() {
        elements = new ArrayList<>();
    }

    public boolean add(Integer element) {
        if (element != 0) {
            if (!contains(element)) {
                elements.add(element);

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean remove(Integer element) {
        return elements.remove(element);
    }
    
    public boolean contains(Integer element) {
        return elements.contains(element);
    }
}
