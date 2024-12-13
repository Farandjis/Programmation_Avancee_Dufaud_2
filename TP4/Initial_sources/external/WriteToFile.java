package external;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WriteToFile {
    public static void writeToFileWithSuffix(String suffix, String content) {
        // Construire le nom du fichier avec le suffixe
        String directory = "TP4/resultats/";

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = now.format(dateFormatter); // Formate la date et l'heure

        String filename = null;
        try {
            filename = directory + formattedDate + "_" + suffix +  "_" + getLH() + "_output.txt";
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        try (FileWriter writer = new FileWriter(filename, true)) {
            // Écriture du contenu dans le fichier
            // writer.write( now.format(dateFormatter) + ", " + suffix + ", " + content + "\n");
            writer.write( content + "\n"); // pour sauveur.txt
            System.out.println("Le fichier '" + filename + "' a été écrit avec succès.");
        } catch (IOException e) {
            // Gestion des erreurs d'écriture
            System.out.println("Une erreur est survenue lors de l'écriture dans le fichier.");
            e.printStackTrace();
        }
    }

    private static String getLH() throws UnknownHostException {

        final InetAddress addr = InetAddress.getLocalHost();
        String hostName = new String(addr.getHostName());
        return hostName;
    }
}
