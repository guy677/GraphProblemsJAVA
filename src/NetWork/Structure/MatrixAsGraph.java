package NetWork.Structure;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import java.lang.*;


public class MatrixAsGraph implements Traversable<Index> {
    private final Matrix matrix;
    private Index index;

    public MatrixAsGraph(@NotNull final Matrix matrix) {
        this.matrix = matrix;
    }

    public Index getIndex() {
        return index;
    }

    public void setIndex(@NotNull final Index index) {
        this.index = index;
    }

    @NotNull
    @Override
    public GraphNode<Index> getOrigin() throws NullPointerException {
        if (this.index == null) throw new NullPointerException("initIndex is not initialized");
        return new GraphNode<>(this.index);
    }

    @NotNull
    @Override
    public Collection<GraphNode<Index>> getReachableNodes(@NotNull final GraphNode<Index> s) {
        return this.matrix.getNeighbors(s.getData()).stream().filter(index -> matrix.getValue(index) == 1)
                .map(neighbor -> new GraphNode<>(neighbor, s)).collect(Collectors.toList());
    }



    @NotNull
    public Matrix getMatrix() {
        return matrix;
    }

}
