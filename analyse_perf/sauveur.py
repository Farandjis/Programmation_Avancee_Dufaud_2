import pandas as pd
import matplotlib.pyplot as plt

file_path1 = 'pi.txt'
file_path2 = 'assignement102.txt'

def faible_speedup(ax, df):
    df_filtered = df[df['Npoint'] != 12000000]
    df_filtered['tempsMilis'] = df_filtered['tempsMilis'].astype(int)
    df_filtered['Nproc'] = df_filtered['Nproc'].astype(int)

    df_mean = df_filtered.groupby('Nproc')['tempsMilis'].mean().reset_index()

    sequential_time = df_mean['tempsMilis'].iloc[0]
    df_mean['speedup'] = sequential_time / df_mean['tempsMilis']

    ax.plot(df_mean['Nproc'], df_mean['speedup'], marker='o', linestyle='-', color='r')
    ax.plot([1, 32], [1, 1], '--b', label='Speedup idéal')
    ax.legend()
    ax.set_xlabel('Nombre de processus (Nproc)')
    ax.set_ylabel('Speed-up')
    ax.set_title('Scalabilité faible : Speed-up')
    ax.grid(True)


def forte_speedup(ax, df):
    df_filtered = df[df['Npoint'] == "12000000"]

    df_filtered['tempsMilis'] = df_filtered['tempsMilis'].astype(int)
    df_filtered['Nproc'] = df_filtered['Nproc'].astype(int)

    df_mean = df_filtered.groupby('Nproc')['tempsMilis'].mean().reset_index()

    sequential_time = df_mean['tempsMilis'].iloc[0]
    df_mean['speedup'] = sequential_time / df_mean['tempsMilis']

    ax.plot(df_mean['Nproc'], df_mean['speedup'], marker='o', linestyle='-', color='r')
    ax.plot([1, 12], [1, 12], '--b', label='Speedup idéal')
    ax.legend()
    ax.set_xlabel('Nombre de processus (Nproc)')
    ax.set_ylabel('Speed-up')
    ax.set_title('Scalabilité forte : Speed-up')
    ax.grid(True)

def forte(ax, df):
    df_filtered = df[df['Npoint'] == "12000000"]
    print(df_filtered)

    df_filtered['tempsMilis'] = df_filtered['tempsMilis'].astype(int)
    df_filtered['Nproc'] = df_filtered['Nproc'].astype(int)

    df_mean = df_filtered.groupby('Nproc')['tempsMilis'].mean().reset_index()

    ax.plot(df_mean['Nproc'], df_mean['tempsMilis'], marker='o', linestyle='-', color='b')
    ax.set_xlabel('Nombre de processus (Nproc)')
    ax.set_ylabel('Temps d\'exécution moyen (ms)')
    ax.set_title('Scalabilité forte : Temps d\'exécution')
    ax.grid(True)

def faible(ax, df):
    df_filtered = df[df['Npoint'] != 12000000]
    print(df_filtered)

    df_filtered['tempsMilis'] = df_filtered['tempsMilis'].astype(int)
    df_filtered['Nproc'] = df_filtered['Nproc'].astype(int)

    df_mean = df_filtered.groupby('Nproc')['tempsMilis'].mean().reset_index()

    ax.plot(df_mean['Nproc'], df_mean['tempsMilis'], marker='o', linestyle='-', color='b')
    ax.set_xlabel('Nombre de processus (Nproc)')
    ax.set_ylabel('Temps d\'exécution moyen (ms)')
    ax.set_title('Scalabilité faible : Temps d\'exécution')
    ax.grid(True)


    # Création de la figure et des sous-graphiques
fig, axs = plt.subplots(4, 2, figsize=(15, 20))

# Chargement et tracé pour file_path1
df = pd.read_csv(file_path1, sep=',', header=None, names=['Error', 'Npoint', 'Pi', 'Nlance', 'tempsMilis', 'Nproc'])
forte(axs[0, 0], df)
forte_speedup(axs[1, 0], df)

df = pd.read_csv(file_path1, sep=',', header=0, names=['Error', 'Npoint', 'Pi', 'Nlance', 'tempsMilis', 'Nproc'])
faible(axs[2, 0], df)
faible_speedup(axs[3, 0], df)

# Chargement et tracé pour file_path2
df = pd.read_csv(file_path2, sep=',', header=None, names=['Error', 'Npoint', 'Pi', 'Nlance', 'tempsMilis', 'Nproc'])
forte(axs[0, 1], df)
forte_speedup(axs[1, 1], df)

df = pd.read_csv(file_path2, sep=',', header=0, names=['Error', 'Npoint', 'Pi', 'Nlance', 'tempsMilis', 'Nproc'])
faible(axs[2, 1], df)
faible_speedup(axs[3, 1], df)

# Ajustement de l'espacement
plt.tight_layout()
plt.show()