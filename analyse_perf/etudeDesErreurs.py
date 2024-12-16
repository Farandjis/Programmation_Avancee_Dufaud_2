import matplotlib.pyplot as plt
import csv
from collections import defaultdict

# Chargement des données
file_path = 'pi.txt'  # Remplace par le chemin vers ton fichier

# Dictionnaire pour stocker les erreurs par valeur de Npoint
data = defaultdict(list)

# Lecture du fichier
try:
    with open(file_path, 'r') as file:
        reader = csv.DictReader(file)
        for row in reader:
            try:
                npoint = int(row['Npoint'])
                error = float(row['Error'])
                data[npoint].append(error)
            except ValueError:
                # Ignorer les lignes avec des valeurs non valides
                continue
except FileNotFoundError:
    print(f"Fichier non trouvé : {file_path}")
    exit()

# Calcul des moyennes d'erreurs
npoint_values = sorted(data.keys())
mean_errors = [sum(errors) / len(errors) for errors in map(data.get, npoint_values)]

# Génération du graphique
plt.figure(figsize=(10, 6))
plt.plot(npoint_values, mean_errors, marker='o', linestyle='-', color='b')
plt.xlabel('Nombre de points (Npoint)')
plt.ylabel('Erreur moyenne (Error)')
plt.title('Erreur moyenne en fonction du nombre de points')
plt.grid(True)
plt.tight_layout()

# Affichage
plt.show()
