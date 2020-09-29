package NetWork.Structure;

import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

public abstract class AbstractMatrix implements MatrixInterface {
    private int[][] primitiveMatrix;
    private int col;
    private int row;

    public AbstractMatrix(){
        this(5,5);
    }
    public AbstractMatrix(int rowSize,int colSize){
        this.row=rowSize;
        this.col=colSize;
        assert(rowSize>0 && colSize>0);
        Random random =new Random();
        primitiveMatrix = new int[rowSize][colSize];
//    Static matrix for testing:
//        primitiveMatrix = new int[][]{
//                {1, 1, 1,1,1},
//                {1, 1, 1,1,1},
//                {1, 1, 1,1,1},
//                {1, 1, 1,1,1},
//                {1, 1, 1,1,1}};
        primitiveMatrix = new int[rowSize][colSize];
        for(int i=1;i<rowSize;i++){
            for(int j=1;j<colSize;j++){
               primitiveMatrix[i][j] = random.nextInt(2);
            }
        }
    }

    public int getCol(){return this.col;}
    public int getRow(){return this.row;}
    public int[][] getPrimitiveMatrix(){return this.primitiveMatrix;}

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int[] row:primitiveMatrix){
            stringBuilder.append(Arrays.toString(row));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public int getValue(Index index) {
        return primitiveMatrix[index.getRow()][index.getCol()];
    }

    public abstract Collection<Index> getNeighbors(final Index index);

    public void printMatrix(){
        for (int[] row : primitiveMatrix) {
            String s = Arrays.toString(row);
            System.out.println(s);
        }
    }

    public abstract Object RandomI();
}
