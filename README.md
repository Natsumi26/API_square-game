# 🎮 Square Games API

API REST développée avec **Spring Boot** permettant de créer et gérer des parties de différents jeux de plateau.

Les jeux sont gérés à travers des plugins afin de permettre à l'API de prendre en charge plusieurs types de jeux, notamment :

* Tic-Tac-Toe (Morpion)
* Connect Four (Puissance 4)
* Taquin

L'application utilise une base de données MySQL pour la persistance des parties mais peut également utiliser une base de données H2 en mémoire.

L'application utilise l'API Users pour l'authentification et les deux applications utilisent des JWT signés avec la même clé secrète.

---

## 🛠️ Technologies

* Java
* Spring Boot
* Spring Web
* Spring Security
* JWT avec JJWT
* Spring Data JPA
* MySQL
* Docker / Docker Compose
* Maven
* Springdoc OpenAPI / Swagger
* RestClient pour la communication avec l'API Users

---

## 📁 Architecture

L'application suit une architecture en couches :

```text
GameController
      ↓
GameService
      ↓
GameDao
      ↓
JPA / JDBC
      ↓
MySQL
```

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
│   ├── JwtService
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
├── security
│   └── JwtAuthenticationFilter
│
└── configuration
│   ├── SecurityConfig
    └── GameConfiguration

src/main/resources
│
├── application.properties (dispo sur le git)
├── application-local.properties (dans le gitignore: contient la clé secret jwt)
├── application-h2.properties (dans le gitignore : configuration de la BDD H2 (url, password, user, ...))
├── application-mysql.properties (dans le gitignore : configuration de la BDD MySQL (url, password, user, ...))
└── messages.properties (dispo sur le git : conserve la traduction)

racine du projet
│
├── mysql
│    └── init.sql (script pour créer la BDD users)
│
├── .env (dans le gitIgnore : contient les donnée sensible pour la connexion bdd pour docker)
├── .env-example (dispo sur git : modèle du .env)
└──  docker-compose.yml (dispo sur git : configuration pour les container docker)

```

Le projet utilise le principe de **DAO** pour séparer la persistance de la logique métier.

Les jeux sont gérés par des plugins (`GamePlugin`) afin de prendre en charge plusieurs types de jeux, notamment :

- Tic-Tac-Toe
- Taquin
- Connect Four

---

# 🚀 Installation et démarrage

## Prérequis

Avant de lancer l'application :

- Java installé
- Maven ou Maven Wrapper
- Docker et Docker Compose
- MySQL lancé via Docker
- API Users disponible sur le port `8081`

## Configuration

L'API Games utilise le port `8080`.

```properties
server.port=8080
```

La clé secrète JWT doit être **strictement identique** à celle utilisée par l'API Users :

```properties
jwt.secret=VOTRE_SECRET
```

Ne pas versionner une vraie clé secrète dans Git, utiliser le fichier `application-local.properties` pour la clé secrete.

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

---

## 2. Démarrer la base de données

La base MySQL est prévue pour fonctionner avec Docker Compose.

Depuis le dossier contenant le fichier `docker-compose.yml` :

```bash
docker compose up -d
```

Le conteneur MySQL doit être démarré avant de lancer l'API Games.

Pour arrêter les conteneurs :

```bash
docker compose down
```

---


## 3. Démarrer l'API Users

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

## Authentification JWT

Les endpoints de l'API Games sont protégés par Spring Security.

Il faut d'abord obtenir un JWT auprès de l'API Users :

```http
POST http://localhost:8081/auth/login
Content-Type: application/json
```

Exemple :

```json
{
  "username": "Alice",
  "password": "motdepasse"
}
```

Le token retourné doit ensuite être envoyé dans les requêtes Games :

```http
Authorization: Bearer <JWT>
```

Le JWT contient notamment :

```json
{
  "sub": "Alice",
  "userId": "UUID_DE_L_UTILISATEUR",
  "role": "ROLE_USER"
}
```

L'API Games valide localement le JWT et récupère le `userId` directement depuis son contenu.

Il n'y a donc plus d'appel à l'API Users pour vérifier l'utilisateur à chaque requête.

---

## Endpoints principaux

### Jeux

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/games` | Liste les jeux de l'utilisateur connecté |
| POST | `/games` | Crée une nouvelle partie |
| GET | `/games/{gameId}` | Récupère une partie |
| GET | `/games/status/{gameId}` | Récupère le statut d'une partie |
| GET | `/games/ongoing` | Liste les parties en cours |
| DELETE | `/games/{gameId}` | Supprime une partie |

### Mouvements

| Méthode | Endpoint | Description |
|---|---|---|
| GET | `/games/{gameId}/tokens/{x}/{y}/moves` | Récupère les mouvements autorisés pour un jeton |
| GET | `/games/{gameId}/possiblemoves` | Récupère les mouvements possibles |
| POST | `/games/{gameId}/moves` | Effectue un mouvement |

Les endpoints exacts de mouvements dépendent de la version actuelle du contrôleur.

## Exemple de création d'une partie

```http
POST http://localhost:8080/games
Authorization: Bearer <JWT>
Content-Type: application/json
```

Exemple minimal :

```json
{
  "type": "tictactoe"
}
```

Des paramètres optionnels peuvent être utilisés selon le jeu :

```json
{
  "type": "tictactoe",
  "playerCount": 2,
  "boardSize": 3,
  "opponentIds": []
}
```
---

# 🧩 Gestion des jeux

Les différents jeux sont gérés grâce à l'interface `GamePlugin`.

Chaque plugin est responsable de la logique spécifique à son jeu, notamment :

* la création d'une partie ;
* la récupération des coups possibles ;
* la sélection du token à déplacer.

Le service principal reste ainsi indépendant du type de jeu.

---

## Sécurité

L'API est configurée en mode **stateless**.

```text
Client
  │
  │ Authorization: Bearer JWT
  ▼
JwtAuthenticationFilter
  │
  ├── vérification du JWT
  ├── récupération du userId
  ▼
SecurityContext
  │
  ▼
GameController
  │
  ▼
GameService
```

L'ancien mécanisme basé sur :

```http
X-UserId: <UUID>
```

n'est plus utilisé.

## Persistance

Les parties sont persistées en base de données MySQL.

La persistance utilise notamment :

- `GameEntity`
- `GameTokenEntity`
- `GameEntityRepository`
- `GameDao`

Les jeux sont associés à l'identifiant de l'utilisateur connecté.

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

## Projets associés

Cette application fonctionne avec l'API Users :

```text
API Users
Port : 8081

API Games
Port : 8080
```

Le flux d'authentification est :

```text
1. Login → API Users
2. Réception du JWT
3. Requête → API Games avec Bearer Token
4. Validation locale du JWT
5. Extraction du userId
6. Accès aux jeux de l'utilisateur
```
