package NetWork.Structure;



import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * example for interface's generics: some class that extends MatrixAsGraph
 * MatrixAsGraph implements Traversable<Index>
 * We should return all the groups of 1's- List<Index>
  */


public interface GenericTraverse<R,V extends Traversable<R>> {
    AbstractList<R> traverse(@NotNull final V s);
    List<List<Index>> Shortest_PATHS(List<List<Index>> indexList , MatrixAsGraph graph, boolean[][] seen , Stack<Index> path, Index end) throws InterruptedException, ExecutionException;
    boolean Sub(@NotNull Traversable<R> graph,boolean[][] seen);

}
