-- =========================================================
-- Base de données Square Users
-- =========================================================

CREATE DATABASE IF NOT EXISTS square_users;



-- =========================================================
-- Base de données Square Games
-- =========================================================

-- ---------------------------------------------------------
-- Table : games
-- Contient les informations générales d'une partie
-- ---------------------------------------------------------

-- CREATE TABLE IF NOT EXISTS games (
--     id CHAR(36) NOT NULL,
--     factory_id VARCHAR(100) NOT NULL,
--     board_size INT NOT NULL,
--
--     PRIMARY KEY (id)
--     );


-- ---------------------------------------------------------
-- Table : game_players
-- Contient les joueurs associés à une partie
-- ---------------------------------------------------------

-- CREATE TABLE IF NOT EXISTS game_players (
--     game_id CHAR(36) NOT NULL,
--     player_id CHAR(36) NOT NULL,
--
--     PRIMARY KEY (game_id, player_id),
--
--     CONSTRAINT fk_game_players_game
--     FOREIGN KEY (game_id)
--     REFERENCES games(id)
--     ON DELETE CASCADE
--     );


-- ---------------------------------------------------------
-- Table : game_tokens
-- Contient les tokens d'une partie
-- ---------------------------------------------------------

-- CREATE TABLE IF NOT EXISTS game_tokens (
--     id BIGINT AUTO_INCREMENT,
--
--     game_id CHAR(36) NOT NULL,
--
--     owner_id CHAR(36) NULL,
--     name VARCHAR(100) NOT NULL,
--
--     x INT NULL,
--     y INT NULL,
--
--     state ENUM('BOARD', 'REMAINING', 'REMOVED') NOT NULL,
--
--     PRIMARY KEY (id),
--
--     CONSTRAINT fk_game_tokens_game
--     FOREIGN KEY (game_id)
--     REFERENCES games(id)
--     ON DELETE CASCADE
--     );


-- ---------------------------------------------------------
-- Index
-- ---------------------------------------------------------

-- CREATE INDEX idx_game_tokens_game_id
--     ON game_tokens(game_id);
--
-- CREATE INDEX idx_game_players_game_id
--     ON game_players(game_id);