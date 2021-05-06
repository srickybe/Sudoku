/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author ricky
 */
public class Settings {
    private String gridPath;
    private String solverSettingsPath;
    private int numberOfThreads;
    private long seed;
    private int populationSize;
    private int criticalSizeOfReserve;
    private int maximumNumberOfGenerations;
    private int restartThreshold;
    private double crossOverRate;
    private double mutationRate;
    private double selectionRate;
    private ArrayList<String> selection_type;
    private ArrayList<Selector> selections;

    public Settings(String pathName) throws FileNotFoundException {
        this.solverSettingsPath = pathName;
        File file = new File(solverSettingsPath);
        Scanner scanner = new Scanner(file);
        String tmp = scanner.next();
        
        if (!tmp.equals("number_of_threads")) {
            throw new IllegalArgumentException();
        }
        
        numberOfThreads = scanner.nextInt();
        tmp = scanner.next();
        
        if (!tmp.equals("random_seed")) {
            throw new IllegalArgumentException();
        }
        
        this.seed = scanner.nextLong();
        Rand.getInstance().setSeed(this.seed);
        tmp = scanner.next();
        
        if (!tmp.equals("path_of_sudoku_grid")) {
            throw new IllegalArgumentException();
        }
        
        this.gridPath = scanner.next();
        tmp = scanner.next();
        
        if (!tmp.equals("population_size")) {
            throw new IllegalArgumentException();
        }
        
        populationSize = scanner.nextInt();
        tmp = scanner.next();
        
        if (!tmp.equals("critical_size_of_reserve")) {
            throw new IllegalArgumentException();
        }
        
        criticalSizeOfReserve = scanner.nextInt();
        tmp = scanner.next();
        
        if (!tmp.equals("maximum_number_of_generations")) {
            throw new IllegalArgumentException();
        }
        
        maximumNumberOfGenerations = scanner.nextInt();
        tmp = scanner.next();
        
        if (!tmp.equals("restart_threshold")) {
            throw new IllegalArgumentException();
        }
        
        restartThreshold = scanner.nextInt();
        tmp = scanner.next();
        
        if (!tmp.equals("cross_over_rate")) {
            throw new IllegalArgumentException();
        }
        
        try {
            crossOverRate = scanner.nextDouble();
        } catch (Exception e) {
            System.out.println("EXCEPTION !!!" 
                    + "\tException class = " + e.getClass());
            System.exit(0);
        }
        
        tmp = scanner.next();
        
        if (!tmp.equals("mutation_rate")) {
            throw new IllegalArgumentException();
        }
        
        mutationRate = scanner.nextDouble();
        tmp = scanner.next();
        
        if (!tmp.equals("selection_rate")) {
            throw new IllegalArgumentException();
        }
        
        selectionRate = scanner.nextDouble();
        this.selection_type = new ArrayList<>();
        this.selections = new ArrayList<>();
        
        for (int i = 0; i < numberOfThreads; ++i) {
            tmp = scanner.next();
            
            if (!tmp.equals("chromosome_selection")) {
                throw new IllegalArgumentException();
            }
            
            tmp = scanner.next();
            this.selection_type.add(tmp);

            switch(tmp) {
                case "RouletteWheel":
                    selections.add(new RouletteWheel());
                    break;
                case "Tournament":
                    selections.add(new Tournament(scanner.nextInt()));
                    //System.out.println("tournament_size = " + ((Tournament)selection).getSize());
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public long getSeed() {
        return seed;
    }

    public String getGridPath() {
        return gridPath;
    }

    public String getSolverSettingsPath() {
        return solverSettingsPath;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public int getMaximumNumberOfGenerations() {
        return maximumNumberOfGenerations;
    }

    public int getRestartThreshold() {
        return restartThreshold;
    }

    public double getCrossOverRate() {
        return crossOverRate;
    }

    public double getMutationRate() {
        return mutationRate;
    }

    public double getSelectionRate() {
        return selectionRate;
    }
    
    public int numberOfSelectors() {
        return selections.size();
    }
    
    public Selector getSelector(int i) {
        return this.selections.get(i);
    } 

    public int getCriticalSizeOfReserve() {
        return criticalSizeOfReserve;
    }

    @Override
    public String toString() {
        return "Settings{" 
                + "gridPath=" + gridPath 
                + ",\nsolverSettingsPath=" + solverSettingsPath 
                + ",\npopulationSize=" + populationSize 
                + ",\nmaximumNumberOfGenerations=" + maximumNumberOfGenerations 
                + ",\ncrossOverRate=" + crossOverRate 
                + ",\nmutationRate=" + mutationRate 
                + ",\nselectionRate=" + selectionRate 
                + ",\nselection=" + selections 
                + '}';
    }
    
    public static void main(String args[]) throws FileNotFoundException {
        String pathName = "/home/ricky/NetBeansProjects/Sudoku2/src/sdk/settings.txt";
        Settings settings = new Settings(pathName);
        System.out.println("settings =\n" + settings);
    }
}