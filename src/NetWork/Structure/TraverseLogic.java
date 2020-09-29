package NetWork.Structure;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class TraverseLogic<T> implements Traverse<T> {
    protected final ThreadLocal<Collection<GraphNode<T>>> greyCollection;
    protected final ThreadLocal<Set<GraphNode<T>>> blackCollection;

    public TraverseLogic() {
        greyCollection = ThreadLocal.withInitial(Stack::new);
        blackCollection = ThreadLocal.withInitial(HashSet::new);
    }

    protected void pushToLocalStack(@NotNull final GraphNode<T> initialState) {
        ((Stack<GraphNode<T>>)this.greyCollection.get()).push(initialState);
    }

    @Nullable
    protected GraphNode<T> popFromLocalStack() throws RuntimeException {
        return ((Stack<GraphNode<T>>)this.greyCollection.get()).pop();
    }


    @NotNull
    @Override
    public AbstractList<T> traverse(@NotNull final Traversable<T> graph) {
        // get ThreadLocal collections
        Collection<GraphNode<T>> grey =this.greyCollection.get();
        Set<GraphNode<T>> black =this.blackCollection.get();

        pushToLocalStack(graph.getOrigin());
        black.clear(); // do not remove
        while(!grey.isEmpty()){
            GraphNode<T> removedNode= popFromLocalStack();
            black.add(removedNode);
            Collection<GraphNode<T>> reachableNodes = graph.getReachableNodes(removedNode);
            for(GraphNode<T> a : reachableNodes){
                Boolean boo= true;
                for(GraphNode<T> b :black){
                    if(b.equals(a)){
                        boo = false;
                        break;
                    }
                }
                if(boo) {
                    for (GraphNode<T> b : grey) {
                        if (b.equals(a)) {
                            boo = false;
                            break;
                        }
                    }
                    if (boo) {
                        pushToLocalStack(a);
                    }
                }
            }

        }

        return black.stream().map(GraphNode::getData).collect(Collectors.toCollection(ArrayList::new));
    }

    /*Submarine Algo - Q:4*/
    @Override
    public boolean Sub(@NotNull final Traversable<T> graph,boolean[][] seen) {
        // get ThreadLocal collections
        Collection<GraphNode<T>> grey = this.greyCollection.get();
        Set<GraphNode<T>> black = this.blackCollection.get();

        pushToLocalStack(graph.getOrigin());
        black.clear(); // do not remove
        while (!grey.isEmpty()) {
            GraphNode<T> removedNode = popFromLocalStack();
            black.add(removedNode);
            if(removedNode.getData() instanceof  Index){
                seen[((Index) removedNode.getData()).getRow()][((Index) removedNode.getData()).getCol()] = true;
            }
            Collection<GraphNode<T>> reachableNodes = graph.getReachableNodes(removedNode);
            // add each reachable node if it was not finished with, nor previously discovered
//            [1,1,0]
//            [0,0,1]
//            [0,0,1]
            if (isCorrect(reachableNodes,(Index)removedNode.getData())) {
                for (GraphNode<T> a : reachableNodes) {
                    Boolean boo = true;
                    for (GraphNode<T> b : black) {
                        if (b.equals(a)) {
                            boo = false;
                            break;
                        }
                    }
                    if (boo) {
                        for (GraphNode<T> b : grey) {
                            if (b.equals(a)) {
                                boo = false;
                                break;
                            }
                        }
                        if (boo) {
                            pushToLocalStack(a);
                        }
                    }
                }

            } else {
                return false;
            }
        }
        //Validation  for at list 2 indices with value of 1
        return black.size() > 1;
    }

    private boolean isCorrect(Collection<GraphNode<T>> reachableNodes,Index Node) {
        /*Create a boolean matrix referencing for the current Node
        *[[T/F,T/F,T/F]
        * [T/F,cNode,T/F]
        * [T/F,T/F,T/F]]
        * each cell hold the correct value for the neighbor
        *  */
        boolean[][] places = new boolean[3][3];
        for(GraphNode<T> r :reachableNodes){
            if(r.getData() instanceof Index) {
                Index currentNeighbor =(Index) r.getData();
                places[currentNeighbor.getRow() - Node.getRow() + 1][currentNeighbor.getCol() - Node.getCol() + 1] = true;
            }
        }
        if(places[0][1]&&places[1][0]){
            /*Check for this error CN-currentNode
            * [[0,1,0]
            *  [1,CN,0]
            *  [0,0,0]
            * */
            if(!places[0][0]){
                return false;
            }
        }
        if(places[0][1]&&places[1][2]){
            /*Check for this error CN-currentNode
             * [[0,1,0]
             *  [0,CN,1]
             *  [0,0,0]
             * */
            if(!places[0][2]){
                return false;
            }
        }
        if(places[1][2]&&places[2][1]){
            /*Check for this error CN-currentNode
             * [[0,0,0]
             *  [0,CN,1]
             *  [0,1,0]
             * */
            if(!places[2][2]){
                return false;
            }
        }
        if(places[2][1]&&places[1][0]){
            /*Check for this error CN-currentNode
             * [[0,0,0]
             *  [1,CN,0]
             *  [0,1,0]
             * */
            if(!places[2][0]){
                return false;
            }
        }
        if(places[2][1]&&places[1][0]&&places[0][1]&&places[1][2]){
            /*Check for this error CN-currentNode
             * [[0,1,0]
             *  [1,CN,1]
             *  [0,1,0]
             * */
            if(!places[2][0]||!places[2][2] ||!places[0][0]||!places[0][2]){
                return false;
            }
        }
        if(!places[2][1]&&!places[1][0]&&!places[0][1]&&!places[1][2]){
            /*Check for this error CN-currentNode
             * [[0,0,0]
             *  [0,CN,0]
             *  [0,0,0]
             * */
            return false;
        }
        return true;
    }

    public List<List<GraphNode<Index>>> bfs(List<List<GraphNode<Index>>> indexList, MatrixAsGraph graph, boolean[][] seen, Stack<GraphNode<Index>> path, Index end) throws InterruptedException {
        Queue<GraphNode<Index>> queue = new LinkedList<>();
        List<GraphNode<Index>> visited = new LinkedList<>();
        HashMap<GraphNode<Index>, Collection<GraphNode<Index>>> indexListHashMap = new HashMap<>();
        queue.add(graph.getOrigin());
        while(!queue.isEmpty()){
            GraphNode<Index> node = queue.poll();
            visited.add(node);
            @NotNull Collection<GraphNode<Index>> reachables = graph.getReachableNodes(node);
            boolean isInKey = false;
            for(GraphNode<Index> key: indexListHashMap.keySet()){
                if(key.equals(node)){
                    isInKey=true;
                    break;
                }
            }
            if(!isInKey) {
                indexListHashMap.put(node, reachables);
            }
            for(GraphNode<Index> gNode :reachables){
                boolean flag = true;
                if(visited.contains(gNode) || queue.contains(gNode)){
                    flag=false;
                }
                if(flag){
                    queue.add(gNode);
                }
            }

        }
        //At this point we have a HashMap of each index with a list of reachable nodes - called indexListHashMap
        ThreadPoolExecutor executorPool = new ThreadPoolExecutor(100, 100, 10, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<>());
        ParallelDfs p = new ParallelDfs(indexList, indexListHashMap, path, end,executorPool);
        Thread t = new Thread(p);
        t.start();
        t.join();

        executorPool.awaitTermination(graph.getMatrix().getRow()*graph.getMatrix().getCol()*20,TimeUnit.MILLISECONDS);
        return indexList;
    }

    public Double euclideanDis(Index A,Index B){
        //Heuristic for ASTAR Algorithm
        return Math.abs(Math.pow(A.getRow()-B.getRow(),2)+Math.pow(A.getCol()-B.getCol(),2));
    }

    public List<Index> AStar(MatrixAsGraph graph, Set<Index> closeSet, List<Index> openSet,Index end){
        Index Start = graph.getOrigin().getData();
        HashMap<Index,Index> cameFrom = new HashMap<>();
        HashMap<Index,Double> g_score = new HashMap<>();//Score of path until Now
        HashMap<Index,Double> f_score = new HashMap<>();//How To choose next Node
        g_score.put(Start,0.0);
        f_score.put(Start,g_score.get(Start)+euclideanDis(Start,end));

        while(!openSet.isEmpty()){
            List<Index> relevantNode = f_score.keySet().stream().filter(key->openSet.contains(key)).collect(Collectors.toList());
                   Index current = f_score.keySet().stream().filter(relevantNode::contains).min(Comparator.comparing(f_score::get)).get();

            if(current.equals(end)){
                return reconstruct_path(cameFrom,end,new LinkedList<>());
            }
            Index findC = g_score.keySet().stream().filter(key-> key.equals(current)).findFirst().get();
            openSet.remove(findC);
            closeSet.add(findC);
            Collection<GraphNode<Index>> reachables = graph.getReachableNodes(new GraphNode<>(current));
            HashMap<Index,Double> tentativeG_score = new HashMap<>();//Score of path until Now
            for(GraphNode<Index> r: reachables){
                tentativeG_score.put(r.getData(),g_score.get(current)+euclideanDis(r.getData(),current));
                boolean seen = false;
                for(Index c: closeSet){
                    if(c.equals(r.getData())){
                        seen =true;
                        break;
                    }
                }
                boolean scoreCalculates = false;
                for(Index score :g_score.keySet()){
                    if(r.getData().equals(score)){
                        scoreCalculates =true;
                        break;
                    }
                }
                if(seen){
                    continue;
                }
                if(scoreCalculates){
                    Double tentativDouble =tentativeG_score.get(r.getData());
                    Index find = g_score.keySet().stream().filter(key-> key.equals(r.getData())).findFirst().get();
                    Double gDouble =g_score.get(find);
                    if(tentativDouble.compareTo(gDouble) > 0){
                        continue;
                    }
                }
                cameFrom.remove(r.getData());
                cameFrom.put(r.getData(),current);
                if(scoreCalculates){
                    Index find = g_score.keySet().stream().filter(key-> key.equals(r.getData())).findFirst().get();
                    g_score.remove(find);
                    f_score.remove(find);
                }
                g_score.put(r.getData(),tentativeG_score.get(r.getData()));
                f_score.put(r.getData(),g_score.get(r.getData())+ euclideanDis(r.getData(),end));
                for(Index o : openSet){
                    if(o.equals(r.getData())){
                        seen = true;
                        break;
                    }
                }
                if(!seen){
                    openSet.add(r.getData());
                }
            }

        }
        return null;
    }
    public List<Index> reconstruct_path(Map<Index ,Index> cameFrom, Index end,List<Index> path){
        Index pathKey = null;
        boolean isthere = false;
        for(Index key:cameFrom.keySet()){
            if(key.equals(end)){
                pathKey= key;
                isthere=true;
                break;
            }
        }
        if(isthere) {
            path.add(end);
            return reconstruct_path(cameFrom, cameFrom.get(pathKey),path);
        }
        path.add(end);
        return path;
    }
    @Override
    public List<List<Index>> Shortest_PATHS(List<List<Index>> indexList , MatrixAsGraph graph, boolean[][] seen , Stack<Index> path, Index end) {
        /*
        first we calculate a shortest path using Astar algorithm deu to fast calculation
        then we search for paths that only shortest or same length
         */
        Index Start = graph.getOrigin().getData();
        List<Index> openSet = new LinkedList<>();
        openSet.add(Start);
        List<Index> Shortest = AStar(graph,new HashSet<>(),openSet,end);
        graph.setIndex(Start);
        indexList.add(Shortest);
        Stack<Index> newPath = new Stack<>();
        newPath.add(Start);
        indexList=findShortestPaths(indexList ,graph, seen , newPath, end);
        indexList.remove(Shortest);
        return indexList;

    }

    public List<List<Index>> findShortestPaths(List<List<Index>> indexList , MatrixAsGraph graph, boolean[][] seen , Stack<Index> path, Index end){
        GraphNode<Index> a = graph.getOrigin();
        seen[a.getData().getRow()][a.getData().getCol()] = true;
        int shortest = indexList.get(0).size();
        if(a.getData().equals(end)){
            if(path.size() == shortest){
                indexList.add(new ArrayList<>(path));
            }
            if(path.size() < shortest){
                indexList.clear();
                indexList.add(new ArrayList<>(path));
            }
            seen[a.getData().getRow()][a.getData().getCol()]=false;
            return indexList;
        }
        Collection<GraphNode<Index>> reachables = graph.getReachableNodes(a);
        for (GraphNode<Index> b : reachables) {
            if (!seen[b.getData().getRow()][b.getData().getCol()]) {
                graph.setIndex(b.getData());
                path.push(b.getData());
                //Check if the current path is shorter or equal to our current shortest path
                if(path.size()<=shortest) {
                    findShortestPaths(indexList, graph, seen, path, end);
                }
                path.pop();
            }
        }
            seen[a.getData().getRow()][a.getData().getCol()] = false;


        return indexList;
    }

    private class ParallelDfs implements Runnable{
        //This class is for implementing our parallel dfs, it save the current stats like current node to start from and the path.
        //we push it to the executor and he manage the tasks.
        GraphNode<Index> current;
        List<List<GraphNode<Index>>> indexList;
        HashMap<GraphNode<Index>,Collection<GraphNode<Index>>> map;
        Stack<GraphNode<Index>> path;
        Index end;
        ThreadPoolExecutor executorPool;

        public ParallelDfs(List<List<GraphNode<Index>>> indexList, HashMap<GraphNode<Index>, Collection<GraphNode<Index>>> map, Stack<GraphNode<Index>> path, Index end, ThreadPoolExecutor executorPool) {
            this.indexList = indexList;
            this.current = new GraphNode<>(path.peek().getData());
            this.map = map;
            this.path = (Stack<GraphNode<Index>>) path.clone();
            this.end = new Index(end.getRow(),end.getCol());
            this.executorPool =executorPool;
        }



        @Override
        public void run() {
            if(current.getData().equals(end)){
                indexList.add(path);
            }
            else {
                Collection<GraphNode<Index>> reachable = map.get(current);
                try {
                    if (reachable != null) {
                        for (GraphNode<Index> r : reachable) {
                            boolean seen = false;
                            for(GraphNode<Index> p : path){
                                if(p.equals(r)){
                                    seen =true;
                                    break;
                                }
                            }
                            if (!seen) {
                                path.push(r);
                                ParallelDfs p = new ParallelDfs(indexList, map, path, end, executorPool);
                                executorPool.execute(p);
                                path.pop();
                            }
                        }
                    }
                }
                catch (Exception e){
                    System.out.println(e.toString());
                }
            }
        }
    }
}