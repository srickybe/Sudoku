/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudoku;

import java.util.Objects;

/**
 *
 * @author Ricky
 */
public class Element {

    private static final int SIZE = 9;
    private Integer value;
    private boolean mutable;
    private Container row;
    private Container column;
    private Container block;

    public Element(
            Integer value,
            boolean mutable,
            Container row,
            Container column,
            Container block) {
        if (value == 0) {
            if (!mutable) {
                throw new UnsupportedOperationException();
            }
        } else {
            if (value > 0 && value <= SIZE) {
                if (mutable) {
                    throw new UnsupportedOperationException();
                }
            } else {
                throw new UnsupportedOperationException();
            }
        }

        this.value = value;
        this.mutable = mutable;
        this.row = row;
        this.column = column;
        this.block = block;
    }

    public Element() {
        value = 0;
    }

    public int getValue() {
        return value;
    }

    public boolean isPossible(Integer value) {
        return mutable && !row.contains(value) && !column.contains(value)
                && !block.contains(value);
    }

    public boolean setValue(Integer value) {
        if (mutable) {
            if (!isPossible(value)) {
                return false;
            } else {
                this.value = value;
                row.add(value);
                column.add(value);
                block.add(value);

                return true;
            }
        } else {
            return false;
        }
    }

    public boolean reset() {
        if (mutable) {
            if (value != 0) {
                row.remove(value);
                column.remove(value);
                block.remove(value);
                value = 0;

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Container getRow() {
        return row;
    }

    public void setRow(Container row) {
        this.row = row;
    }

    public Container getColumn() {
        return column;
    }

    public void setColumn(Container column) {
        this.column = column;
    }

    public Container getBlock() {
        return block;
    }

    public void setBlock(Container block) {
        this.block = block;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + this.value;
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

        final Element other = (Element) obj;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public String toString() {
        return "  " + value + "  ";
    }
}
