import javax.imageio.IIOException;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientWork implements Runnable {
    //wenn eine Client eine nachricht schickt, wird es auf diese array gespeichert einmal an alle clients gesendet
    public static ArrayList<ClientWork> clientWorks = new ArrayList<>();
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String clientUsername;

    public ClientWork(Socket socket) {
        try {
            this.socket = socket;
            //reader und writer sind dafür zuständig, dass nachrichten gelesen und versendet werden
            /* getOutputstream ist eine stream wo mit bit funkioniert. Um nachrichten
            zu senden muss man ein charakterstream haben. Deswegen wird es umgewandelt
             */
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //erste sting ist der name der clients
            this.clientUsername = reader.readLine();
            clientWorks.add(this);
            broadcastMessage("Server: " + clientUsername + " chat beigetreten");
        } catch (IIOException e) {
            closeEverything(socket, writer, reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //run wird als erstes ausgeführt. Es funktioniert auf eine separate thread
    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            //solange client verbunden ist, wird es wiederholt. Falls keine verbindung gibt, wird alles geschlossen
            try {
                messageFromClient = reader.readLine();
                broadcastMessage(messageFromClient);
                //wenn ein input/output Exception ensteht, wird alles geschlossen
            } catch (IOException e) {
                closeEverything(socket, writer, reader);
                break;
            }
        }

    }

    public void broadcastMessage(String messageToSend) {
        //hier wird eine loop erstellt damit alle client es erhalten
        for (ClientWork clientWork : clientWorks) {
            try {
                //es wird an allen geschickt ausser an sich selber
                if (!clientWork.clientUsername.equals(clientUsername)) {
                    clientWork.writer.write(messageToSend);
                    //buffered reader schlissen sich nicht von selber, deshalb flushen
                    clientWork.writer.newLine();
                    clientWork.writer.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, writer, reader);
            }
        }
    }
    public void removeClientHandler(){
        clientWorks.remove(this);
        broadcastMessage("Server: " + clientUsername + " hat chat verlassen");
    }
    /*bei diese konstruktor wird definiert was bei closeEverything passieren wird.
    Name wird von arraylist entfernt und Verbindung (socket) und bufferedreder/witer werden geschlossen
     */
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
