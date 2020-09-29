package NetWork.Server;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.*;

/**
 * This class represents a multi-threaded server
 */
public class TCPserver {
    private final int port;
    private volatile boolean stopServer;
    private ThreadPoolExecutor executor;
    private IHandler requestConcreteIHandler;

    public TCPserver(int port) {
        this.port = port;
        stopServer = false;
        executor = null;
    }

    public void run(IHandler concreteIHandlerStrategy) {
        this.requestConcreteIHandler = concreteIHandlerStrategy;
        Runnable mainLogic = () -> {
            try {
                executor = new ThreadPoolExecutor(
                        3, 5, 10,
                        TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
                ServerSocket server = new ServerSocket(port);
                server.setSoTimeout(1000);
                while (!stopServer) {
                    try {
                        Socket request = server.accept();
                        System.out.println("server::client!!!!");
                        Runnable runnable = () -> {
                            try {
                                System.out.println("server::handle!!!!");
                                requestConcreteIHandler.handle(request.getInputStream(),
                                        request.getOutputStream());
                                System.out.println("server::Close all streams!!!!");
                                // Close all streams
                                request.getInputStream().close();
                                request.getOutputStream().close();
                                request.close();
                            } catch (Exception e) {
                                System.out.println("server::"+e.getMessage());
                                System.err.println(e.getMessage());
                            }
                        };
                        executor.execute(runnable);
                    } catch (SocketTimeoutException ignored) {
                    }
                }
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        };
        new Thread(mainLogic).start();
    }
//for other implementations we have this stop method
    public void stop() {
        if (!stopServer) {
            stopServer = true;
            if (executor != null) {
                executor.shutdown();
            }
        }
    }

    public static void main(String[] args) {
        TCPserver tcpServer =new TCPserver(8010);
        tcpServer.run(new MatrixHandler());
    }


}