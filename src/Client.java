import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private  String username;
    //was der client braucht, verbindung und name
    public Client( Socket socket, String username) {
        try{
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, reader, writer);
        }
    }
    //gesende nachricht wird aufgenommen in array und an alle client geschickt
    public void sendMessage(){
        try {
            //clientWork wartet auf diese name wo dann von client eingegeben wird
            writer.write(username);
            writer.newLine();
            writer.flush();

            Scanner sc = new Scanner(System.in);
            // wenn client verbunden ist wird nachricht gelesen und an alle geschickt
            while (socket.isConnected()){
                String messageToSend = sc.nextLine();
                writer.write(username + ": " + messageToSend);
                writer.newLine();
                writer.flush();

            }
        } catch (IOException e) {
            closeEverything(socket, reader, writer);
        }
    }
    //gleichzeitig werden Nachrichten erhalten. Von separate thread ausgeführt
    public void getMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String FromGroupchat;
                while (socket.isConnected()){
                    try{
                        FromGroupchat = reader.readLine();
                        System.out.println(FromGroupchat);
                    } catch (IOException e) {
                        closeEverything(socket, reader, writer);
                    }
                }
            }
        }).start();//wird automatisch gestartet
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
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
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        //nach name abgefragt für arraylist
        System.out.println("Deine Nutzername: ");
        String username = sc.nextLine();
        //verbindung erstellt durch port
        Socket socket = new Socket("localhost", 1234);
        //client, getMessage, sendMessage erstellt
        Client client = new Client(socket, username);
        client.getMessage();
        client.sendMessage();
    }
}
