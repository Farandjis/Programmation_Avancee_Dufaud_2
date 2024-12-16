import matplotlib.pyplot as plt
import csv

# Fonction pour lire le fichier et extraire les données
def lire_donnees(fichier):
    erreurs = []
    nlances = []
    
    with open(fichier, mode='r') as f:
        reader = csv.DictReader(f)  # Utilisation de DictReader pour gérer les en-têtes
        for row in reader:
            erreurs.append(float(row['Error']))  # Conversion en float pour traitement numérique
            nlances.append(int(row['Nlance']))  # Conversion en int pour le nombre de lancés
            
    return nlances, erreurs

# Fonction pour tracer le graphique
def tracer_graphique(nlances, erreurs):
    plt.plot(nlances, erreurs, marker='o', linestyle='-', color='b')
    plt.xlabel('Nombre de lancés (Nlance)')
    plt.ylabel('Taux d\'erreur (Error)')
    plt.title('Taux d\'erreur en fonction du nombre de lancés')
    plt.grid(True)
    plt.show()

# Programme principal
fichier = 'pi.txt'  # Remplacez ce nom par le chemin vers votre fichier pi.txt
nlances, erreurs = lire_donnees(fichier)
tracer_graphique(nlances, erreurs)

