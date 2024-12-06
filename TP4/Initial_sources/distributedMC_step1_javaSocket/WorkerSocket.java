package distributedMC_step1_javaSocket;

import java.io.*;
import java.net.*;
import java.util.Random;

/**
 * Worker is a server. It computes PI by Monte Carlo method and sends 
 * the result to Master.
 */
public class WorkerSocket {
    static int port; //default port
    private static boolean isRunning = true;
    
    /**
     * compute PI locally by MC and sends the number of points 
     * inside the disk to Master. 
     */

    public WorkerSocket(int parPort) {
        port = parPort;
    }

    public WorkerSocket() {
        port = 25545;
    }

    public static long PermettraDeCalculerPi(int numIterations){
        long circleCount = 0;
        Random prng = new Random ();
        for (int j = 0; j < numIterations; j++)
        {
            double x = prng.nextDouble();
            double y = prng.nextDouble();
            if ((x * x + y * y) < 1)  ++circleCount;
        }
        return circleCount;
    }

    public static void main(String[] args) throws Exception {

	if (!("".equals(args[0]))) port=Integer.parseInt(args[0]);
	System.out.println(port);
        ServerSocket s = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        Socket soc = s.accept();
	
        // BufferedReader bRead for reading message from Master
        BufferedReader bRead = new BufferedReader(new InputStreamReader(soc.getInputStream()));

        // PrintWriter pWrite for writing message to Master
        PrintWriter pWrite = new PrintWriter(new BufferedWriter(new OutputStreamWriter(soc.getOutputStream())), true);
	String str;
    String strWorker;
        while (isRunning) {
            str = bRead.readLine();          // read message from Master -- Il attend, il ne passera pas cette ligne tant qu'il n'a pas reçus de message.
            if (!(str.equals("END"))){
                System.out.println("Server receives totalCount = " +  str);

                // compute
                System.out.println("TODO : compute Monte Carlo and send total");
                strWorker = String.valueOf(PermettraDeCalculerPi(Integer.parseInt(str))); // str correspond à la valeur de Master pour calculer MC
                System.out.println("test nouveau str : " + strWorker); //strWorker correspond au résultat de PermettraDeCalculerPi que l'on transmet à Master
                // Master pourra faire le calcul de Pi de son côté
                pWrite.println(strWorker);         // send number of points in quarter of disk
            }
            else{
                isRunning=false;
            }
        }
        bRead.close();
        pWrite.close();
        soc.close();
   }
}