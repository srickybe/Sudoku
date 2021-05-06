/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeMap;

/**
 *
 * @author ricky
 */
public class Reserve {

    private final TreeMap<Integer, HashSet<Chromosome>> chromosomesByFitness;

    public Reserve() {
        chromosomesByFitness = new TreeMap<>(Collections.reverseOrder());
    }

    public boolean add(Chromosome chr) {
        HashSet<Chromosome> set = chromosomesByFitness.get((int) chr.getFitness());

        if (set == null) {
            set = new HashSet<>();

            if (set.add(chr)) {
                chromosomesByFitness.put((int) chr.getFitness(), set);

                return true;
            } else {
                return false;
            }
        } else {
            return set.add(chr);
        }
    }

    public boolean addAll(Collection<Chromosome> collection) {
        boolean allAdded = true;

        for (Chromosome chr : collection) {
            if (!this.add(chr)) {
                if (allAdded) {
                    allAdded = false;
                }
            }
        }

        return allAdded;
    }

    public void clear() {
        chromosomesByFitness.clear();
    }

    /*public void set(ArrayList<Chromosome> chrs) {
        clear();
        addAll(chrs);
    }*/
    
    public ArrayList<Chromosome> toArrayList() {
        ArrayList<Chromosome> res = new ArrayList<>();

        chromosomesByFitness.entrySet().forEach(entry -> {
            res.addAll(entry.getValue());
        });

        return res;
    }

    public int getNumberOfChromosomes() {
        int res = 0;

        for (var entry : chromosomesByFitness.entrySet()) {
            res += entry.getValue().size();
        }
        
        return res;
    }

    public Pair<ArrayList<Chromosome>, Boolean> getNBestFit(int nChrs) {
        ArrayList<Chromosome> bestFit = new ArrayList<>(nChrs);
        int count = 0;

        for (var entry : chromosomesByFitness.entrySet()) {
            for (var chr : entry.getValue()) {
                if (count < nChrs) {
                    if (bestFit.add(chr)) {
                        ++count;
                    } else {
                        throw new UnsupportedOperationException();
                    }
                } else {
                    return new Pair(bestFit, true);
                }
            }
        }

        return new Pair(bestFit, false);
    }

    @Override
    public String toString() {
        return "Reserve{"
                + "chromosomesByFitness=" + chromosomesByFitness
                //+ ", numberOfChromosomes=" + numberOfChromosomes 
                + '}';
    }

    public static void main(String args[]) {
        Rand.getInstance().setSeed(0);
        Reserve reserve = new Reserve();
        Chromosome[] chromos = new TestChromo[10];

        for (int i = 0; i < chromos.length; ++i) {
            chromos[i] = new TestChromo(5);
            reserve.add(chromos[i]);
            System.out.println(i + ": " + chromos[i]);
        }

        System.out.println("reserve = " + reserve);
        System.out.println("reserve.add(tmp[0]) ?" + reserve.add(chromos[0]));
        System.out.println("+++++reserve = " + reserve);
        var chrs = reserve.toArrayList();

        for (var chr : chrs) {
            System.out.println("chr =\n" + chr);
            System.out.println("chr.getFitness() = " + chr.getFitness());
        }

        chrs = reserve.getNBestFit(25).getFirst();

        for (var chr : chrs) {
            System.out.println("*****chr.getFitness() = " + chr.getFitness());
        }
    }
}
