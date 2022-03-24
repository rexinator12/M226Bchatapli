import javax.imageio.IIOException;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientWork implements Runnable {
    public static ArrayList<ClientWork> clientWorks = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientWork(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientWorks.add(this);
            broadcastMessage("Server: " + clientUsername + " chat beigetreten");
        } catch (IIOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
                break;
            }
        }

    }

    public void broadcastMessage(String messageToSend) {
        for (ClientWork clientWork : clientWorks) {
            try {
                if (!clientWork.clientUsername.equals(clientUsername)) {
                    clientWork.bufferedWriter.write(messageToSend);
                    clientWork.bufferedWriter.newLine();
                    clientWork.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }
    }
    public void removeClientHandler(){
        clientWorks.remove(this);
        broadcastMessage("Server: " + clientUsername + " hat chat verlassen");
    }
    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
        removeClientHandler();
        try {
            if (bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
