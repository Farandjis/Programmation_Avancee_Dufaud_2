package distributedMC_step1_javaSocket.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GreetClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port); // On connecte le client au serveur en lui disant où se trouve la socket (ip + port). L'adresse correspond à ce qu'on a dit au serveur.
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg); // on envoi le message
        // Note : dès qu'il y a readLine, on attend une réponse. On ne passe à la suite que losque l'on reçois une réponse.
        String resp = in.readLine(); // il attend la réponse du serveur et on la récupère. On sait qu'il va répondre, au vu de notre code (donc soit hello soit on ne comprend pas).
        return resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

}
