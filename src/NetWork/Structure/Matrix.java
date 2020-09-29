package NetWork.Structure;

import java.util.*;
import java.util.stream.Collectors;

public class Matrix extends AbstractMatrix {
    public Matrix(int row, int col) {
        super(row,col);
    }
    public Matrix() {
        super();
    }

    @Override
    public Collection<Index> getNeighbors(Index index ) {
        int row =index.getRow();
        int col = index.getCol();
        int extracted = -1;
        Collection<Index> list = new ArrayList<>();
        try {
            extracted = this.getPrimitiveMatrix()[index.row-1][index.col-1];
            list.add(new Index(row - 1, col - 1));
        } catch (IndexOutOfBoundsException o){
        }
        try {
            extracted = this.getPrimitiveMatrix()[index.row-1][index.col];
            list.add(new Index(row - 1, col));
        } catch (IndexOutOfBoundsException o){
        }
        try {
            extracted = this.getPrimitiveMatrix()[index.row-1][index.col+1];
            list.add(new Index(row - 1, col + 1));
        } catch (IndexOutOfBoundsException o){
        }
        try {
            extracted = this.getPrimitiveMatrix()[index.row][index.col-1];
            list.add(new Index(row, col - 1));

        } catch (IndexOutOfBoundsException o){
        }
        try {
            extracted = this.getPrimitiveMatrix()[index.row][index.col+1];
            list.add(new Index(row , col +1));
        } catch (IndexOutOfBoundsException o){
        }
        try {
            extracted = this.getPrimitiveMatrix()[index.row+1][index.col-1];
            list.add(new Index(row + 1, col - 1));
        } catch (IndexOutOfBoundsException o){
        }
        try {
            extracted = this.getPrimitiveMatrix()[index.row+1][index.col];
            list.add(new Index(row + 1, col ));
        } catch (IndexOutOfBoundsException o){
        }
        try {
            extracted = this.getPrimitiveMatrix()[index.row+1][index.col+1];
            list.add(new Index(row + 1, col + 1));
        } catch (IndexOutOfBoundsException o){
        }
        return list;
    }

    public Collection<Index> getReachables(Index index){
        Collection<Index> reachable = new ArrayList<>();
        this.getNeighbors(index).stream().filter(i-> getValue(i)==1)
                .map(reachable::add).collect(Collectors.toList());
        return reachable;
    }

    @Override
    public Object RandomI() {
        int count=0;
        Random r = new Random();
        Index random =new Index(r.nextInt(this.getRow()),r.nextInt(this.getCol()));
        while(this.getValue(random)!=1 && count<this.getRow()*this.getCol()){
            count+=1;
            random.setCol(r.nextInt(this.getCol()));
            random.setRow(r.nextInt(this.getRow()));
            if(this.getValue(random) == 1)
            {
                break;
            }
        }
        return random;
    }




}
