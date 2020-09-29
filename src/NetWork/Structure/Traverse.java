package NetWork.Structure;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

public interface Traverse<R> extends GenericTraverse<R,Traversable<R>>{
     List<List<GraphNode<Index>>> bfs(List<List<GraphNode<Index>>> indexList, MatrixAsGraph graph, boolean[][] seen, Stack<GraphNode<Index>> path, Index end) throws InterruptedException;
}
