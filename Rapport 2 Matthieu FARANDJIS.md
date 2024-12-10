Matthieu FARANDJIS
INF3-FI

<div align="center">
<img height="95" width="400" src="https://www.uvsq.fr/medias/photo/iut-velizy-villacoublay-logo-2020-ecran_1580904185110-jpg?ID_FICHE=214049" title="logo uvsq vélizy"/>
<h1>Programmation Avancée & Qualité de développement</h1>
<h2>- Rapport n°2 -</h2>
</div>


### Rappel définitions : <br>


<br><br>
### Liens intéressants :
**Sur  :** https://<br>

<br><br>
## Plan
- **I - Méthode MC**
- **II - Algorithme et parallélisation**
  - a) Itération parallèle
  - b) Master-Worker
- **III - Mise en œuvre sur machine à mémoire partagée**
  - a) Analyse d'Assigment102
  - b) Analyse Pi.java
- **IV - Qualité et test de performance (cf R05.08 Q Dev)**
- **V - Mise en œuvre en mémoire distribuée**
  - a) (Analyse) JavaSocket
  - b) MasterWorker
- **VI - Perf MW dist**
<br><br><br>


## I - Méthode MC
Pour résumer, la méthode de Monte-Carlo prédit un résultat à partir d'une portion de valeurs générés aléatoire.<br>
Elle recalcul plusieurs fois le résultats.<br>
<br>
*Méthode d'après IBM : https://www.ibm.com/fr-fr/topics/monte-carlo-simulation* <br>
<br><br>
**Calcul de π par une méthode de MonteCarlo, d'après le TP4 fait en cours**<br>
Soit l'aire $ A_{\tfrac{1}{4}d} $ d'un quart de disque de rayon r=1.<br>

$ A_{\tfrac{1}{4}d} =  \frac{\pi r^2}{4} = \frac{\pi}{4} $


### II - Algorithme et parallélisation



















## I - Présentation des projets

Cette partie traite des différents projets que nous avons utilisés durant nos TP.<br>
Assigment102 et Pi.java sont les deux premiers projets que nous avons étudiés.<br>
Le projet Master-Worker est plus complexe, nous l'avons vu après.<br>

### a) Présentation d'Assigment102
Le code d'Assigment102 calcule une valeur approximative de Pi à partir de la méthode de MonteCarlo.<br>
On y retrouve deux dépendances nouvelles : Atomic integer et Executor.

- **Atomic Integer :**<br>
  Encapsule une valeur entière qui peut être mise à jour de manière atomique.<br>
  C'est-à-dire que toutes les opérations de lecture-modification-écriture sur cette valeur sont effectuées comme une seule unité insécable. Cela garantit la sécurité des threads (thread-safety) sans nécessiter de synchronisation explicite.<br>
  Un AtomicInteger est utilisé dans les applications telles que les compteurs incrémentés atomiquement et ne peut pas être utilisé comme remplacement d’un java.lang.Integer.<br>
  <br>
  *Sources :*
  - *https://www.jmdoudoux.fr/java/dej/chap-acces_concurrents.htm*
  - *https://learn.microsoft.com/fr-fr/dotnet/api/java.util.concurrent.atomic.atomicinteger?view=net-android-34.0*
  - *ChatGPT (la phrase du "C'est-à-dire")*
  <br><br><br>
- **Executor :**<br>
  L'interface java.util.concurrent.Executor décrit les fonctionnalités permettant l'exécution différée de tâches implémentées sous la forme de Runnable.<br>
  C’est un support pour les Threads en Java à un plus haut niveau que la classe Thread.<br>
  Il permet de découpler la soumission des tâches de la mécanique des Threads (Execution, Ordonnancement)<br>
  On peut ici gérer des pool de Threads. Chaque Thread du pool peut être réutilisé dans un Executor.<br>
  L’interface Executor définit la méthode execute.<br>
  <br>
  *Sources :* 
  - *https://jmdoudoux.developpez.com/cours/developpons/java/chap-executor.php#executor-1*
  - *Cours de M. DUFAUD : CM4-complement-parallelisation-et-Java.pdf*
- **Algorithme Workstealing (abordé en cours) :**<br>
  Le principe de cet algorithme est que chaque worker possède une liste de tâche.<br>
  Lorsqu'un worker n'en possède plus, plutôt qu'attendre de nouvelles tâches, il va en extraire chez un autre worker.<br>
  <br>
  De telle manière, non seulement chaque worker à peu peut près le même nombre de tâches, mais aussi, on s'assure que les workers soient toujours actif.<br>
  On garanti donc l'optimisation du fonctionnement du programme.<br>
  *Sources :*
  - https://www.linkedin.com/advice/0/what-benefits-drawbacks-using-work-stealing?lang=fr&originalSubdomain=fr
- **Les futures :**<br>
  Paradigme de programmation qui est très pratique, qui permet de définir quand on créer notre tâche le schéma de dépendance entre les tâches.<br>
  L'idée est de dire que la tâche va envoyer un résultat dans le futur; On ne sait pas quand il va l'envoyer, mais plus loin dans le code on va utliser son code.<br>
  On dit que la tâche x va envoyer un res y dans le temps. la tâche s'éxecute, on continu le code (d'autres trucs s'exécutent), puis un moment x renvoi le resultat, et on fait un calcul.<br>
    - le code à ce moment là, pour calculer doit attendre le resultat de x, c'est un point de synchronisation

