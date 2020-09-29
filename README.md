# GraphProblemsJAVA



In this project I had to solve 3 graph problems:

The graph came as a matrix of 0s and 1s where 1 is a reachable node.

The Problems:
  1.Given a Matrix. return all the Connected vertices.(Multithreaded)
  Solution:
    DFS with theadlocal stack and hashset.
    each thread started from diffrent reachable node,if the node was'nt seen already.
  2.Matrix is given and indexes start and end. Return all paths between indexes without circles(Multithreaded)
    Solution:
      for this problem I created a new private class called ParallelDFS that implements Runnable.
      each time I come a cross a new(one that is not in my current path) reacheable node.
      I create new instant from ParallellDFS with this node as start node.
      then I put this instant in TheardPoolExecutor and continue to search reachable nodes.  
  3.Matrix is given and indexes start and end. Return all the shortest paths between indexes without circles(1 thread)
    Solution:
      for this problem I am first searching for shortest path useing A* algorithm with euclidean heuristic.
      then I am searching for all the paths.
      but when I am reaching to a path that is equal to the shortest path but is not full path i am droping it and continue to the next one.
      if I am reaching to shorter path I am droping all the paths that I found and continue with a new value.
