/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

/**
 *
 * @author ricky
 */
public class FitnessFunction extends Fitness {
    private final Sudoku sudoku;

    public FitnessFunction(Sudoku sudoku) {
        this.sudoku = sudoku;
    }

    @Override
    public double evaluate(Chromosome chr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
