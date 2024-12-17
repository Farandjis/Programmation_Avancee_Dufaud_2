// Estimate the value of Pi using Monte-Carlo Method, using parallel program
package assignments;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import external.WriteToFile;
import java.time.LocalTime;
class PiMonteCarlo {
	AtomicInteger nAtomSuccess;
	int nThrows;
	double value;
	class MonteCarlo implements Runnable {
		@Override
		public void run() {
			double x = Math.random(); // génération d'un nombre aléatoire, donc avec y => génération d'un point
			double y = Math.random(); // Si on précise une graine (seed) : le tirage est déterministe. Utile si on veut retester le code (donc en s'assurant qu'on obtient le même résultat)
			if (x * x + y * y <= 1)
				nAtomSuccess.incrementAndGet(); // On remarque que ça resemble à notre pseudo code avec machin++. C'est un msg envoyé à nAtomSuccess
		}
	}
	public PiMonteCarlo(int i) {
		this.nAtomSuccess = new AtomicInteger(0); // Initialise AtomicInteger à 0
		this.nThrows = i; // Throws signifie lancer. On peut IMAGINER que c'est le nombre de lancé
		this.value = 0; // peut-être que c'est Pi
	}
	public double getPi(int numWorkers) {
		int nProcessors = numWorkers; // runtime : c'est l'environnement pendant le temps d'exécution du code. Ici elle nous propose de regarder le nb processeurs dispo
		ExecutorService executor = Executors.newWorkStealingPool(nProcessors); // il fixe le nb de thread au nb de processeur détecté par la JVM (donc si dans le bios on a autorisé l'hyperthreading, alors il va dire que c'est 8 professeurs (8 coeurs en réalités), sinon 4)
		for (int i = 1; i <= nThrows; i++) {
			Runnable worker = new MonteCarlo();
			executor.execute(worker);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		value = 4.0 * nAtomSuccess.get() / nThrows;
		return value;
	}
}
public class Assignment102 {
	public static void main(String[] args) {
		// int totalCount = 50000;
		// int numWorkers = 20;
		final int[] listNumWorkers = {1, 2, 4, 8, 16, 32, 64};
		int totalCountParDefaut = 15000000;
		int tour = 50;
		String time = String.format("%02d%02d%02d", LocalTime.now().getHour(), LocalTime.now().getMinute(), LocalTime.now().getSecond());
		WriteToFile.writeToFileWithSuffix(time + "_Assigment102", "Error,Npoint,Pi,Nlance,tempsMilis,Nproc", true);



		// POUR SCALABILITÉ FORTE ======================================================================================
		for (int nbNWorker = 0; nbNWorker < listNumWorkers.length; nbNWorker++) {
			for (int nbTour = 0; nbTour < tour; nbTour++) {

				int numWorkers = listNumWorkers[nbNWorker];
				PiMonteCarlo PiVal = new PiMonteCarlo(totalCountParDefaut); // On instancie MonteCarlo
				long startTime = System.currentTimeMillis(); // On règle un timer
				double value = PiVal.getPi(numWorkers); // on lance le code parallèle (c'est getPi())
				long stopTime = System.currentTimeMillis(); // on mesure le temps à la sortie


				System.out.println("\nPi : " + value);
				System.out.println("Error: " + String.format("%.10e", (Math.abs((value - Math.PI)) / Math.PI)) + "\n");

				System.out.println("Ntot: " + totalCountParDefaut * numWorkers);
				System.out.println("Available processors: " + numWorkers);
				System.out.println("Time Duration (ms): " + (stopTime - startTime) + "\n");

				System.out.println((Math.abs((value - Math.PI)) / Math.PI) + " " + totalCountParDefaut + " " + numWorkers + " " + (stopTime - startTime));


				// String result = value + ", " + String.format("%.10e", (Math.abs((value - Math.PI)) / Math.PI)) + ", " + listTotalCount[nbTC] * numWorkers + ", " + numWorkers + ", " + (stopTime - startTime);
				String result = String.format(Locale.US, "%.10e", (Math.abs((value - Math.PI)) / Math.PI)) + "," + totalCountParDefaut + "," + value + "," + nbTour +  "," + (stopTime - startTime)  + "," + numWorkers; // avec sauveur.py de Florent
				WriteToFile.writeToFileWithSuffix(time + "_Assigment102", result, true);
				System.out.println(result);
			}
		}

		totalCountParDefaut = 1000000;
		tour = 50;
		// POUR SCALABILITÉ FAIBLE ======================================================================================
		for (int nbNWorker = 0; nbNWorker < listNumWorkers.length; nbNWorker++) {
			for (int nbTour = 0; nbTour < tour; nbTour++) {

				int numWorkers = listNumWorkers[nbNWorker];
				PiMonteCarlo PiVal = new PiMonteCarlo(totalCountParDefaut*numWorkers); // On instancie MonteCarlo il fait 100 000
				long startTime = System.currentTimeMillis(); // On règle un timer
				double value = PiVal.getPi(numWorkers); // on lance le code parallèle (c'est getPi())
				long stopTime = System.currentTimeMillis(); // on mesure le temps à la sortie


				System.out.println("\nPi : " + value);
				System.out.println("Error: " + String.format("%.10e", (Math.abs((value - Math.PI)) / Math.PI)) + "\n");

				System.out.println("Ntot: " + totalCountParDefaut* numWorkers);
				System.out.println("Available processors: " + numWorkers);
				System.out.println("Time Duration (ms): " + (stopTime - startTime) + "\n");

				System.out.println((Math.abs((value - Math.PI)) / Math.PI) + " " + totalCountParDefaut * numWorkers + " " + numWorkers + " " + (stopTime - startTime));


				// String result = value + ", " + String.format("%.10e", (Math.abs((value - Math.PI)) / Math.PI)) + ", " + listTotalCount[nbTC] * numWorkers + ", " + numWorkers + ", " + (stopTime - startTime);
				String result = String.format(Locale.US, "%.10e", (Math.abs((value - Math.PI)) / Math.PI)) + "," + totalCountParDefaut * numWorkers + "," + value + "," + nbTour +  "," + (stopTime - startTime)  + "," + numWorkers; // avec sauveur.py de Florent
				WriteToFile.writeToFileWithSuffix(time + "_Assigment102", result, true);
				System.out.println(result);
			}
		}


		/*
		System.out.println("Approx value:" + value); // affichage blablabla
		System.out.println("Difference to exact value of pi: " + (value - Math.PI));
		System.out.println("Error: " + (value - Math.PI) / Math.PI * 100 + " %");
		System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
		System.out.println("Time Duration: " + (stopTime - startTime) + "ms");
		*/
	}
}