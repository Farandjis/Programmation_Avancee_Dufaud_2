package distributedMC_step1_javaSocket.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class GreetServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(int port) throws IOException {

        serverSocket = new ServerSocket(port); // il prend un port entrée
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true); // objet qui prend le flux de sorti. Tout ce qu'on écrit est le flux de sorti du client Socket
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // cette fois ci ce sont les flux de sorti des clients Socket
        String greeting = in.readLine();

        if ("hello server".equals(greeting)) { // si ce qu'on a reçu est bien "hello server"
            out.println("hello client"); // on lui envoi "hello client" (on lui répond donc à l'aide de notre object out)
        }
        else {
            out.println("unrecognised greeting"); // sinon, on dit qu'on a pas compris ce qu'il a dit
        }
    }

    public void stop() throws IOException { // pour arrêter
        in.close(); // on ferme le flux
        out.close(); // on ferme le flux
        clientSocket.close(); // on ferme le socket
        serverSocket.close(); // on ferme le socket
    }
    public static void main(String[] args) throws IOException {
        GreetServer server=new GreetServer();
        server.start(6666);
    }
}