<br><br>
On définit un objet executor qui est un support de thread.<br>
On va lui dire : tu définie le nombre de threads que tu vas utiliser.<br>
-> il permet de faire l'association entre des tâches et des threads de manière dynamique en fonction de l'exécution des tâches en cours.<br>
-> Il est donc possible d'associer 1000 tâches à 4 threads (au lieu de 1000 tâches = 1000 threads dans le TP mobile, vu qu'on ne pouvait pas utiliser Executor).<br>
<br>
===> On utilise désormais la classe Executor, plutôt que la classe Thread.<br>
Le fait est que les threads en Java présentent de nombreux inconvénients essentiellement liés au fait que cette classe est de bas niveau.<br>
De ce fait, elle ne propose pas des fonctionnalités tel qu'obtenir un résultat d'exécution d'un thread ou encore attendre la fin d'un ensemble de threads.<br>
<br>
*Sources :*<br>
*https://jmdoudoux.developpez.com/cours/developpons/java/chap-executor.php#executor-1*

====> paradigme d'Assigment102 : itération parallèle = parallélisme de boucle = parallélisme itératif


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
### b) Présentation de Pi.java

**Paradigme de Pi.java :** Master Worker

On a un Master qui va créer puis lancer des tâches qui via le paradigme des futures fait une résolution de dépendance.<br>
<br><br>


Pi.java est plus efficace que Assigmnent102, puisque si on créer 500000 tâche, l'OS gère lui-même ce qui prend plus de temps que de faire :
génération nb aléatoire x, génération nb aléatoire y, test, incrément (~40 cycles).<br>
<br><br>
**Quelques annotations sur le code :**<br>
- **Executors.newFixedThreadPool(numWorkers)**
  - Renvoyer une instance de type ExecutorService qui utilise un pool de threads dont la taille est fixe. Les tâches à exécuter sont stockées dans une queue
    <br><br>
### c) Présentation de Master-Worker
Pour le dernier projet, on utilise deux fichiers java :
- Master, qui gère les Workers lancés sur différents ports
- Worker, qui se lance sur un port, ce qui permet d'en lancer plusieurs simultanéments.<br>
  <br>**Pour lancer et changer de port :**
  - **Lancer sur le port 25545 :**<br>
    Sur IntelliJ pour WorkerSocker :
    - Cliquer sur le bouton "3 points vertical", puis "Edit" et mettre "25545" dans le port (sous distributedMC).
  - **Lancer plusieurs instances :**
    Sur IntelliJ pour WorkerSocker :
    - Cliquer sur le bouton "3 points vertical", puis dans "Config", dans "Modify options (Build and Run)", cocher Allow m... (CTRL+U) en haut de la liste.

<br><br><br><br><br>

## II - Sortie
### a) Uniformisation de la sortie d'Assigment102 et Pi.java
Nos deux codes affiche les résultats de manière différente. Afin de mieux comparer et étudiier leurs efficacités, nous avons du uniformiser les sorties.<br>
C'est à dire s'assurer que chaque code affiche/renvoie la même chose.<br>
<br>
<br>
Ainsi les prints ci-dessous sont dans le même format que ce soit dans Assigment102 ou Pi.java.<br>


<img src="img\uni_sorties.png" width="1000"/><br>
*Code de Pi.java*

### b) Ecriture dans un fichier texte
Pour traiter ces informations et facilité l'automatisation du lancement de plusieurs fois d'affiler le code, nous allons sauvegarder ces informations dans un document.<br>
Pour cela on va créer une classe WriteToFile  :<br>
<img src="img\wtf.png" width="600"/>
<br><br>
Comme nous pouvons le voir, on créer en premier un nom à notre fichier.<br>
Nous indiquons le jour du test, ainsi que la machine sur lequel on teste (grâce à `InetAddress.getLocalHost()`), sachant que le nom de chaque machine de l'IUT correspond à la salle et à sa position dans la salle.<br>
C'est indispensable car les résultats peuvent variés d'une machine à l'autre.<br>
Pour de meilleurs résultats, dans les faits, il faudrait même fermer tous les logiciels ouvert, voir même utiliser le terminal plutôt qu'IntelliJ et dans l'idéal, ne pas d'utiliser l'interface graphique Windows.<br>
<br>
Ensuite nous écrivons à la suite du fichier les données reçu, on sauvegarde et on affiche si tout s'est bien passé ou non.<br>

<br><br><br><br><br>

## II - Projet Master-Worker
### a) Ajout du code pour calculer MonteCarlo et enregistrer dans un fichier
On reprend simplement le morceau de code d'Assigment102 permettant de calculer Pi que l'on place dans une fonction qu'on appel.<br>
On aurait pu directement l'intégrer au code, seulement celui-ci serait moins maintenable, il serait plus dure de retrouver puis de remplacer le morceau.<br>
Ici on sait ce qu'il a précisément besoin et ce qu'il doit renvoyer, faire.<br>
<br>
<img src="img\permettraDeCalculerPi.png" width="350"/><br>
<img src="img\whileWorker.png" width="900"/>
<br><br>
### b) Est-ce que WorkerSocket peut appeler une librairie extérieur / un autre code MC / un autre comportement ?
### c) Mise en place expérience Scalabilité forte et faible
### d) Résultat et études des expériences