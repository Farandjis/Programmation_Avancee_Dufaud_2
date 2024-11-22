// Estimate the value of Pi using Monte-Carlo Method, using parallel program
package assignments;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
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
	public double getPi() {
		int nProcessors = Runtime.getRuntime().availableProcessors(); // runtime : c'est l'environnement pendant le temps d'exécution du code. Ici elle nous propose de regarder le nb processeurs dispo
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
		PiMonteCarlo PiVal = new PiMonteCarlo(100000); // On instancie MonteCarlo il fait 100 000
		long startTime = System.currentTimeMillis(); // On règle un timer
		double value = PiVal.getPi(); // on lance le code parallèle (c'est getPi())
		long stopTime = System.currentTimeMillis(); // on mesure le temps à la sortie
		System.out.println("Approx value:" + value); // affichage blablabla
		System.out.println("Difference to exact value of pi: " + (value - Math.PI));
		System.out.println("Error: " + (value - Math.PI) / Math.PI * 100 + " %");
		System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
		System.out.println("Time Duration: " + (stopTime - startTime) + "ms");
	}
}