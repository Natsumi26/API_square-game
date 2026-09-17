# 🎮 Square Games API

API REST développée avec **Spring Boot** permettant de créer et gérer des parties de différents jeux de plateau.

Les jeux sont gérés à travers des plugins afin de permettre à l'API de prendre en charge plusieurs types de jeux, notamment :

* Tic-Tac-Toe (Morpion)
* Connect Four (Puissance 4)
* Taquin

L'application utilise une base de données MySQL pour la persistance des parties mais peut également utiliser une base de données H2 en mémoire.

---

## 🛠️ Technologies

* Java
* Spring Boot
* Spring Web
* Spring Data JPA
* MySQL
* Docker / Docker Compose
* Maven
* Springdoc OpenAPI / Swagger
* RestClient pour la communication avec l'API Users

---

## 📁 Architecture

L'application est organisée notamment autour des éléments suivants :

```text
src/main/java/com/square_games/api
│
├── clients
│   └── UserClient
|
├── controllers
|   ├── GameCatalogController
│   └── GameController
│
├── services
│   ├── GameCatalogService
│   ├── GameCatalogServiceImpl
│   ├── GameService
│   └── GameServiceImpl
│
├── dao
│   ├── GameDao
│   ├── InMemoryGameDao
│   ├── JdbcGameDao
│   └── JpaGameDao
│
├── DTO
│   ├── GameCreationParams
│   └── MoveParams
|
├── models
│   ├── GameEntity
│   ├── GameEntityRepository
│   └── GameTokenEntity
│
├── plugins
│   ├── GamePlugin
│   ├── TicTacToePlugin
│   ├── ConnectFourPlugin
│   └── TaquinPlugin
│
└── config
    └── GameConfiguration

src/main/resources
│
├── application.properties (dispo sur le git)
├── application-h2.properties (dans le gitignore : configuration de la BDD H2 (url, password, user, ...))
├── application-mysql.properties (dans le gitignore : configuration de la BDD MySQL (url, password, user, ...))
└── messages.properties (dispo sur le git : conserve la traduction)

racine du projet
│
├── mysql
│    └── init.sql (script pour créer la BDD users)
│
├── .env (dans le gitIgnore : contient les donnée sensible pour la connexion bdd pour docker)
├── env-example (dispo sur git : modèle du .env)
└──  docker-compose.yml (dispo sur git : configuration pour les container docker)

```

Le projet utilise le principe de **DAO** pour séparer la persistance de la logique métier.

Les règles spécifiques à chaque jeu sont gérées par les `GamePlugin`.

---

# 🚀 Installation et démarrage

## 1. Prérequis

Avant de démarrer l'application, installer :

