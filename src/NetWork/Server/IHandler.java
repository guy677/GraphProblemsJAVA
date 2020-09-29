package NetWork.Server;

import java.io.*;

public interface IHandler {
    void handle(InputStream inClient, OutputStream outClient) throws IOException, ClassNotFoundException, Exception;
}