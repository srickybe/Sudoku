/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

/**
 *
 * @author ricky
 */
public class Sudoku implements Chromosome {

    private Byte[][] grid;
    private Boolean[][] given;
    private Boolean[][][] possible;
    private Byte[][][] indexes;
    private Byte[][] missing;
    private Byte length;
    private Byte blockSize;
    private double fitness;

    public Sudoku() {

    }

    public Sudoku(byte length) {
        this.length = length;

        if (!Util.isPerfectSquare(length)) {
            throw new UnsupportedOperationException();
        }

        blockSize = (byte) Math.sqrt(length);
        grid = emptyGrid(this.length);
        given = emptyFixed(this.length);
        possible = emptyPossible(length);
        indexes = convertAllIndexes(length);
        missing = missingValuesInBlocks();
        fitness = 0.0;
    }

    public Sudoku(String pathName) throws FileNotFoundException {
        File file = new File(pathName);
        Scanner sc = new Scanner(file);

        this.length = sc.nextByte();

        if (!Util.isPerfectSquare(length)) {
            throw new IllegalArgumentException();
        }

        if (this.length != sc.nextByte()) {
            throw new IllegalArgumentException();
        }

        blockSize = (byte) Math.sqrt(length);
        grid = emptyGrid(length);
        indexes = convertAllIndexes(length);
        given = emptyFixed(length);
        possible = emptyPossible(length);

        for (byte i = 0; i < length; ++i) {
            for (byte j = 0; j < length; ++j) {
                String tmp = sc.next();

                if ("-".equals(tmp) || "--".equals(tmp)) {
                    setValueAndGiven(i, j, null, false);
                    updatePossible(i, j);
                } else {
                    byte value = Byte.parseByte(tmp);

                    if (value > 0 || value <= length) {
                        setValueAndGiven(i, j, --value, true);
                        updatePossible(i, j);
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
            }
        }

        removePossible();
        simplifyGrid();
        missing = missingValuesInBlocks();
        computeFitness();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Arrays.deepHashCode(this.grid);
        
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final Sudoku other = (Sudoku) obj;
        
        if (!Arrays.deepEquals(this.grid, other.grid)) {
            return false;
        }
        
        return true;
    }

    public final Byte[][] emptyGrid(byte length) {
        Byte[][] res = new Byte[length][];

        for (int i = 0; i < length; ++i) {
            res[i] = new Byte[length];

            for (int j = 0; j < length; ++j) {
                res[i][j] = 0;
            }
        }

        return res;
    }

    public final Boolean[][] emptyFixed(byte length) {
        Boolean[][] res = new Boolean[length][];

        for (byte i = 0; i < length; ++i) {
            res[i] = new Boolean[length];

            for (byte j = 0; j < length; ++j) {
                res[i][j] = true;
            }
        }

        return res;
    }

    public final Boolean[][][] emptyPossible(byte length) {
        Boolean[][][] res = new Boolean[length][][];

        for (byte i = 0; i < length; ++i) {
            res[i] = new Boolean[length][];

            for (byte j = 0; j < length; ++j) {
                res[i][j] = new Boolean[length];

                for (byte k = 0; k < length; ++k) {
                    res[i][j][k] = true;
                }
            }
        }

        return res;
    }

    public final Byte[][][] convertAllIndexes(Byte length) {
        if (!Util.isPerfectSquare(length)) {
            throw new IllegalArgumentException();
        }

        Byte[][][] res = new Byte[length][][];

        for (byte i = 0; i < length; ++i) {
            res[i] = new Byte[length][];

            for (byte j = 0; j < length; ++j) {
                res[i][j] = new Byte[2];

                res[i][j][0] = (byte) ((i / blockSize) * blockSize + j / blockSize);
                res[i][j][1] = (byte) ((i % blockSize) * blockSize + j % blockSize);
            }
        }

        return res;
    }

    public Byte getValue(byte i, byte j) {
        int block = block(i, j);
        int pos = pos(i, j);
        return grid[block][pos];
    }

    public final void setValueAndGiven(byte i, byte j, Byte value, boolean isGiven) {
        if (value == null && isGiven) {
            throw new IllegalArgumentException();
        }

        grid[block(i, j)][pos(i, j)] = value;
        given[block(i, j)][pos(i, j)] = isGiven;
    }

    public byte block(byte i, byte j) {
        return indexes[i][j][0];
    }

    public byte pos(byte i, byte j) {
        return indexes[i][j][1];
    }

    public byte getLength() {
        return length;
    }

    public byte getBlockSize() {
        return (byte) (Math.sqrt(getLength()));
    }

    public Boolean[] getPossible(byte i, byte j) {
        return possible[block(i, j)][pos(i, j)];
    }

    public final void updatePossible(byte i, byte j) {
        if (isEmpty(i, j)) {
            byte row = block(i, j);
            byte col = pos(i, j);
            //possible[row][col][0] = false;

            for (byte k = 0; k < length; ++k) {
                possible[row][col][k] = true;
            }
        } else {
            Byte value = getValue(i, j);

            if (value >= 0 && value < length) {
                byte row = block(i, j);
                byte col = pos(i, j);

                for (byte k = 0; k < length; ++k) {
                    possible[row][col][k] = false;
                }
            } else {
                throw new IllegalArgumentException("value == " + value);
            }
        }
    }

    public boolean isPossible(byte i, byte j, byte poss) {
        return possible[block(i, j)][pos(i, j)][poss];
    }

    public boolean isGiven(byte i, byte j) {
        int block = block(i, j);
        int pos = pos(i, j);
        return given[block][pos];
    }

    public void setGiven(byte i, byte j, Boolean b) {
        byte row = block(i, j);
        byte col = pos(i, j);

        given[row][col] = b;
    }

    public boolean isEmpty(byte i, byte j) {
        return getValue(i, j) == null;
    }

    public final void removePossible() {
        possible = emptyPossible(length);

        for (byte i = 0; i < length; ++i) {
            for (byte j = 0; j < length; ++j) {
                if (!isEmpty(i, j)) {
                    Byte val = getValue(i, j);
                    removeFromRowPoss(i, val);
                    removeFromColumnPoss(j, val);
                }

                Byte val2 = getBlock(i)[j];

                if (getBlock(i)[j] != null) {
                    removeFromBlockPoss(i, val2);
                }
            }
        }
    }

    public void removeFromRowPoss(byte row, byte val) {
        for (byte col = 0; col < length; ++col) {
            if (isEmpty(row, col)) {
                removePossible(row, col, val);
            }
        }
    }

    public void removePossible(byte row, byte col, byte val) {
        possible[block(row, col)][pos(row, col)][val] = false;
    }

    public void removeFromColumnPoss(byte col, byte val) {
        for (byte row = 0; row < length; ++row) {
            if (isEmpty(row, col)) {
                removePossible(row, col, val);
            }
        }
    }

    public Byte[] getBlock(byte i) {
        return grid[i];
    }

    public void setBlock(byte i, Byte[] block) {
        grid[i] = block;
    }

    public void removeFromBlockPoss(byte block, byte val2) {
        for (byte pos = 0; pos < length; ++pos) {
            if (grid[block][pos] == null) {
                possible[block][pos][val2] = false;
            }
        }
    }

    public final void simplifyGrid() {
        boolean simplified = false;

        for (byte i = 0; i < length; ++i) {
            for (byte j = 0; j < length; ++j) {
                if (isEmpty(i, j)) {
                    Pair<Boolean, Byte> result = isCellNaked(i, j);

                    if (result.getFirst()) {
                        setValueAndGiven(i, j, result.getSecond(), true);
                        updatePossible(i, j);
                        removePossible();
                        simplified = true;
                    } else {
                        result = isHiddenSingle(i, j);

                        if (result.getFirst()) {
                            setValueAndGiven(i, j, result.getSecond(), true);
                            updatePossible(i, j);
                            removePossible();
                            simplified = true;
                        }
                    }
                }
            }
        }

        if (simplified) {
            simplifyGrid();
        }
    }

    public Pair<Boolean, Byte> isCellNaked(byte row, byte col) {
        Boolean[] poss = getPossible(row, col);
        byte count = 0;
        byte res = (byte) -1;

        for (byte i = 0; i < poss.length; ++i) {
            if (poss[i]) {
                ++count;
                res = i;

                if (count > 1) {
                    return new Pair<>(false, (byte) -1);
                }
            }
        }

        return count == 1 ? new Pair<>(true, res) : new Pair<>(false, (byte) -1);
    }

    public Pair<Boolean, Byte> isHiddenSingle(byte i, byte j) {
        Boolean[] poss = getPossible(i, j);

        for (byte k = 0; k < poss.length; ++k) {
            if (poss[k]) {
                if (isHiddenSingleInRow(i, j, k)) {
                    return new Pair<>(true, k);
                } else if (isHiddenSingleInColumn(i, j, k)) {
                    return new Pair<>(true, k);
                } else if (isHiddenSingleInBlock(i, j, k)) {
                    return new Pair<>(true, k);
                }
            }
        }

        return new Pair<>(false, (byte) -1);
    }

    public boolean isHiddenSingleInRow(byte row, byte col, Byte poss) {
        for (byte k = 0; k < length; ++k) {
            if (k != col && isEmpty(row, k) && isPossible(row, k, poss)) {
                return false;
            }
        }

        return true;
    }

    public boolean isHiddenSingleInColumn(byte row, byte col, Byte poss) {
        for (byte k = 0; k < length; ++k) {
            if (k != row && isEmpty(k, col) && isPossible(k, col, poss)) {
                return false;
            }
        }

        return true;
    }

    public boolean isHiddenSingleInBlock(byte row, byte col, Byte poss) {
        byte block = block(row, col);
        byte pos = pos(row, col);

        for (byte k = 0; k < length; ++k) {
            boolean isEmpty = (grid[block][pos] == null);
            boolean isPossible = possible[block][pos][poss];

            if (k != pos && isEmpty && isPossible) {
                return false;
            }
        }

        return true;
    }

    public final Byte[][] missingValuesInBlocks() {
        Byte[][] res = new Byte[length][];
        Boolean[][] tmp = new Boolean[length][];

        for (byte i = 0; i < length; ++i) {
            tmp[i] = new Boolean[length];

            for (byte j = 0; j < length; ++j) {
                tmp[i][j] = true;
            }

            byte nValues = 0;

            for (byte j = 0; j < length; ++j) {
                Byte value = grid[i][j];

                if (value != null) {
                    tmp[i][value] = false;
                    ++nValues;
                }
            }

            res[i] = new Byte[tmp[i].length - nValues];
            byte count = 0;

            for (Byte j = 0; j < tmp[i].length; ++j) {
                if (tmp[i][j]) {
                    res[i][count] = j;
                    ++count;
                }
            }
        }

        return res;
    }

    @Override
    public double getFitness() {
        return fitness;
    }

    @Override
    public final void computeFitness() {
        double res = 0;
        Boolean[] present = new Boolean[length];

        for (byte i = 0; i < this.length; ++i) {
            for (byte j = 0; j < present.length; ++j) {
                present[j] = false;
            }

            for (byte j = 0; j < this.length; ++j) {
                if (!isEmpty(i, j)) {
                    present[getValue(i, j)] = true;
                }
            }

            byte count = 0;

            for (byte j = 0; j < present.length; ++j) {
                if (present[j]) {
                    ++count;
                }
            }

            res += count;
        }

        for (byte j = 0; j < this.length; ++j) {
            for (byte i = 0; i < present.length; ++i) {
                present[i] = false;
            }

            for (byte i = 0; i < this.length; ++i) {
                if (!isEmpty(i, j)) {
                    present[getValue(i, j)] = true;
                }
            }

            byte count = 0;

            for (byte i = 0; i < present.length; ++i) {
                if (present[i]) {
                    ++count;
                }
            }

            res += count;
        }

        fitness = (double) res;
    }

    @Override
    public Chromosome copy() {
        Sudoku sdk = new Sudoku();
        sdk.length = this.length;
        sdk.blockSize = this.blockSize;
        sdk.grid = new Byte[this.length][];

        for (byte i = 0; i < this.length; ++i) {
            sdk.grid[i] = new Byte[this.length];

            for (byte j = 0; j < this.length; ++j) {
                sdk.grid[i][j] = this.grid[i][j];
            }
        }

        sdk.indexes = this.indexes;
        sdk.missing = this.missing;
        sdk.given = this.given;

        return sdk;
    }

    @Override
    public boolean isSolution() {
        return fitness == 2 * length * length;
    }

    @Override
    public boolean crossOver(Chromosome chr, double crossOverRate) {
        if (Rand.getInstance().nextDouble() <= crossOverRate) {
            int index1 = Rand.getInstance().nextInt(1, this.length - 1);
            int index2 = Rand.getInstance().nextInt(1 + index1, this.length);
            Sudoku rhs = (Sudoku) chr;

            for (int i = index1; i < index2; ++i) {
                for (int j = 0; j < length; ++j) {
                    Byte tmp = this.grid[i][j];
                    this.grid[i][j] = rhs.grid[i][j];
                    rhs.grid[i][j] = tmp;
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean mutate(double mutationRate) {
        //we iterate through the blocks
        Boolean mutated = false;

        for (int i = 0; i < length; ++i) {
            //we iterate through the elements of the blocks
            for (int j = 0; j < length; ++j) {
                if (Rand.getInstance().nextDouble() <= mutationRate) {
                    if (!given[i][j]) {
                        int choice = Rand.getInstance().nextInt(length);

                        while (choice == j) {
                            choice = Rand.getInstance().nextInt(length);
                        }

                        if (swapValues(i, j, choice)) {
                            if (!mutated) {
                                mutated = true;
                            }
                        }
                    }
                }
            }
        }

        return mutated;
    }

    private boolean swapValues(int block, int index1, int index2) {
        if (index1 == index2) {
            return false;
        }

        if (!given[block][index2] && !given[block][index1]) {
            byte value = grid[block][index1];
            grid[block][index1] = grid[block][index2];
            grid[block][index2] = value;

            return true;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Chromosome chr) {
        double diff = this.getFitness() - chr.getFitness();

        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        } else {
            Sudoku tmp = (Sudoku) chr;

            for (int i = 0; i < length; ++i) {
                for (int j = 0; j < length; ++j) {
                    if (!Objects.equals(grid[i][j], tmp.grid[i][j])) {
                        return grid[i][j] > tmp.grid[i][j] ? 1 : -1;
                    }
                }
            }

            return 0;
            //return this.toString().compareTo(((Sudoku)chr).toString());
        }
    }

    public ArrayList<Chromosome> randomChromosomes(int number) {
        ArrayList<Chromosome> res = new ArrayList<>(number);

        for (int i = 0; i < number; ++i) {
            res.add((Sudoku) copy());

            for (byte block = 0; block < length; ++block) {
                ((Sudoku) res.get(i)).setBlock(block, permutate(block));
            }

            ((Sudoku) res.get(i)).computeFitness();
        }

        return res;
    }

    public Byte[] permutate(byte block) {
        Byte[] res = new Byte[length];
        Byte[] shuffled = shuffle(missing[block]);
        byte count = 0;

        for (byte i = 0; i < length; ++i) {
            Byte value = getBlock(block)[i];

            if (value == null) {
                byte m = shuffled[count];
                res[i] = m;
                ++count;
            } else {
                res[i] = value;
            }
        }

        return res;
    }

    public static Byte[] shuffle(Byte[] array) {
        int size = array.length;
        Byte[] res = new Byte[size];

        for (int i = 0; i < size; ++i) {
            res[i] = array[i];
        }

        for (int i = 0; i < size; ++i) {
            int choice = Rand.getInstance().nextInt(size);
            byte tmp = res[i];
            res[i] = res[choice];
            res[choice] = tmp;
        }

        return res;
    }

    public String printGrid() {
        return length < 10 ? printGrid1() : printGrid2();
    }

    public String printGrid1() {
        String res = "";

        for (byte i = 0; i < length; ++i) {
            for (byte j = 0; j < length; ++j) {
                if (!isEmpty(i, j)) {
                    res += ((byte) (getValue(i, j) + 1) + " ");
                } else {
                    res += "- ";
                }

                if (j % blockSize == blockSize - 1) {
                    res += "   ";
                }
            }

            res += "\n";

            if (i % blockSize == blockSize - 1) {
                res += "\n";
            }
        }

        return res;
    }

    public String printGrid2() {
        String res = "";

        for (byte i = 0; i < length; ++i) {
            for (byte j = 0; j < length; ++j) {
                if (!isEmpty(i, j)) {
                    byte value = (byte) (getValue(i, j) + 1);

                    if (value < 10) {
                        res += (" " + value + " ");
                    } else {
                        res += value + " ";
                    }

                } else {
                    res += "-- ";
                }
                //res += (isEmpty(i, j) ? "-": (getValue(i, j) + 1)) + " ";
                if (j % blockSize == blockSize - 1) {
                    res += "   ";
                }
            }

            res += "\n";

            if (i % blockSize == blockSize - 1) {
                res += "\n";
            }
        }

        return res;
    }

    public String possibleToString() {
        String res = "\n";

        for (byte i = 0; i < length; ++i) {
            for (byte j = 0; j < length; ++j) {
                res += "(" + i + ", " + j + ") = " + possibleToString(i, j) + "\n";
            }
        }

        return res;
    }

    public String missingToString() {
        String res = "[";
        res += missingToString((byte) 0);

        for (byte i = 1; i < length; ++i) {
            res += ", " + missingToString(i);
        }

        res += "]";

        return res;
    }

    public int numberOfErrors() {
        int res = 0;

        for (byte i = 0; i < length; ++i) {
            res += numberOfErrorsInRow(i);
            res += numberOfErrorsInColumn(i);
        }

        return res;
    }

    public int numberOfErrorsInRow(byte row) {
        int nErrors = 0;
        boolean[] present = new boolean[length];

        for (byte col = 0; col < length; ++col) {
            present[col] = false;
        }

        for (byte col = 0; col < length; ++col) {
            if (!isEmpty(row, col)) {
                byte value = getValue(row, col);

                if (present[value]) {
                    ++nErrors;
                } else {
                    present[value] = true;
                }
            }
        }

        return nErrors;
    }

    public int numberOfErrorsInColumn(byte col) {
        int nErrors = 0;
        boolean[] present = new boolean[length];

        for (byte row = 0; row < length; ++row) {
            present[row] = false;
        }

        for (byte row = 0; row < length; ++row) {
            if (!isEmpty(row, col)) {
                byte value = getValue(row, col);

                if (present[value]) {
                    ++nErrors;
                } else {
                    present[value] = true;
                }
            }
        }

        return nErrors;
    }

    public int numberOfGivens() {
        return 0;
    }

    public Byte[] getMissing(byte i) {
        return missing[i];
    }

    public String missingToString(byte i) {
        String res = "[";
        Byte[] tmp = getMissing(i);
        res += tmp[0];

        for (byte j = 1; j < tmp.length; ++j) {
            res += ", " + tmp[j];
        }

        res += "]";

        return res;
    }

    public String possibleToString(byte i, byte j) {
        return Util.printArray(getPossible(i, j));
    }

    @Override
    public String toString() {
        return "SDK{"
                + "\ngrid=" + Util.printArray(grid)
                + ", \nfixed=" + Util.printArray(given)
                + ", \npossible=" + Util.printArray(possible)
                + ", \nindexes=" + Util.printArray(indexes)
                + ", \nmissing=" + Util.printArray(missing)
                + ", \nlength=" + length
                + "\n}";
    }

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        /*Rand.getInstance().setSeed(0);
        Sudoku sdk = new Sudoku("/home/ricky/NetBeansProjects/Sudoku2/src/sdk/sudoku5.txt");
        //System.out.println("sdk = " + sdk);
        System.out.println("sdk.printGrid() =\n" + sdk.printGrid());
        //System.out.println("sdk.possible = " + sdk.possibleToString());
        System.out.println("sdk.missing = " + sdk.missingToString());
        System.out.println("sdk.fitness = " + sdk.getFitness());
        Byte[] permutated = sdk.permutate((byte) 0);
        System.out.println("sdk.permutate(" + 0 + ") = " + Arrays.toString(permutated));*/
        //test("/home/john/NetBeansProjects/Sudoku1/src/sudoku1/sudoku1.txt");

        String settingPathName
                = "/home/john/NetBeansProjects/Sudoku/src/sudoku/settings.txt";
        Settings settings = new Settings(settingPathName);
        Rand.getInstance().setSeed(settings.getSeed());
        Sudoku sdk = new Sudoku(settings.getGridPath());
        System.out.println("sdk =\n" + sdk.printGrid());
        System.out.println("sdk.getFitness() = " + sdk.getFitness());
        System.out.println("sdk.numberOfGivens() = " + sdk.numberOfGivens());
        //System.out.println("sdk =\n" + sdk);
        Thread chrono = new Thread(new Chronometer(10000));
        chrono.start();
        int nThreads = settings.getNumberOfThreads();
        GASolver[] solvers = new GASolver[nThreads];
        Thread[] threads = new Thread[nThreads];

        for (int i = 0; i < threads.length; ++i) {
            ArrayList<Chromosome> chrs = sdk.randomChromosomes(settings.getPopulationSize());
            //System.out.println("i = " + i);

            int min = 162;

            for (int j = 0; j < chrs.size(); ++j) {
                Sudoku tmp = (Sudoku) (chrs.get(j));

                /*if (!tmp.sameValuesAtGivens(sdk)) {
                    System.out.println("@@@@@tmp = " + tmp.printGrid());
                    System.exit(j);
                    throw new UnsupportedOperationException("!tmp.sameGivens(sdk)");
                }*/
                int nErrors = tmp.numberOfErrors();

                if (nErrors < min) {
                    /*System.out.println("j = " + j);
                    System.out.println("tmp = " + tmp.printGrid());*/
                    min = nErrors;
                }
            }

            //System.out.println("min = " + min);
            solvers[i] = new GASolver(
                    //sdk,
                    chrs,
                    settings.getSelector(i),
                    settings,
                    Integer.toString(i)
            );

            solvers[i].setSelector(settings.getSelector(i));
            solvers[i].setVerbose(true);
            threads[i] = new Thread(solvers[i]);
            threads[i].start();
        }

        boolean on = true && threads.length > 0;
        double bestFitness = 0;
        Chromosome bestFit = null;

        while (on) {
            for (int i = 0; i < threads.length && on; ++i) {
                if (solvers[i].hasSolution()) {
                    Sudoku solution = (Sudoku) solvers[i].getBestFit();

                    /*if (!solution.sameValuesAtGivens(sdk)) {
                        throw new UnsupportedOperationException("!solution.sameGivens(sdk)");
                    }*/
                    System.out.println("GASolver" + i);
                    System.out.println("Generation" + solvers[i].getCurrentGenerationNumber());
                    System.out.println("solution =\n" + solution.printGrid());
                    System.out.println("solution fitness = " + solution.getFitness());
                    System.exit(0);
                } else {
                    if (solvers[i].hasEnded()) {
                        System.out.println("No solution found.");
                        System.exit(0);
                    } else {
                        Chromosome chr = solvers[i].getBestFit();

                        if (chr != null && chr.getFitness() > bestFitness) {
                            bestFitness = chr.getFitness();
                            bestFit = chr;
                            System.out.println("best fitness = " + bestFitness);
                        }
                    }
                }
            }
        }
    }
}
