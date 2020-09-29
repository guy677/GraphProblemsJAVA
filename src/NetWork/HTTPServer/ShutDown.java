package NetWork.HTTPServer;

public class ShutDown extends Thread {

    @Override
    public void run() {
        MyServer.shutDown();
    }
}