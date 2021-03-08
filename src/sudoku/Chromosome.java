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
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author ricky
 */
public class Chromosome implements Comparable<Chromosome> {

    private boolean[] bases;
    private double fitness;

    public Chromosome(int nBases) {
        bases = new boolean[nBases];
        fitness = 0.0;
    }

    public Chromosome(boolean[] bases, double fitness) {
        this.bases = bases.clone();
        this.fitness = fitness;
    }

    public Chromosome(Chromosome chr) {
        this(chr.bases, chr.fitness);
    }

    /*
    Returns true if this and chr both reference to the same object
    Returns false if this and chr reference objects of different classes
    Returns true if this.bases and chr.bases reference to the same array
    Returns true if the this.bases an chr.bases contains the same array elements
     */
    @Override
    public boolean equals(Object chr) {
        System.out.println("public boolean equals(Object chr)");
        if (this == chr) {
            return true;
        }

        if (chr instanceof Chromosome) {
            return this.equals(chr);
        }

        return false;
    }

    public boolean equals(Chromosome chr) {
        System.out.println("public boolean equals(Chromosome chr)");

        return Arrays.equals(bases, chr.bases)
                && Objects.equals(fitness, chr.fitness);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Arrays.hashCode(this.bases);
        hash = 11 * hash + Objects.hashCode(this.fitness);

        return hash;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public static Chromosome randomChromosome(int nBases) {
        Chromosome chr = new Chromosome(nBases);

        for (int i = 0; i < nBases; ++i) {
            chr.bases[i] = Util.randomBoolean();
        }

        chr.fitness = 0.0;

        return chr;
    }

    public boolean[] getBases() {
        return bases;
    }

    public boolean getBase(int i) {
        return bases[i];
    }

    public int length() {
        return bases.length;
    }

    public void setBase(int i, boolean val) {
        bases[i] = val;
    }

    public void crossOver(Chromosome chr, double crossOverRate) {
        if (this.length() != chr.length()) {
            throw new IllegalArgumentException();
        }

        if (Util.randomDouble() <= crossOverRate) {
            int index1 = Util.randomInt(1, this.length() - 1);
            int index2 = Util.randomInt(1 + index1, this.length());

            for (int i = index1; i < index2; ++i) {
                boolean base = this.getBase(i);
                this.setBase(i, chr.getBase(i));
                chr.setBase(i, base);
            }
        }
    }

    public void mutate(double mutationRate) {
        for (int i = 0; i < bases.length; ++i) {
            if (Util.randomDouble() <= mutationRate) {
                int index = Util.randomInt(length());
                bases[index] = Util.mutateBase(bases[index]);
            }
        }
    }

    @Override
    public String toString() {
        return "Chromosome{"
                + "bases=" + Arrays.toString(bases)
                + ", fitness=" + fitness
                + '}';
    }

    public static void testEquals() {
        boolean[] bases = Util.randomBooleans(5);
        double fitness = 1.0;
        Chromosome chr1 = new Chromosome(bases, fitness);
        Chromosome chr2 = new Chromosome(bases, 2.0);
        System.out.println("chr1.equals(chr2) ? " + chr1.equals(chr2));
    }

    public static void main(String[] args) {
        testChromo();
    }

    @Override
    public int compareTo(Chromosome chr) {
        double diff = this.getFitness() - chr.getFitness();

        if (diff > 0) {
            return -1;
        }

        if (diff < 0) {
            return 1;
        }

        return 0;
    }

    public static void testChromo() {
        int nChrs = 10;
        Chromosome[] chrs = new Chromosome[nChrs];

        for (int i = 0; i < nChrs; ++i) {
            chrs[i] = new Chromosome(
                    new boolean[]{Util.randomBoolean()},
                    Util.randomDouble()
            );
        }

        for (var a : chrs) {
            System.out.println("a = " + a);
        }

        Arrays.sort(chrs);

        for (var a : chrs) {
            System.out.println("###a = " + a);
        }
    }
}
