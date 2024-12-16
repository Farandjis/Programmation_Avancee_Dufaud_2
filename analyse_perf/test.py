import matplotlib.pyplot as plt
import csv

# Chargement des données
file_path = 'pi.txt'  # Remplace par le chemin vers ton fichier

# Listes pour stocker les valeurs de Npoint et les erreurs
npoint_values = []
errors = []

# Lecture du fichier
try:
    with open(file_path, 'r') as file:
        reader = csv.DictReader(file)
        for row in reader:
            try:
                npoint = int(row['Npoint'])
                error = float(row['Error'])
                npoint_values.append(npoint)
                errors.append(error)
            except ValueError:
                # Ignorer les lignes avec des valeurs non valides
                continue
except FileNotFoundError:
    print(f"Fichier non trouvé : {file_path}")
    exit()

# Génération du nuage de points
plt.figure(figsize=(10, 6))
plt.scatter(npoint_values, errors, color='b', alpha=0.7)
plt.xlabel('Nombre de points (Npoint)')
plt.ylabel('Erreur (Error)')
plt.title('Nuage de points : Erreurs pour chaque Npoint')
plt.grid(True)
plt.tight_layout()

# Affichage
plt.show()