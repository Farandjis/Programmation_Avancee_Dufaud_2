package external;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WriteToFile {
    public static void writeToFileWithSuffix(String suffix, String content) {
        // Construire le nom du fichier avec le suffixe
        String directory = "TP4/resultats/";
        String filename = directory + "output.txt";
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");

        try (FileWriter writer = new FileWriter(filename, true)) {
            // Écriture du contenu dans le fichier
            writer.write( now.format(dateFormatter) + ", " + suffix + ", " + content + "\n");
            System.out.println("Le fichier '" + filename + "' a été écrit avec succès.");
        } catch (IOException e) {
            // Gestion des erreurs d'écriture
            System.out.println("Une erreur est survenue lors de l'écriture dans le fichier.");
            e.printStackTrace();
        }
    }
}
