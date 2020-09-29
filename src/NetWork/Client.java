package NetWork;

import NetWork.Server.IHandler;
import NetWork.Structure.GraphNode;
import NetWork.Structure.Index;
import NetWork.Structure.Matrix;


import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class  Client {
    private final int port;
    private final String IP;
    private final int id;
    private IHandler requestConcreteIHandler;

    public Client(int id,int port, String ip) {
        this.port = port;
        this.id = id;
        IP = ip;
    }

    public void run() throws IOException, ClassNotFoundException {
        Socket socket =new Socket(IP,port);
        System.out.println("client::Socket");

        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        ObjectOutputStream toServer=new ObjectOutputStream(outputStream);
        ObjectInputStream fromServer=new ObjectInputStream(inputStream);
        Matrix m = new Matrix(5,5);

        toServer.writeObject("matrix");
        toServer.writeObject(this.id);
        toServer.writeObject(m);

        // sending #3 index for getAdjacentIndices
        toServer.writeObject("AdjacentIndices");
        toServer.writeObject( m.RandomI());
        // receiving #1 getAdjacentIndices
        Collection<Index> AdjacentIndices =
                new ArrayList<>((Collection<Index>) fromServer.readObject());
        System.out.println("client::getAdjacentIndices:: "+ AdjacentIndices);

        // sending #4 index for getReachables
//        Index start = (Index) m.RandomI();
//        Index end = (Index) m.RandomI();
        Index start = new Index(0,3);
        Index end = new Index(4,1);
        System.out.println("client::**** Run All Reachable Algorithm ***** ");
        toServer.writeObject("AllReachables");
//
//         receiving #2 getReachables
        Collection<Index> ReachablesIndices =
                new ArrayList<>((Collection<Index>) fromServer.readObject());
        System.out.println("client::ReachablesIndices:: "+ ReachablesIndices);
//
        toServer.writeObject("GetAllPaths");
        System.out.println("client::**** Run Get All Paths ***** ");
        toServer.writeObject(start);
        toServer.writeObject(end);
        String AllPathURl = (String) fromServer.readObject();
        System.out.println("client::AllPath:: At "+AllPathURl);

        System.out.println("client::**** Run Get Minimum Paths ***** ");
        toServer.writeObject("GetMinimumPaths");
        toServer.writeObject(start);
        toServer.writeObject(end);
        Collection<Index> n=
                new ArrayList<>((Collection<Index>) fromServer.readObject());
        System.out.println("client::MinimumPaths:: "+ n);


        System.out.println("client::**** Run Check Submarines Algorithm  ***** ");
        toServer.writeObject("CheckSubmarines");
        String submarineResult = (String) fromServer.readObject();
        System.out.println("client::Submarine Result:: "+submarineResult);


        toServer.writeObject("stop");

        System.out.println("client::Close all streams!!!!");
        fromServer.close();
        toServer.close();
        socket.close();
        System.out.println("client::Close socket!!!!");
    }


    public static void main(String[] args) throws Exception {
        for(int i =0;i<1;i++) {
            Client newClient = new Client(i, 8010, "127.0.0.1");
            newClient.run();
        }

    }


}
