package NetWork.Structure;

import java.io.Serializable;
import java.util.Objects;

public class Index implements Serializable {
    int row,col;

    public Index(final int row,final int col){
        this.setRow(row);
        this.setCol(col);
    }
    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public String toString(){
        return "(" + this.getRow() + "," + this.getCol() + ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(row,col);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Index)) return false;

        return this.row == ((Index) o).getRow() && this.col == ((Index) o).getCol();

    }

}
