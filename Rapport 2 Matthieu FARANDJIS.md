Matthieu FARANDJIS
INF3-FI

<div align="center">
<img height="95" width="400" src="https://www.uvsq.fr/medias/photo/iut-velizy-villacoublay-logo-2020-ecran_1580904185110-jpg?ID_FICHE=214049" title="logo uvsq vélizy"/>
<h1>Programmation Avancée & Qualité de développement</h1>
<h2>- Rapport n°2 -</h2>
</div>



# Sommaire

- [**I - Méthode de Monte-Carlo**](#p1)
- [**II - Algorithme et parallélisation**](#p2)
  - [a) Itération parallèle](#p2a)
  - [b) Master-Worker](#p2b)
- [**III - Mise en œuvre sur machine à mémoire partagée**](#p3)
  - [a) Analyse d'Assigment102](#p3a)
  - [b) Analyse Pi.java](#p3b)
- [**IV - Qualité et test de performance (cf R05.08 Q Dev)**](#p4)
  - [a) Mise en place](#p4a)
  - [b) Définitions des termes](#p4b)
  - [c) Etude sur le critère d'efficiency](#p4c)
  - [d) Etude sur le critère d'effictiveness pour Pi.java](#p4d)
  - [e) Etude sur le critère de satisfaction pour Pi.java](#p4e)
- [**V - Mise en œuvre en mémoire distribuée**](#p5)
  - [a) (Analyse) JavaSocket](#p5a)
  - [b) MasterWorker](#p5b)
- [**VI - Test de performance Master-Worker distribuée**](#p6)
- [**VIII - Mesures bonus sur Pi.java mais sans explication**](#p8)
<br><br><br><br><br><br><br><br><br>

____

## <a name="p1"></a> I - Méthode de Monte-Carlo

La méthode de Monte-Carlo permet de prédire un résultat à partir d'un ensemble de valeurs générées aléatoirement.<br>
Elle effectue plusieurs itérations pour recalculer et affiner le résultat.<br>
L'un des principes fondamentaux de la méthode est qu'une augmentation du nombre d'itérations améliore la précision de l'estimation.<br>
<br>
*Méthode d'après IBM : https://www.ibm.com/fr-fr/topics/monte-carlo-simulation* <br>
<br><br>
**Calcul de π par une méthode de MonteCarlo, d'après le TP4 fait en cours**<br>
Soit l'aire $`A_{\tfrac{1}{4}d}`$ d'un quart de disque de rayon $`r = 1`$.<br>

$$A_{\tfrac{1}{4}d} =  \frac{\pi r^2}{4} = \frac{\pi}{4}$$


<br><img src="img/figure1.png" width="300"/><br>
_**Figure 1 :** Tirage aléatoire dans un carré de côté r = 1._


Soit l'aire d'un carré de côté $`r=1`$, $`A_{c} = r^2 = 1`$ <br>
<br>
Soient les points $`X_{p}(x_{p},y_{p})`$ dont les coordonnées sont tirées selon une loi $`U (]0,1[)`$.<br>
La probabilité que $`X_{p}`$ soit tiré dans le quart de disque est

$$P = \frac{A_\frac{{1}{4}d}{A_{c}}} = \frac{\pi}{4}$$

On effectue $`n_{tot}`$ tirages aléatoires.<br>
Soit $`n_{cible}`$ le nombre de points tirés dans le quart de disque.<br>
<br>
Si $`n_{tot}`$ est rand, alors on peut approximer P pour $`P = \frac{n_{cible}}{n_{tot}} \approx \frac{\pi}{4}`$<br>
<br>
D'où $`\pi \approx 4 \times \frac{n_{cible}}{n_{tot}}`$<br>
<br><br>

**ALGORITHME : Implémentation séquentielle de la méthode de Monte-Carlo**<br>

<img src="img/algo_mc.png" width="500"/>


- On identifie deux
  - T0 : Tirer et compter $`n_{tot}`$ points
  - T1 : Calculer $`\pi`$
- T0 se décompose en $`n_{tot}`$ sous tâches
  - $`T_{0P1}`$ : tirer $`X_{p}`$
  - $`T_{0P2}`$ : incrémenter $`n_{cible}`$
- *Dépendance entre tâches :*
  - T1 dépend de T0
  - $`T_{0P2}`$ dépend de $`T_{0P1}`$
  - Les $`T0P1`$ sont indépendats selon p
  - Les $`T0P2`$ sont indépendats selon p


<br><br><br><br>
___ 
## <a name="p2"></a> II - Algorithme et parallélisation

### <a name="p2a"></a> a) Itération parallèle

L'itération parallèle est aussi appelée parallélisme de boucle et parallélisme itératif.<br>
Dans un algorithme parallèle, on suppose que le calcul effectué par une unité de calcul est indépendant de celui effectué par une autre unité de calcul.<br>
L'adjectif "parallèle" attribué à un tel algorithme provient du fait que cela correspond à une architecture de type SIMD (une Seule Instruction, Plusieurs Données).<br>
*Source :*
  - *Explications adaptés pour Assigment102 :*<br>
    https://dpt-info.u-strasbg.fr/~cronse/TIDOC/ALGO/parseq.html


<br>
L'algorithme à itération parallèle de la méthode de Monte-Carlo que nous avons étudié est Assignment102.<br>
<br>

Voici un extrait du code d'Assigment102 :<br>
```
	class MonteCarlo implements Runnable {
		@Override
		public void run() {
			double x = Math.random(); // génération d'un nombre aléatoire, donc avec y => génération d'un point
			double y = Math.random(); // Si on précise une graine (seed) : le tirage est déterministe. Utile si on veut retester le code (donc en s'assurant qu'on obtient le même résultat)
			if (x * x + y * y <= 1)
				nAtomSuccess.incrementAndGet(); // On remarque que ça resemble à notre pseudo code avec machin++. C'est un msg envoyé à nAtomSuccess
		}
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
```
<br><br><br>

### <a name="p2b"></a> b) Master-Worker

Le premier algorithme Master-Worker de la méthode de Monte-Carlo que nous avons étudié est Pi.java.<br>
Master crée puis lance des tâches qui, via le paradigme des futures, font une résolution de dépendance.<br>
<br>
Pi.java est plus efficace que Assignment102, puisque si l'on crée 500 000 tâches, l'OS gère lui-même celles-ci, ce qui prend plus de temps que de faire :
génération nombre aléatoire x, génération nombre aléatoire y, test, incrément (~40 cycles).<br>
<br><br>
Il est difficile de montrer un exemple du code calculant Monte-Carlo, mais nous pouvons le visualiser via le code disponible sur ce dépôt.<br>


<br><br><br><br>
___ 

## <a name="p3"></a> III - Mise en œuvre sur machine à mémoire partagée

### <a name="p3a"></a> a) Analyse d'Assigment102

Le code de Assignment102 calcule une valeur approximative de 𝜋 à partir de la méthode de Monte-Carlo.<br>
On y retrouve deux dépendances nouvelles : AtomicInteger et Executor.<br>

- **Atomic Integer :**<br>
  Encapsule une valeur entière qui peut être mise à jour de manière atomique.<br>
  C'est-à-dire que toutes les opérations de lecture-modification-écriture sur cette valeur sont effectuées comme une seule unité insécable. Cela garantit la sécurité des threads (thread-safety) sans nécessiter de synchronisation explicite.<br>
  Un AtomicInteger est utilisé dans les applications telles que les compteurs incrémentés atomiquement et ne peut pas être utilisé comme remplacement d’un java.lang.Integer.<br>  <br>
  *Sources :*
  - *https://www.jmdoudoux.fr/java/dej/chap-acces_concurrents.htm*
  - *https://learn.microsoft.com/fr-fr/dotnet/api/java.util.concurrent.atomic.atomicinteger?view=net-android-34.0*
  - *ChatGPT (la phrase du "C'est-à-dire")*


- **Executor :**<br>
  L'interface java.util.concurrent.Executor décrit les fonctionnalités permettant l'exécution différée de tâches implémentées sous la forme de Runnable.<br>
  C’est un support pour les threads en Java à un niveau plus élevé que la classe Thread.<br>
  Elle permet de découpler la soumission des tâches de la gestion des threads (exécution, ordonnancement).<br>
  On peut ici gérer des pools de threads. Chaque thread du pool peut être réutilisé dans un Executor.<br>
  L’interface Executor définit la méthode execute.<br>
  <br>
  *Sources :*
  - *https://jmdoudoux.developpez.com/cours/developpons/java/chap-executor.php#executor-1*
  - *Cours de M. DUFAUD : CM4-complement-parallelisation-et-Java.pdf*


- **Algorithme Workstealing (abordé en cours) :**<br>
  Le principe de cet algorithme est que chaque worker possède une liste de tâches.<br>
  Lorsqu'un worker n'en possède plus, plutôt que d'attendre de nouvelles tâches, il va en extraire chez un autre worker.<br>
  <br>
  De cette manière, non seulement chaque worker a à peu près le même nombre de tâches, mais on s'assure également que les workers restent toujours actifs.<br>
  On garantit donc l'optimisation du fonctionnement du programme.<br>*Sources :*
  - https://www.linkedin.com/advice/0/what-benefits-drawbacks-using-work-stealing?lang=fr&originalSubdomain=fr


- **Les futures :**<br>
  Le paradigme de programmation est très pratique car il permet de définir, dès la création de la tâche, le schéma de dépendance entre les tâches.<br>
  L'idée est de dire que la tâche va envoyer un résultat dans le futur. On ne sait pas quand elle va l'envoyer, mais plus loin dans le code, on va utiliser ce résultat.<br>
  On dit que la tâche x va envoyer un résultat y dans le temps. La tâche s'exécute, d'autres opérations s'exécutent en parallèle, puis à un moment donné, x renvoie le résultat et un calcul est effectué.<br>
  - À ce moment-là, pour effectuer le calcul, le code doit attendre le résultat de x, c'est un point de synchronisation.<br>
<br><br>
On définit un objet executor qui est un support de thread.<br>
    On va lui dire : tu définis le nombre de threads que tu vas utiliser.<br>
    -> Il permet de faire l'association entre des tâches et des threads de manière dynamique, en fonction de l'exécution des tâches en cours.<br>
    -> Il est donc possible d'associer 1000 tâches à 4 threads (au lieu de 1000 tâches = 1000 threads dans le TP mobile, vu qu'on ne pouvait pas utiliser Executor).<br>
    <br>
    ===> On utilise désormais la classe Executor, plutôt que la classe Thread.<br>
    Les threads en Java présentent de nombreux inconvénients, essentiellement liés au fait que cette classe est de bas niveau.<br>
    De ce fait, elle ne propose pas des fonctionnalités telles que l'obtention d'un résultat d'exécution d'un thread ou encore l'attente de la fin d'un ensemble de threads.<br>
<br>
*Sources :*<br>
*https://jmdoudoux.developpez.com/cours/developpons/java/chap-executor.php#executor-1*

<br><br>
**Note :**
- start et run sont deux choses différentes
- V est un type générique
- overhead : ordonnanceur de l'OS

<br><br>
**Quelques annotations sur le code :**<br>
- **double y = Math.random();**
  - Si on précise une graine (seed) : le tirage est déterministe. Utile si on veut retester le code (donc en s'assurant qu'on obtient le même résultat)
- **int nProcessors = Runtime.getRuntime().availableProcessors();**
  - runtime : c'est l'environnement pendant le temps d'exécution du code. Ici elle nous propose de regarder le nb processeurs dispo
- **Executors.newWorkStealingPool(nProcessors);**
  - il fixe le nb de thread au nb de processeur détecté par la JVM (donc si dans le bios on a autorisé l'hyperthreading, alors il va dire que c'est 8 professeurs (8 coeurs en réalités), sinon 4)
<br><br>

Voici un diagramme UML d'Assigment102 :<br>
<img src="img/uml_assigment102.png" width="600"/><br>

*Les outils suivants m'ont aidé : StarUML, ChatGPT*<br>

<br><br><br>

### <a name="p3b"></a> b) Analyse Pi.java


**Paradigme de Pi.java :** Master Worker


Voici la version corrigée de ton texte :

On a un Master qui va créer puis lancer des tâches qui, via le paradigme des futures, effectuent une résolution de dépendance.<br>
<br>

Voici un schéma montrant l'interraction entre 1 Master et 3 Workers : <br>
<img src="img\schema_dufaud_1M_3W_1M.png" width="600"/><br>

*Les outils suivants m'ont aidé : Excalidraw*<br>


<br><br>
Pi.java est plus efficace que Assignment102, puisque si l'on crée 500 000 tâches, l'OS gère lui-même celles-ci, ce qui prend plus de temps que de faire :
génération du nombre aléatoire x, génération du nombre aléatoire y, test, incrément (~40 cycles).<br>
<br><br>
**Quelques annotations sur le code :**<br>
- **Executors.newFixedThreadPool(numWorkers)**
  - Renvoie une instance de type ExecutorService qui utilise un pool de threads dont la taille est fixe. Les tâches à exécuter sont stockées dans une queue.
<br><br>

Voici un diagramme UML de Pi.java :<br>
<img src="img/uml_pi.java.png" width="600"/><br>

*Les outils suivants m'ont aidé : StarUML, ChatGPT*<br>

<br><br>
Avec le modèle l'utilisation du paradigme Master-Worker, nous pouvons facilement répartir la partie Worker sur une autre machine avec quelque modification.<br>
De ce fait, Pi.java satisfait le critère "flexible" du Context Coverage de la Quality in use.<br>


<br><br><br><br>
___ 
## <a name="p4"></a> IV - Qualité et test de performance (cf R05.08 Q Dev)


Dans cette partie et les parties qui suivent, nous utilisons la norme ISO_IEC_25010_2011.


### <a name="p4a"></a> a) Mise en place


Voici la version corrigée et améliorée de ton texte :

De prime abord, Assignment102 et Pi.java affichent les résultats de manière différente. Afin de mieux comparer et étudier leurs efficacités, nous avons dû uniformiser les sorties.<br>
C'est-à-dire s'assurer que chaque code affiche/renvoie la même chose.<br>
<br><br>
Ainsi, les prints ci-dessous sont dans le même format, que ce soit dans Assignment102 ou Pi.java.<br>

<img src="img\uni_sorties.png" width="1000"/><br>
*Code de Pi.java*<bR>
<br><br>
Pour traiter ces informations et faciliter l'automatisation du lancement du code plusieurs fois d'affilée, nous allons sauvegarder ces informations dans un document.<br>
Pour cela, nous allons créer une classe WriteToFile :<br>
<img src="img\wtf.png" width="600"/><br>
<br><br>
Comme nous pouvons le voir, nous créons en premier un nom pour notre fichier.<br>
Nous indiquons le jour du test, ainsi que la machine sur laquelle nous testons (grâce à InetAddress.getLocalHost()), sachant que le nom de chaque machine de l'IUT correspond à la salle et à sa position dans la salle.<br>
C'est indispensable car les résultats peuvent varier d'une machine à l'autre.<br>
Pour de meilleurs résultats, dans les faits, il faudrait même fermer tous les logiciels ouverts, voire même utiliser le terminal plutôt qu'IntelliJ et, dans l'idéal, ne pas utiliser l'interface graphique de Windows.<br>
<br>
Ensuite, nous écrivons à la suite du fichier les données reçues, nous sauvegardons et affichons si tout s'est bien passé ou non.<br>

<br><br>
Enfin, nous allons traiter ces fichiers via un programme Python afin d'établir un graphique et étudier les résultats.<br>
Plus précisément, nous allons étudier la scalabilité forte et faible d'Assignment102 et Pi.java sur les critères de temps d'exécution et de speed-up.<br>

<br><br><br>

### <a name="p4b"></a> b) Définitions des termes
- **Speed-up :**<br>
  Le speed-up, noté Sp, est le gain de vitesse d’exécution en fonction du nombre de processus P.<br>
  L'idée est donc de mesurer le gain de performance obtenu en exécutant une tâche sur plusieurs processeurs (ou cœurs) par rapport à un seul processeur.<br>
  <br>
  Il se calcul de cette manière : $`S_p = \frac{T_1}{T_p}`$ : <br>
  Avec :
  - $`S_p`$ le speedup 
  - $`T_1`$ le temps d’éxécution sur un processus
  - $`T_P`$ le temps d’éxécution sur P processus


- **Temps d'exécution**<br>
  Le temps d'exécution correspond au temps que demande le programme pour effectuer le calcul de π.<br>
  Dans le cas du paradigme Master-Worker, le temps d'exécution correspond au temps des échanges entre le Master et les Workers, au temps que demandent les Workers pour calculer, ainsi qu'au temps nécessaire pour assembler le résultat final par le Master.


- **Scalabilité forte :**<br>
  La scalabilité forte consiste à étudier ce qu'il se passe lorsque l'on ajoute des processus pour un problème de taille fixe.


- **Scalabilité faible :**<br>
  La scalabilité faible consiste à étudier ce qu'il se passe lorsqu'on augmente simultanément la taille du problème et le nombre de processus.

<br><br><br>

### <a name="p4c"></a> c) Etude sur le critère d'efficiency
L'objectif de cette étude est de prouver quel est le meilleur paradigme pour calculer π à l'aide de la méthode de Monte-Carlo, entre Assignment102 et Pi.java.<br>
Nous étudons donc l'efficiency des programmes : quel est le programme le plus efficace en terme de temps, d'utilisation des ressources et de marge d'erreur.<br>
<br>
Pour rappel :
- Paradigme d'Assignment102 : Itération parallèle
- Paradigme de Pi.java : Master-Worker

<br><br>

- Les tests suivant ont été effectués sur un des ordinateurs de la rangée de droite de la salle G24, voici sa configuration :
  - Processeur : Intel Core i7-9700 3.00GHz - 8 coeurs - DDR4-2666
    - https://www.intel.fr/content/www/fr/fr/products/sku/191792/intel-core-i79700-processor-12m-cache-up-to-4-70-ghz/specifications.html
  - RAM : 32Go
  - Windows 11 Pro 23H2, build : 22631.4169
  - Attention, certains logiciel fonctionnaient en fond, par ailleurs l'interface graphique de Windows était démarré. Cela influe sur les performances de l'ordinateur.



- Information :
  - **Programme utilisé :** analyseur/analyseur_scalabilités.py
  - **Fichier étudié pour Pi.java :** 17-12-2024_121434_Pi-java_G24-5_output.txt
  - **Fichier étudié pour Assignment102 :** 16-12-2024_112105_Assigment102_G24-5_output.txt


**Résultats obtenus :**
<img src="img\etude_sca.png"><br>

<br><br>

- **Expérience n°1 en Scalabilité Forte : Est-ce que le temps d’exécution diminue lorsque j’ajoute des processus pour un problème de taille fixe ?**
  <img src="img\etude_sca_e1.png"><br>
  Nous remarquons que contrairement à Assignment102, pour Pi.java, le temps d'exécution diminue.<br>
  Cela s'explique par le fait que l'on divise la taille du problème entre les processus. Vu que les processus traitent des problèmes de plus en plus petits, c'est très rapide bien qu'ils soient nombreux.<br>
  En revanche, pour Assignment102, nous donnons un problème de même taille à chaque processus, donc ça ne peut qu'augmenter. Cependant, nous remarquons que ça augmente de plus en plus lentement.<br>
  <br>
  Pi.java est donc le gagnant de cette expérience.<br>

<br>

- **Expérience n°2 en Scalabilité Forte : Comment les ressources sont utilisés lorsque j'ajoute des processus pour un problème de taille fixe ?**
  <img src="img\etude_sca_e2.png"><br>
  
  La courbe bleue correspond au speed-up idéal en fonction du nombre de processus.<br>
  Cela signifie que dans l'idéal, si la méthode est parfaitement parallèle, le fait de doubler le nombre de processus divise par deux le temps de calcul. En d'autres termes : il y a une excellente répartition des ressources.<br>
  La courbe du speed-up idéal augmente rapidement puisque chaque processus est censé s'exécuter plus rapidement à chaque ajout (vu que le totalCount attribué diminue à chaque ajout).<br>
  Si le point de notre mesure est au-dessus du speed-up idéal, c'est que le processus résout plus rapidement le problème que dans la logique ; sinon, si c'est en dessous, c'est qu'il met plus de temps.<br>
  <br>
  - **Pi.java**<br>
    Nous remarquons que le speed-up suit le speed-up idéal au début lorsque l'on utilise de 1 à 8 processus, puis augmente très lentement de 8 à 16 processus avant de stagner.<br>
    Cela signifie que Pi.java est efficace lorsque l'on utilise un petit nombre de processus.<br>
    Après cela, on peut déduire que l'interaction de plus en plus importante entre le Master et les Workers fait perdre de l'efficacité au programme. Ce temps de traitement de plusieurs processus par la machine s'appelle "overhead", nous en parlons en partie V a).<br>
  - **Assignment102**<br>
    C'est la catastrophe ! Même avec plus de processus, le résultat est mauvais : cela tend vers 0.<br>
    Cela ressemble à une courbe de scalabilité faible (voir expérience n°4), mais c'est bien le résultat de notre expérience en scalabilité forte.<br>
    On en déduit une très mauvaise répartition de la charge de travail entre les processus, chose que nous avons déjà remarquée dans l'expérience n°1.<br>
    <br>
    Par conséquent, Pi.java est de nouveau vainqueur : cet algorithme est plus efficace qu'Assignment102.<br>

<br>

- **Expérience n°3 en Scalabilité Faible : Est-ce que le temps d’exécution diminue lorsque j’augmente simultanément la taille du problème et le nombre de processus ?**
  <img src="img\etude_sca_e3.png"><br>
  Nous remarquons que dans les deux cas, le temps d'exécution augmente. Cependant, ici également, Pi.java reste le plus efficace : il augmente beaucoup moins vite qu'Assignment102 si l'on se fie à l'axe des ordonnées.<br>
  Nous remarquons également que c'est une courbe linéaire, ce qui est normal puisque le problème grandit proportionnellement au nombre de processus.<br>
  Il est à noter que dans le cas de Pi.java, de 1 à 8 processus, le temps est constant. Ça augmente dès qu'il y a plus de 8 processus.<br>
  <br>
  Même lors d'une étude de scalabilité faible, Pi.java est mieux qu'Assignment102.<br>

<br>

- **Expérience n°4 en Scalabilité Faible : Comment les ressources sont utilisés lorsque j’augmente simultanément la taille du problème et le nombre de processus ?**
  <img src="img\etude_sca_e4.png"><br>
  Ici, le speed-up idéal est constant. Bien qu'on augmente le totalCount général, chaque processus calcule le même totalCount et donc, par conséquent, prend le même temps d'exécution dans l'idéal.<br>
  <br>
  Notre observation rejoint l'observation sur l'expérience n°2 : augmenter le nombre de processus ne permet pas toujours d'avoir un bon speed-up, même si la taille du problème augmente proportionnellement.<br>
  En effet, comme dans l'expérience n°2, de 1 à 8, la courbe suit le speed-up idéal. Après cela, le coût des communications entre le Master et les Workers influe trop sur les performances.<br>
  De même que dans l'expérience n°2, Assignment102 s'effondre rapidement.<br>
  <br>
  Seulement, je me demande si Pi.java ne finira pas par stagner comme Assignment102 lorsque l'on compare l'allure des deux courbes.<br>
  <br>
  La conclusion reste la même : Pi.java est mieux qu'Assignment102.<br>
  <br><bR>

**Conclusion**<br>
À l'issue de ces quatre expériences, la conclusion est sans appel : le paradigme Master-Worker offre de meilleurs résultats avec la méthode de Monte-Carlo que le paradigme d'itération parallèle.<br>
À partir de là, nous pouvons étudier les taux d'erreur de Pi.java sous la forme d'une expérience n°5.<br>

<br><br>
- **Expérience n°5 : Taux d'erreurs par rapport au nombre de points calculés par Pi.java**
  <img src="img\etude_sca_e5.png"><br>
  <br>
  Légende :
  - **Points rouges :** Représente le taux d'erreur "Error" pour chaque calcul, c'est-à-dire chaque ligne du fichier pi.txt
  - **Points noir :** Représente la médiane des taux d'erreurs 
  - **ordonnée :** le taux d'erreur
  - **abscisse :** Le nombre de processus Nproc en millions (donc 1M = 1000000)
  <br><br>

  Information :
  - **Programme utilisé :** analyseur/analyseur_erreurs.py
  - **Fichier étudié :** 16-12-2024_215711_Pi-java_DESKTOP-9GESL6B_output.txt
  - **Fichier étudié partie Scalabilité Forte :** 16-12-2024_215711_Pi-java_DESKTOP-9GESL6B_output__pi_scalabilité_forte.txt
  - **Fichier étudié partie Scalabilité Faible :** 16-12-2024_215711_Pi-java_DESKTOP-9GESL6B_output__pi_scalabilité_faible.txt
  
  <bR><br>
  Nous remarquons que lors de notre étude de la scalabilité faible, plus il y avait de points totaux (totalCount), plus l'algorithme est fiable.<br>
  Il est à noter que chaque processus calcule "totalCount / nbProcessus". Cela signifie dans notre cas que peu importe le nombre de processus, chaque processus calcule Pi pour totalCount = 1 000 000.<br>
  Seulement, à la fin, cela fait bien 1M * nbProcessus, ce que nous représentons en abscisse dans le graphique.<br>
  <br>
  Il faut bien comprendre qu'on répartit le totalCount d'origine entre les processus en amont de l'attribution de la valeur au Worker lors de sa création. Sinon, le calcul de Pi est faux (on aurait fait deux fois une division par nbProcessus).<br>

<br><br><br>


### <a name="p4d"></a> d) Etude sur le critère d'effictiveness pour Pi.java

Même si Pi.java est performant et que son taux d'erreur diminue plus on augmente le totalCount... Quand est-il de son **effectiveness** ?<br>
Nous allons donc étudier l'**effectiveness** de Pi.java, c'est-à-dire sa capacité à résoudre le problème parfaitement sur le critère suivant :<br>
<br>
**Est-ce que Pi.java permet de calculer le nombre Pi sur $`10^{-3}`$ ?**<br>
<br>
La valeur de Pi pour $`10^{-3}`$ est : **3,141**<br>
Nous allons donc vérifier si lors de nos calculs, Pi.java renvoie bien cette valeur. Nous tiendrons uniquement compte des 3 premiers chiffres après la virgule.<br>
<br>
Le tableau ci-dessous correspond au bilan de la vérification depuis le fichier pi.txt.<br>
Dans la colonne "OK", nous indiquons le nombre de fois où nous obtenons 3,141 comme valeur. Il en va de même pour "HS", dans le cas où le résultat est invalide.<br>

<br>

**Scalablité Forte**

| Nlance | Nproc | OK    | HS    |
|--------|-------|-------|-------|
| 15M    | 1     | 19/25 | 06/25 |
| 15M    | 2     | 19/25 | 06/25 |
| 15M    | 4     | 17/25 | 08/25 |
| 15M    | 8     | 18/25 | 07/25 |
| 15M    | 16    | 18/25 | 07/25 |
| 15M    | 32    | 17/25 | 08/25 |
| 15M    | 64    | 18/25 | 07/25 |

<br>

**Scalablité faible**

| Nlance | Nproc | OK    | HS    |
|--------|-------|-------|-------|
| 1M     | 1     | 8/25  | 17/25 |
| 2M     | 2     | 12/25 | 13/25 |
| 4M     | 4     | 14/25 | 11/25 |
| 8M     | 8     | 17/25 | 08/25 |
| 16M    | 16    | 22/25 | 03/25 |
| 32M    | 32    | 24/25 | 01/25 |
| 64M    | 64    | 25/25 | 00/25 |


<br><br>
Dans le cas de la scalabilité forte, majoritairement l'algorithme trouve 3,141. On est à 6 à 8 ratés pour 25 tentatives au total, soit un taux de succès de 72 % en moyenne. L'ajout de processus supplémentaires n'améliore pas le résultat.<br>
Dans le cas de la scalabilité faible en revanche, plus il y a de processus, mieux nous trouvons 3,141 ! Le résultat est catastrophique pour Nproc = 1 et excellent pour Nproc = 64.<br>
Cela correspond à ce qu'on a vu comme résultat à l'expérience n°5.<br>
<br>
En conclusion, pour calculer 3,141, l'effictiveness de Pi.java est excellente à partir de Nproc = 64 pour totalCount = 64M dans le cas de la scalabilité faible.<br>
Il vaut donc mieux utiliser Pi.java en scalabilité faible qu'en scalabilité forte.<br>

<br><br><br>

### <a name="p4e"></a> e) Etude sur le critère de satisfaction pour Pi.java
Maintenant, nous allons vérifier que Pi.java correspond bien à nos attentes en tant que client.<br>
<br>
- **Usefulness : est-ce que le programme est utile ?**<br>
  Dans notre cas, le programme est utile pour faire notre étude.<br>
  En général, l'algorithme de Monte-Carlo est utilisé pour modéliser des systèmes incertains ou aléatoires dans divers domaines tels que la finance, la physique statique et la biologie (selon ChatGPT).<br>


- **Trust : est ce que le programme respecte la norme ?**<br>
  Pour assurer notre confiance envers le programme, nous devons vérifier que le générateur de nombres aléatoires génère bien des nombres aléatoires indépendants entre chaque processus.<br>
  <br>
  Le générateur de nombres aléatoires se trouve dans la boucle for de la méthode `call` de la classe `Worker`.<br>
  Nous allons donc écrire dans un fichier texte les valeurs enregistrées pour quel Worker (son id) et pour quel tour (la variable j). Nous indiquerons dans un nouvel attribut "id" de la classe le numéro du Worker.<br>
  Afin de distinguer les exécutions du programme, nous indiquerons juste avant les critères de lancement, tels que le nombre de Workers ou le totalCount.<br>
  <br>
  L'expérience qui suit a été réalisée sur mon ordinateur avec la configuration suivante :<br>

  - **Fichier :** Pi.java<br>
    - En : Scalabilité forte<br>
    - Nombre de processus : 1, 2, 4, 8<br>
    - totalCount : 15 000 000<br>
  - **PC :**<br>
    - Processeur : Intel Core i5-9400F, 2.90 GHz, 6 cœurs, 1 socket<br>
    - RAM : 8 Go<br>
    - Windows 10 Home 22H2, build : 19045.5247<br>
    - Attention, certains logiciels fonctionnaient en arrière-plan. Par ailleurs, l'interface graphique de Windows était démarrée. Cela influe sur les performances de l'ordinateur.<br>

  <br><br>
  Voici une sélection du fichier `17-12-2024_Pi-java_trust_DESKTOP-9GESL6B_output.txt`. Notre analyse se fondera dessus :<br>

  ```
  =========== tour n°4 Nworker = 2 totalCount = 7500000 -- heure : 205745
    --[W0 - tour 0]-->(0.6081051753721731, 0.6047682773649212)
    --[W1 - tour 0]-->(0.7278271711041955, 0.6775989547714912)
    --[W0 - tour 1]-->(0.20780501411925578, 0.8716607124040157)
    --[W1 - tour 1]-->(0.5941601270829872, 0.061907042047551264)
    --[W0 - tour 2]-->(0.05355968858713944, 0.7126668019026495)
    --[W1 - tour 2]-->(0.887138018183691, 0.20071311431511185)
    --[W0 - tour 3]-->(0.24020044011396913, 0.07554104600422717)
    --[W1 - tour 3]-->(0.3866213958047845, 0.5359512604793124)
    --[W0 - tour 4]-->(0.3227026009876759, 0.37004047777817484)
    --[W1 - tour 4]-->(0.45311327236889853, 0.2326071153151319)

  =========== tour n°0 Nworker = 4 totalCount = 3750000 -- heure : 205745
    --[W0 - tour 0]-->(0.8025528982424484, 0.2226773653722971)
    --[W1 - tour 0]-->(0.3248899870176598, 0.04926276744406999)
    --[W2 - tour 0]-->(0.5225164923121788, 0.6900527962622921)
    --[W3 - tour 0]-->(0.43812046529937565, 0.07867120537337235)
    --[W1 - tour 1]-->(0.7862151186163597, 0.36044700594397694)
    --[W0 - tour 1]-->(0.4851773188977315, 0.6621674785597783)
    --[W2 - tour 1]-->(0.006746046782472925, 0.6354973634446587)
    --[W3 - tour 1]-->(0.4077618482875708, 0.6449175124991942)
    --[W1 - tour 2]-->(0.04196849409072456, 0.088908523700715)
    --[W0 - tour 2]-->(0.2926212471609845, 0.7998970075375)
    --[W2 - tour 2]-->(0.63435248239826, 0.8108491937973279)
    --[W3 - tour 2]-->(0.8324205636569904, 0.47053779689037745)
    --[W1 - tour 3]-->(0.2730817545547871, 0.2964243338384539)
    --[W0 - tour 3]-->(0.5155221502509648, 0.8220765104699722)
    --[W2 - tour 3]-->(0.6719261799865358, 0.963865894251568)
    --[W3 - tour 3]-->(0.12983509879140642, 0.7102248715438713)
    --[W1 - tour 4]-->(0.461360743383905, 0.9939536120283735)
    --[W0 - tour 4]-->(0.9480406778244539, 0.4022095903943069)
    --[W2 - tour 4]-->(0.9439628526362639, 0.9553383219811445)
    --[W3 - tour 4]-->(0.38479681908071495, 0.16050873876455507)
  ```

  Ces deux appels au programme se différencient par le nombre de Worker et le totalCount qui a augmenté entre temps.<br>
  Ces données sont involontairement classées par l'itération de la variable `j` dans la boucle for. Cela simplifie nos comparaisons.<br>
  <br>
  Nous remarquons que sur un même tour, aucun Worker ne possède le même nombre généré aléatoirement, que ce soit pour `x` (à gauche) ou `y` (à droite), qui sont les deux variables dans la boucle.<br>
  <br>
  Nous pouvons alors conclure que **oui, Pi.java est fiable puisque son générateur de nombres aléatoires génère bien des nombres aléatoires distincts entre les Workers**.<br>
  Autrement, chaque Worker aurait fait le même calcul, donc on aurait fait nWorker fois la même chose, même si nous sommes allés plus vite. Ça n'aurait donc servi à rien.<br>
  La confiance "trust" dépend de la précision et de la qualité du code.<br>


- **Pleasure : est-ce qu'on a des désagréments dans l'utilisation, est-ce qu'on a de mauvaises surprises ?**<br>
  Je suppose que oui, disons que le code s'exécute rapidement et qu'il utilise assez bien la puissance de l'ordinateur. Il est facile d'utilisation, je trouve.<br>

  
<br><br><br><br>
___ 
## <a name="p5"></a> V - Mise en œuvre en mémoire distribuée

### <a name="p5a"></a> a) (Analyse) JavaSocket

En Software, le **socket** est un fichier contenant des informations.<br>
Toutes nos données, toutes les cases mémoires sont mises dans un fichier qui est transmis dans le réseau.<br>
<br>
Nous en venons au terme **overhead** qui est le temps de traitement de plusieurs processus par la machine.<br>
On peut le diminuer en augmentant la charge d'un processus, car traiter quelques gros processus est moins coûteux que de gérer plusieurs petits processus.<br>
C'est pour cela que l'architecture d'une carte graphique, utile pour des calculs parallèles, n'est pas adaptée pour faire fonctionner un système d'exploitation.<br>
Une carte graphique gère plusieurs petits processus, contrairement à un processeur qui en gère principalement des gros. Avec un GPU, le système serait donc très lent !<br>

<br><br><br>

### <a name="p5b"></a> b) MasterWorker

Pour le dernier projet, on utilise deux fichiers Java :

- Master, qui gère les Workers lancés sur différents ports
- Worker, qui se lance sur un port précis. Ce qui permet d'en lancer plusieurs simultanément sur différents ports.<br>
  
<br>
  Notre objectif à terme est de lancer un Master sur un ordinateur, et utiliser les Workers qui sont sur d'autres ordinateurs.<br>
  En conséquence, le code est prévu pour : après une petite modification, on peut donner des IP personnalisées à Master pour accéder aux autres ordinateurs.<br>
  Pour le moment, nous allons tester sur une seule machine.<br>

Voici un diagramme UML complet de Master-Worker :<br>
<img src="img/uml_mw.png" width="800"/><br>

*Les outils suivants m'ont aidé : StarUML, ChatGPT*<br>

<br>
J'ai dû rajouter un morceau de code pour exécuter la méthode de Monte-Carlo ainsi que l'enregistrement dans un fichier texte comme pour Assignment102 et Pi.java.<br>
On reprend simplement le morceau de code d'Assignment102 permettant de calculer Pi que l'on place dans une fonction qu'on appelle.<br>
On aurait pu directement l'intégrer au code, seulement celui-ci serait moins maintenable, il serait plus difficile de retrouver puis de remplacer le morceau.<br>
Ici, on sait ce qu'il a précisément besoin et ce qu'il doit renvoyer, faire.<br>
<br>
<img src="img\permettraDeCalculerPi.png" width="350"/><br>
<img src="img\whileWorker.png" width="900" processus/><br>

<br>**Pour lancer et changer de port :**<br>
- **Lancer sur le port 25545 :**<br>
  Sur IntelliJ pour WorkerSocker :
  - Cliquer sur le bouton "3 points verticaux", puis "Edit" et mettre "25545" dans le port (sous distributedMC).<br>
- **Lancer plusieurs instances :**<br>
  Sur IntelliJ pour WorkerSocker :
  - Cliquer sur le bouton "3 points verticaux", puis dans "Config", dans "Modify options (Build and Run)", cocher Allow m... (CTRL+U) en haut de la liste.<br><br>

Si nous exécutons nos programmes sur une seule machine, la répartition des Worker + Master correspond au schéma du III - b).<br>
Si nous l'exécutons sur plusieurs machines, cela correspondrait à ça :<br>

<img src="img\schema_dufaud_1M_3W_1M_sur_plusieurs_pc.png" width="600"/><br>

<br><br><br><br>
___ 
## <a name="p6"></a> VI - Test de performance Master-Worker distribuée

Les deux expériences suivantes ont été menées en salle G24 le Vendredi 13 Décembre 2024.<br>
12 ordinateurs à droite ont été utilisés en tant que Worker, 1 ordinateur à gauche a été utilisé en tant que Master.<br>
<br>

**Attention :** les ordinateurs "Workers" n'exécutaient pas tous Pi.java, certains étaient sous Assignment102 !<br>
Seulement, nous ne tiendrons pas compte de l'impact que peut avoir Assignment102 dans notre analyse.<br>

<br>

<img src="img/scalabilite_distribuee_forte_faible_tableau.png">

<br><br>

- **Expérience n°6 en Scalabilité Forte : Comment les ressources sont utilisés lorsque j'ajoute des processus pour un problème de taille fixe ?**<br>
  <img src="img\scalabilite_forte_MW_sur_machine_192000000.png"><bR><br>
  L'expérience n°6 reprend la consigne de l'expérience n°2. Même si ici le totalCount est différent (192 000 000) et que la configuration est différente (utilisation d'un parc de 13 PC sous CentOS), nous remarquons une différence frappante entre ces deux expériences !<br>
  Déjà, le speed-up calculé suit le speed-up idéal jusqu'à 16 cœurs (16 processus), contrairement à l'expérience n°2 où c'était de 1 à 4.<br>
  La courbe commence à dévier à partir de 16 cœurs mais reste proche de l'idéal. Il faudrait tester avec davantage de processus pour observer quand ça stagne. Dans l'expérience n°2, ça stagne dès 8 processus.<br>
  Nous remarquons tout de suite la puissance d'un parc informatique assemblé pour faire un calcul distribué plutôt qu'un seul PC pour un calcul parallèle.<br>
  <br>
  Il est intéressant de noter que pour l'expérience n°2, nous sommes allés jusqu'à 64 processus, ici, nous nous arrêtons à 48. Mais nous pouvons assez bien imaginer la suite des courbes et surtout, nous pouvons comparer les résultats aisément.<br>

<br>

- **Expérience n°7 en Scalabilité Faible : Comment les ressources sont utilisés lorsque j'ajoute des processus pour un problème de taille fixe ?**<br>
  <img src="img\scalabilite_faible_MW_sur_machine_4000000.png"><br><br>
  Cette fois-ci, c'est l'expérience n°4 qui est comparée. La conclusion reste la même : le résultat du calcul distribué écrase celui du calcul parallèle


___ 
## <a name="p8"></a> VIII - Mesures bonus sur Pi.java mais sans explication

<img src="img\mesure_supp1.png"><br>
<img src="img\mesure_supp2.png">

___ 
## <a name="p9"></a> IX - System/Software Product Quality

- Functional Suitability : Respect des fonctionnalités, des attendus de l'utilisateur
  - Pi.java répond aux attentes de l'utilisateur comme déjà expliqué pour le critère "Satisfaction" de la Quality in use
- Performance efficiency :
  - Time behavior : limite de temps d'utilisation
    - La seul limite que connait Pi.java est celle de Java comme l'espace mémoire du système attribué à Java.<br>
      Autrement, le programme ne s'arrête pas après un certain temps dans le cas d'un très grand calcul.<br>
      <br>
      L'utilisateur doit avoir conscience du temps que peut prendre son calcul.
  - Resource utilisation capacity : utilisation des ressources, quantité de mémoire
    - cela dépend des calculs effectués.<br>
      Par exemple, dans le cas de la mesure bonus n°2 de la partie 8, voici ce qu'indiquait le Gestionnaire des tâches :
      <img src="img\utilisation processeur.png"><br>
      <br>
      Nous observons que le processeur est monopolisé par Java, en terme de mémoire volatile, Java n'en utilise pas beaucoup à ce moment de l'étude (voir console).<br>
      La capture provient d'un ordinateur de la salle G24, rangée de droite.
- Compatibility : Si on peut l'utiliser avec d'autres librairies par exemple
  - Pi.java est utilisable sous Linux avec CentOS. Il n'y a pas eu de tests de compatiblité avancés.
- Usability :
  - Appropriateness recognisability : Si c'est facile à prendre en main
    - Déjà abordé dans la section "Satisfaction" de la Quality in use
  - Learnability : Si c'est facile à apprendre, s'il y a un guide, un tutoriel
    - Ce rapport explique comment utiliser Pi.java mais également ce que l'on peut en faire.<br>
      Autrement, son lancement est simple sur IntelliJ.
    - Operability :
      - Non étudié
    - User error protection : est ce qu'il protège ou demande une confirmation lors d'une situation à rique
      - Pi.java ne présente aucune situation à risque, excepé si on exécute un calcul lourd : de ce fait, la charge demandé pour l'ordinateur est importante.<br>
        Pi.java ne juge pas de l'importance du problème et n'informe donc pas l'utilisateur des conséquences de son calcul.
    - User Interface aesthetics :
      - Non étudié
    - Accessibility :
      - L'accessibility de Pi.java est en lien à l'accessibilité du terminal Linux ou d'IntelliJ en fonction de comment on démarre le programme.
- Reliability
  - Maturity : Si on a appris des erreurs de la dernière version
    - J'ai appris que nous ne devons pas toucher au code de Pi.java : `tasks.add(new Worker(totalCount, i));`, on doit laisser cette ligne tel quel.<br>
      Si nous divisons totalCount par le nombre de processus, cela rend le calcul de Pi faux (on doit enlever la division par le nombre de processus dans ce cas) :  `pi = 4.0 * total / totalCount / numWorkers;`
  - Availability :
    - Le programme ne demande pas de condition d'exécution particulière. 
  - Fault tolerance : Dans quel mesure le logiciel tolère une erreur
    - Non étudié
  - Recoverability :
    - Non étudié
- Security
  - Confidentiality :
    - Pi.java ne manipule pas de données confidentiel
  - Integrity : Si une donnée peut être modifié
    - Non étudié
  - Accountability :
    - Non étudié
  - Authenticity : Si les données sont authentique avec une utilisation de chiffrement RSA par exemple.
    - Non étudié