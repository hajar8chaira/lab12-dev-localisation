# LAB 12-Lab 13 - Localisation temps réel
**Cours :** Programmation Mobile : Android avec Java  
**Étudiant :** Hajar Chaira

---

## 1. Objectif 
L'objectif de ce laboratoire est de concevoir et de réaliser une architecture client-serveur complète pour le suivi géolocalisé en temps réel de terminaux Android. L'application mobile capte en continu les coordonnées GPS du terminal et les transmet de manière asynchrone à un serveur Web local (Apache/PHP) qui les enregistre dans une base de données MySQL. L'historique des positions est ensuite récupéré pour être projeté sous forme de repères interactifs (pins) sur une carte OpenStreetMap (osmdroid).

Ce projet met en pratique la structuration en couches du backend (modèle DAO), la sécurisation des requêtes SQL via PDO, le requêtage réseau asynchrone avec la bibliothèque Volley et la manipulation d'interfaces cartographiques open-source.

---

## 2. Aperçu visuel du projet et Démonstration

### Captures d'écran de la configuration et de l'exécution

| Suivi GPS et Synchronisation | Enregistrement en Base de données | Cartographie interactive et Marqueurs |
| :---: | :---: | :---: |
| ![Suivi et Synchronisation](/img-lab12-dev/1.png) | ![Base de données MySQL](/img-lab12-dev/2.png) | ![Carte OpenStreetMap](/img-lab12-dev/3.png) |
| Interface principale affichant les coordonnées en temps réel avec notification de synchronisation réussie | Données de géolocalisation enregistrées dynamiquement avec date précise et identifiant unique de l'appareil | Rendu de la carte centré sur le dernier point avec les pins rouges représentant l'historique complet |

---

## 3. Démonstration Vidéo
La vidéo ci-dessous montre la chaîne complète de fonctionnement : la simulation de déplacement GPS sur l'émulateur, la réception instantanée des coordonnées, leur écriture automatique en base de données et l'actualisation dynamique des marqueurs sur la carte OpenStreetMap lors de l'ouverture de l'écran cartographique.

[<video src="../img-lab12-dev/video.mp4" controls="controls" style="max-width: 100%;">
</video>](https://github.com/user-attachments/assets/46a7c57d-63df-41ad-8410-6ff8dda60325)

---

## 4. Architecture et Étapes de réalisation

### Étape 1 : Conception de la Base de données (MySQL)
Une base de données relationnelle nommée `localisation` a été mise en place. Elle contient la table structurée appelée `position` 

### Étape 2 : Développement du Backend PHP (Architecture DAO)
Le serveur web Apache héberge une API structurée en couches pour interagir proprement avec les données :
* **Couche Modèle (Classe Position) :** Représente l'objet d'information géographique sous forme de classe PHP avec ses constructeurs et accesseurs (getters/setters).
* **Couche Connexion (PDO Singleton) :** Gère la connexion à MySQL de manière sécurisée en activant les rapports d'erreurs d'exceptions pour éviter les failles.
* **Couche Accès aux Données (DAO Interface & Service) :** Déclare et implémente les méthodes CRUD. L'insertion utilise des requêtes préparées avec des paramètres d'interrogation (?) afin de se prémunir totalement contre les injections SQL.
* **Endpoints API REST :** 
  * `createPosition.php` reçoit les requêtes HTTP POST de l'application Android, extrait les variables de géolocalisation, capture l'adresse IP du client et sollicite le service DAO pour l'enregistrement.
  * `showPosition.php` extrait l'historique complet trié par date décroissante et le renvoie au client Android sous la forme d'un objet JSON structuré.

### Étape 3 : Application Android - Acquisition GPS et Envoi (MainActivity)
La classe principale configure l'acquisition des données à l'aide du service système `LocationManager`.
* **Écoute Réactive :** Pour les besoins de test sur l'émulateur, les paramètres de distance minimale et d'intervalle temporel ont été fixés à 0. Cela garantit que chaque nouveau clic sur le panneau "Set Location" de l'émulateur déclenche immédiatement une mise à jour.
* **Requêtes Réseau Asynchrones :** Lors de chaque modification de coordonnées, une requête HTTP POST est assemblée via la bibliothèque Volley. Les paramètres (latitude, longitude, date et identifiant d'appareil) sont encapsulés et envoyés de manière non-bloquante vers l'adresse spéciale de bouclage `10.0.2.2`, qui redirige le trafic de l'émulateur directement vers le serveur web du PC hôte.

### Étape 4 : Application Android - Rendu Cartographique (MapsActivity)
L'affichage cartographique repose sur un composant `MapView` fourni par osmdroid dans le layout XML.
* **Identification Cliente (User-Agent) :** Afin de se conformer aux conditions d'utilisation d'OpenStreetMap et d'autoriser le chargement fluide des tuiles cartographiques, un identifiant client personnalisé est passé au gestionnaire de configuration.
* **Extraction et Projection JSON :** Au chargement de l'activité, une requête HTTP POST de Volley récupère la liste des points enregistrés sur le serveur. L'application décode le tableau JSON reçu, instancie des objets marqueurs (`Marker`) positionnés sur les coordonnées correspondantes, et leur associe une infobulle interactive détaillant le nom du terminal et l'heure du relevé avant de centrer la caméra sur le point le plus récent.

---
## Resultat final dans la table  position:
![](/img-lab12-dev/5.png)

