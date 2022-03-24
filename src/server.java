import javax.imageio.IIOException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class server {

    private ServerSocket serverSocket;

    public server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    public void startServer() throws IOException {
        try {
            while (!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("Neue Nutzer hat sich verbunden!");
                ClientWork clientWork = new ClientWork(socket);
                Thread thread = new Thread(clientWork);
                thread.start();


            }

        } catch (IIOException e) {
    }
}
public void closeServerSocket(){
    try{
        if (serverSocket != null){
            serverSocket.close();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
    public static void main (String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        server server = new server(serverSocket);
        server.startServer();
    }
}