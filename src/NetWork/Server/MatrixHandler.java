package NetWork.Server;

import NetWork.Client;
import NetWork.Structure.*;
import NetWork.TaskManager.TaskType;
import NetWork.TaskManager.TaskWrapper;

import java.io.*;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class MatrixHandler implements IHandler {

    private Matrix matrix;
    private Index start, end;
    private ThreadPoolExecutor executor;
    int clientID;


    public MatrixHandler() {
        this.resetParams();
    }

    private void resetParams() {
        this.matrix = null;
        this.start = null;
        this.end = null;
    }

    @Override
    public void handle(InputStream inClient, OutputStream outClient) throws Exception {
        System.out.println("server::start handle");
        if (inClient == null) {
            inClient = System.in;
        }
        if (outClient == null) {
            outClient = System.out;
        }
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outClient);
        ObjectInputStream objectInputStream = new ObjectInputStream(inClient);

        this.resetParams();

        boolean dowork = true;
        while (dowork) {
            String task = objectInputStream.readObject().toString();
            switch (task) {
                case "stop": {
                    dowork = false;
                    break;
                }
                case "matrix": {
                    this.clientID = (int) objectInputStream.readObject();
                    this.matrix = (Matrix) objectInputStream.readObject();
                    this.matrix.printMatrix();
                    break;
                }
                case "start Index": {
                    this.start = (Index) objectInputStream.readObject();
                    break;
                }
                case "end Index": {
                    this.end = (Index) objectInputStream.readObject();
                    break;
                }
                case "AdjacentIndices": {
                    // receiving index for getAdjacentIndices
                    Index indexAdjacentIndices = (Index) objectInputStream.readObject();
                    Collection<Index> adjacentIndices = new ArrayList<>();
                    if (this.matrix != null) {
                        adjacentIndices.addAll(this.matrix.getReachables(indexAdjacentIndices));
                    }
                    // sending getAdjacentIndices
                    System.out.println("server::getAdjacentIndices:: " + adjacentIndices);
                    objectOutputStream.writeObject(adjacentIndices);
                    break;
                }
                case "AllReachables": {
                    // receiving index for getReachables
                    executor = new ThreadPoolExecutor(
                                    3, 5, 10,
                                    TimeUnit.SECONDS, new LinkedBlockingQueue<>());
                    Collection<Index> reachables = new ArrayList<>();
                    List<HashSet<Index>> indexList = Collections.synchronizedList(new ArrayList<>());
                    if (this.matrix != null) {
                        HashSet<Index> seenIndexes = new HashSet<>();

                        Traverse<Index> algorithm = new TraverseLogic<>();
                        MatrixAsGraph graph = new MatrixAsGraph(matrix);
                        for (int i = 0; i < this.matrix.getRow(); i++) {
                            for (int j = 0; j < this.matrix.getCol(); j++) {
                                final Index index = new Index(i, j);
                                Boolean boo = true;
                                for (Index a : seenIndexes) {
                                    if (a.equals(index)) {
                                        boo = false;
                                        break;
                                    }
                                }
                                if (this.matrix.getValue(index) == 1 && boo) {
                                    graph.setIndex(index);
                                    Callable<AbstractList<Index>> callable = () -> {
                                        AbstractList<Index> list = algorithm.traverse(graph);
                                        for (Index a : list) {
                                            if (!reachables.contains(a)) {
                                                reachables.add(a);
                                            }
                                        }
                                        return list;
                                    };
                                    TaskType type = TaskType.COMPUTATIONAL;
                                    TaskWrapper<AbstractList<Index>> taskWrapper =new TaskWrapper<>(callable,type);
                                    executor.execute(taskWrapper);
                                    HashSet<Index> hashSet = new HashSet<>(taskWrapper.get());
                                    indexList.add(hashSet);
                                    seenIndexes.addAll(hashSet);
                                }
                            }
                        }
                    }
                    // sending getReachables
                    System.out.println("server::getReachables:: " + indexList);
                    objectOutputStream.writeObject(indexList);
                    break;
                }
                case "GetAllPaths": {
                    Index start = (Index) objectInputStream.readObject();
                    System.out.println("Start Index:" + start);
                    Index end = (Index) objectInputStream.readObject();
                    System.out.println("End Index:" + end);
                    if (start.equals(end)) {
                        System.out.println("server::Start and End are the same indexes");
                        objectOutputStream.writeObject("Start and End are the same indexes");
                        break;
                    }
                    List<List<GraphNode<Index>>> indexList = Collections.synchronizedList(new ArrayList<>());
                    if (this.matrix != null) {
                        if (!(this.matrix.getValue(start) == 1 && this.matrix.getValue(end) == 1)) {
                            System.out.println("server::GetAllPaths::wrong indexes:: value is not 1 ");
                            objectOutputStream.writeObject("wrong indexes:: value is not 1");
                            break;
                        }
                        Traverse<Index> algorithm = new TraverseLogic<>();
                        MatrixAsGraph graph = new MatrixAsGraph(matrix);
                        graph.setIndex(start);
                        final AbstractList<Index> list = algorithm.traverse(graph);
                        Stack<GraphNode<Index>> path = new Stack<>();
                        boolean[][] seen = new boolean[matrix.getRow()][matrix.getCol()];
                        if (list.contains(end)) {
                            path.push(graph.getOrigin());
                            algorithm.bfs(indexList, graph, seen, path, end);
                            indexList.sort((Comparator.comparingInt(List::size)));
//                            indexList= indexList.stream().distinct().collect(Collectors.toList());

                            FileWriter myWriter = new FileWriter(new File("C:\\Users\\guygi\\IdeaProjects\\Assinment\\webapp", "filename"+ clientID +".txt"));
                                indexList.forEach(i-> {
                                    try {
                                        myWriter.write(i.toString()+"\n");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                myWriter.close();

                                objectOutputStream.writeObject("http://127.0.0.1:8000/filename"+clientID+".txt");

                        } else {
                            System.out.println("server::No possible routs have been found");
                            objectOutputStream.writeObject("No possible routs have been found");
                        }
                    }
                    break;
                }
                case "GetMinimumPaths": {

                    Index start = (Index) objectInputStream.readObject();
                    System.out.println("Start Index:" + start);
                    Index end = (Index) objectInputStream.readObject();
                    System.out.println("End Index:" + end);
                    if (start.equals(end)) {
                        System.out.println("server::Start and End are the same indexes");
                        objectOutputStream.writeObject(new ArrayList<>());
                        break;
                    }
                    List<List<Index>> indexList = new LinkedList<>();
                    if (this.matrix != null) {
                        if (!(this.matrix.getValue(start) == 1 && this.matrix.getValue(end) == 1)) {
                            System.out.println("server::GetMinimumPaths::wrong indexes:: value is not 1 ");
                            objectOutputStream.writeObject(new ArrayList<>());
                            break;
                        }
                        Traverse<Index> algorithm = new TraverseLogic<>();
                        MatrixAsGraph graph = new MatrixAsGraph(matrix);
                        graph.setIndex(start);
                        final AbstractList<Index> list = algorithm.traverse(graph);
                        Stack<Index> path = new Stack<>();
                        boolean[][] seen = new boolean[matrix.getRow()][matrix.getCol()];
                        //
                        if (list.contains(end)) {
                            indexList = algorithm.Shortest_PATHS(indexList, graph, seen, path, end);
                            indexList.sort((Comparator.comparingInt(List::size)));
                            indexList.forEach(System.out::println);
                            objectOutputStream.writeObject(indexList);

                        } else {
                            System.out.println("server::No possible routs have been found");
                            objectOutputStream.writeObject(new ArrayList<>());
                        }

                    }
                    break;
                }
                case "CheckSubmarines": {
                    if (this.matrix != null) {
                        executor = new ThreadPoolExecutor(
                                3, 5, 10,
                                TimeUnit.SECONDS, new LinkedBlockingQueue<>());
                        boolean[][] seen=new boolean[this.matrix.getRow()][this.matrix.getCol()];
                        Traverse<Index> algorithm = new TraverseLogic<>();
                        MatrixAsGraph graph = new MatrixAsGraph(matrix);
                        boolean check = true;
                        boolean onlyOne = false;
                        boolean badSurface = false;
                        int subCount=0;
                        for (int i = 0; i < this.matrix.getRow(); i++) {
                            for (int j = 0; j < this.matrix.getCol(); j++) {
                                final Index index = new Index(i, j);
                                if (this.matrix.getValue(index) == 1 && !seen[i][j]) {
                                    onlyOne =true;
                                    graph.setIndex(index);
                                    Callable<Boolean> booleanCallable = () -> algorithm.Sub(graph, seen);
                                    TaskType type =TaskType.COMPUTATIONAL;
                                    TaskWrapper<Boolean>  taskWrapper= new TaskWrapper<>(booleanCallable,type);
                                    executor.execute(taskWrapper);
                                    if (!taskWrapper.get()) {
                                        check =false;
                                    }
                                    subCount++;
                                }
                            }
                            if(!check){
                                badSurface =true;
                                check = true;
                                subCount -=1;
                            }
                        }
                        if(check && onlyOne && !badSurface){
                            System.out.println("Server::Correct Submarine surface!!!!");
                            objectOutputStream.writeObject("Correct Submarine surface!!!! Number of SubMarine: " + subCount);
                        }
                        else if(badSurface){
                            System.out.println("Incorrect Submarine surface!!!!");
                            objectOutputStream.writeObject("Incorrect Submarine surface!!!!Number of SubMarine: " + subCount);
                        }
                        else if(!onlyOne){
                            System.out.println("Server::Seems like you got a matrix with 0's on all indexes!!!!");
                            objectOutputStream.writeObject("Seems like you got a matrix with 0's on all indexes!!!!");
                        }

                    }
                    break;
                }
                default:{
                    System.out.println("Server::Error - please chose a correct case! ");
                    objectOutputStream.writeObject("Error - please chose a correct case!");

                }
            }

        }
    }
}