* Java
* Maven
* Docker
* Docker Compose
* MySQL (si vous ne l'utilisez pas avec Docker)

Vérifier les installations :

```bash
java -version
mvn -version
docker --version
docker compose version
```

---

## 2. Démarrer la base de données

La base MySQL est prévue pour fonctionner avec Docker Compose.

Depuis le dossier contenant le fichier `docker-compose.yml` :

```bash
docker compose up -d
```

Vérifier que le conteneur fonctionne :

```bash
docker ps
```

Le conteneur MySQL doit être démarré avant de lancer l'API Games.

Pour arrêter les conteneurs :

```bash
docker compose down
```

---

## 3. Configuration de l'application

L'application utilise les propriétés Spring pour configurer la connexion à la base de données.

Pour un environnement local, le profil `h2` peut être utilisé :

```text
application-h2.properties
```

Les paramètres de connexion à MySQL doivent correspondre à ceux configurés dans Docker Compose.

L'URL de l'API Users est également configurée dans `application.properties` :

```properties
users.api.url=http://localhost:8081
```

L'API Games utilise cette URL pour vérifier qu'un utilisateur fourni dans `X-UserId` existe bien dans l'API Users.

---

## 4. Démarrer l'API Users

L'API Games communique avec une seconde application dédiée à la gestion des utilisateurs.

Cette API doit être démarrée sur le port :

```text
8081
```

L'API Games fonctionne sur :

```text
8080
```

Les deux applications doivent donc être lancées en parallèle.

---

## 5. Démarrer l'API Games

Depuis la racine du projet :

```bash
mvn spring-boot:run
```

Ou depuis IntelliJ IDEA, lancer la classe principale de l'application Spring Boot.

L'API sera accessible à :

```text
http://localhost:8080
```

---

# 📖 Documentation Swagger

La documentation interactive de l'API est disponible avec Swagger UI :

```text
http://localhost:8080/swagger-ui/index.html
```

La spécification OpenAPI est également disponible à :

```text
http://localhost:8080/v3/api-docs
```

Swagger permet notamment de consulter et tester les endpoints de l'API.

---

# 🔐 Authentification utilisateur

L'utilisateur est identifié grâce au header HTTP :

```text
X-UserId
```

Exemple :

```http
X-UserId: 550e8400-e29b-41d4-a716-446655440000
```

Avant certaines opérations, l'API Games vérifie que cet utilisateur existe auprès de l'API Users.

---

# 🎮 Endpoints principaux

## Lister les parties

```http
GET /games
```

Header :

```http
X-UserId: <UUID>
```

Retourne les parties auxquelles participe l'utilisateur.

---

## Créer une partie

```http
POST /games
```

Header :

```http
X-UserId: <UUID>
```

Exemple :

```json
{
  "type": "tictactoe",
  "playerCount": 2,
  "boardSize": 3,
  "opponentIds": [
    "650e8400-e29b-41d4-a716-446655440000"
  ]
}
```

---

## Obtenir une partie

```http
GET /games/{gameId}
```

Header :

```http
X-UserId: <UUID>
```

---

## Obtenir le statut d'une partie

```http
GET /games/status/{gameId}
```

Header :

```http
X-UserId: <UUID>
```

---

## Lister les parties en cours

```http
GET /games/ongoing
```

Header :

```http
X-UserId: <UUID>
```

Retourne uniquement les parties :

* auxquelles participe l'utilisateur ;
* dont le statut est `ONGOING`.

---

## Supprimer une partie

```http
DELETE /games/{gameId}
```

Header :

```http
X-UserId: <UUID>
```

---

# ♟️ Coups possibles

Pour les jeux comme **Tic-Tac-Toe** et **Connect Four** :

```http
GET /games/{gameId}/possiblemoves
```

Header :

```http
X-UserId: <UUID>
```

Pour le **Taquin**, une position de jeton est nécessaire :

```http
GET /games/{gameId}/tokens/{x}/{y}/possiblemoves
```

Exemple :

```http
GET /games/123/tokens/1/2/possiblemoves
```

---

# 🎯 Jouer un coup

```http
POST /games/{gameId}/moves
```

Header :

```http
X-UserId: <UUID>
Content-Type: application/json
```

Exemple de `MoveParams` :

Avec position de départ non null (taquin) :

```json
{
  "from": {
    "x": 1,
    "y": 2
  },
  "to": {
    "x": 1,
    "y": 3
  }
}
```

Avec position de départ null (Connect four et tic tac toe) :

```json
{
  "from": null,
  "to": {
    "x": 1,
    "y": 3
  }
}
```

La structure de `MoveParams` dépend du type de jeu.

Le moteur du jeu vérifie ensuite si le déplacement demandé est autorisé.

---

# 🧩 Gestion des jeux

Les différents jeux sont gérés grâce à l'interface `GamePlugin`.

Chaque plugin est responsable de la logique spécifique à son jeu, notamment :

* la création d'une partie ;
* la récupération des coups possibles ;
* la sélection du token à déplacer.

Le service principal reste ainsi indépendant du type de jeu.

---

# 💾 Persistance

Les parties sont persistées dans MySQL.

L'application utilise :

* Spring Data JPA ;
* `GameDao` pour abstraire la persistance ;
* `JpaGameDao` pour l'implémentation JPA.

Les parties peuvent être reconstruites à partir des données persistées grâce aux `GameFactory` du moteur de jeux.

---

# 🐳 Commandes Docker utiles

Démarrer les services :

```bash
docker compose up -d
```

Afficher les conteneurs :

```bash
docker ps
```

Afficher les logs :

```bash
docker compose logs
```

Afficher les logs de MySQL :

```bash
docker compose logs mysql
```

Arrêter les services :

```bash
docker compose down
```

---

# 🧪 Tests

Les endpoints peuvent être testés avec :

* Swagger UI
* Postman
* tout autre client HTTP

Swagger :

```text
http://localhost:8080/swagger-ui/index.html
```

---

# 📌 Résumé du démarrage

Dans l'ordre :

```text
1. Démarrer Docker
       ↓
2. Démarrer MySQL
       ↓
3. Démarrer l'API Users sur le port 8081
       ↓
4. Démarrer l'API Games sur le port 8080
       ↓
5. Ouvrir Swagger
       ↓
6. Tester les endpoints
```

Swagger :

```text
http://localhost:8080/swagger-ui/index.html
```

API Games :

```text
http://localhost:8080
```

API Users :

```text
http://localhost:8081
```
