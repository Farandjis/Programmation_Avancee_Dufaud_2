import external.WriteToFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Approximates PI using the Monte Carlo method.  Demonstrates
 * use of Callables, Futures, and thread pools.
 */
public class Pi 
{
    public static void main(String[] args) throws Exception 
    {
	long total=0;
	// 10 workers, 50000 iterations each
	total = new Master().doRun(50000, 10); // correspond au 2e pseudo code que l'on devait écrire
	System.out.println("total from Master = " + total);
    }
}

/**
 * Creates workers to run the Monte Carlo simulation
 * and aggregates the results.
 */
class Master {
    public long doRun(int totalCount, int numWorkers) throws InterruptedException, ExecutionException 
    {

	long startTime = System.currentTimeMillis(); // pour mesurer le temps

	// Create a collection of tasks
	List<Callable<Long>> tasks = new ArrayList<Callable<Long>>();
	for (int i = 0; i < numWorkers; ++i) 
	    {
		tasks.add(new Worker(totalCount)); // Ajoute des nouveaux Worker à notre tableau de tâche (nb workers = nb tâches)
	    }
    
	// Run them and receive a collection of Futures
	ExecutorService exec = Executors.newFixedThreadPool(numWorkers); // il prend un Collable (héritant d'un Runnable) qu'il associe à un threads
	List<Future<Long>> results = exec.invokeAll(tasks); // On créer une liste de Future (pour les lancés dans le futur) et on invoque tout le tableau de tâche (toutes les tâches (invokeAll))
	long total = 0;
    
	// Assemble the results.
	for (Future<Long> f : results)
	    {
		// Call to get() is an implicit barrier.  This will block
		// until result from corresponding worker is ready.
		total += f.get(); // on récupère le résultat dans le futur. Le programme est interrompu (je crois) en attendant le résultat -- On fait le get pour chaque tâche (pas sûr que ce soit des tâches)
	    }
	double pi = 4.0 * total / totalCount / numWorkers;

	long stopTime = System.currentTimeMillis();


	System.out.println("\nPi : " + pi );
	System.out.println("Error: " + String.format("%.10e",(Math.abs((pi - Math.PI)) / Math.PI)) +"\n");

	System.out.println("Ntot: " + totalCount*numWorkers);
	// System.out.println("Available processors: " + numWorkers);
	System.out.println("Available processors: " + numWorkers);
	System.out.println("Time Duration (ms): " + (stopTime - startTime) + "\n");

	System.out.println( (Math.abs((pi - Math.PI)) / Math.PI) +" "+ totalCount*numWorkers +" "+ numWorkers +" "+ (stopTime - startTime));

	String result = pi + ", " + String.format("%.10e",(Math.abs((pi - Math.PI)) / Math.PI)) + ", " + totalCount * numWorkers + ", " + numWorkers + ", " + (stopTime - startTime);
	WriteToFile.writeToFileWithSuffix("Pi", result);

	System.out.println(result);

	exec.shutdown();
	return total;
    }
}

/**
 * Task for running the Monte Carlo simulation.
 */
class Worker implements Callable<Long> 
{   
    private int numIterations;
    public Worker(int num) 
	{ 
	    this.numIterations = num; 
	}

  @Override
      public Long call() // code de MonteCarlo
      {
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
}
