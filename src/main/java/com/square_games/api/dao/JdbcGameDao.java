package com.square_games.api.dao;

import fr.le_campus_numerique.square_games.engine.*;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class JdbcGameDao implements GameDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Map<String, GameFactory> gameFactories;

    private record GameData(
            UUID id,
            String factoryId,
            int boardSize
    ) {
    }

    public JdbcGameDao(NamedParameterJdbcTemplate jdbcTemplate, List<GameFactory> gameFactories) {
        this.jdbcTemplate = jdbcTemplate;
        this.gameFactories = gameFactories.stream()
                .collect(Collectors.toMap(
                        GameFactory::getGameFactoryId,
                        Function.identity()
                ));
    }

    @Override
    public Collection<Game> findAll() {
        String sql = "select * from games";


        List<String> gameIds = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("id")
        );

        return gameIds.stream()
                .map(this::findById)
                .toList();
    }

    @Override
    public Game findById(String gameId) {
        String gameSql = "SELECT * FROM games WHERE id = :gameId";

        List<GameData> games = jdbcTemplate.query(
                gameSql,
                new MapSqlParameterSource()
                        .addValue("gameId", gameId),
                (rs, rowNum) -> new GameData(
                        UUID.fromString(rs.getString("id")),
                        rs.getString("factory_id"),
                        rs.getInt("board_size")
                )
        );

        if (games.isEmpty()) {
            return null;
        }

        GameData gameData = games.getFirst();

        String playersSql = """
        SELECT player_id
        FROM game_players
        WHERE game_id = :gameId
        """;

        List<UUID> players = jdbcTemplate.query(
                playersSql,
                new MapSqlParameterSource()
                        .addValue("gameId", gameId),
                (rs, rowNum) ->
                        UUID.fromString(rs.getString("player_id"))
        );

        String tokensSql = """
        SELECT owner_id, name, x, y
        FROM game_tokens
        WHERE game_id = :gameId
          AND state = 'BOARD'
        """;

        List<TokenPosition<UUID>> boardTokens = jdbcTemplate.query(
                tokensSql,
                new MapSqlParameterSource()
                        .addValue("gameId", gameId),
                (rs, rowNum) -> {

                    String ownerId = rs.getString("owner_id");

                    UUID owner = ownerId == null
                            ? null
                            : UUID.fromString(ownerId);

                    return new TokenPosition<>(
                            owner,
                            rs.getString("name"),
                            rs.getInt("x"),
                            rs.getInt("y")
                    );
                }
        );

        GameFactory factory = gameFactories.get(gameData.factoryId());

        if (factory == null) {
            throw new IllegalArgumentException(
                    "Unknown game factory: " + gameData.factoryId()
            );
        }

        try {
            return factory.createGameWithIds(
                    gameData.id(),
                    gameData.boardSize(),
                    players,
                    boardTokens,
                    List.of()
            );

        } catch (InconsistentGameDefinitionException e) {
            throw new IllegalStateException(
                    "Unable to reconstruct game " + gameId,
                    e
            );
        }

    }

    @Override
    @Transactional
    public void upsert(Game game) {

        // 1. Enregistrer la partie
        String gameSql = """
        INSERT INTO games (id, factory_id, board_size)
        VALUES (:id, :factoryId, :boardSize)
        ON DUPLICATE KEY UPDATE
            factory_id = :factoryId,
            board_size = :boardSize
        """;

        MapSqlParameterSource gameParams = new MapSqlParameterSource()
                .addValue("id", game.getId().toString())
                .addValue("factoryId", game.getFactoryId())
                .addValue("boardSize", game.getBoardSize());

        jdbcTemplate.update(gameSql, gameParams);


        // 2. Supprimer les anciennes données associées
        // avant de les réinsérer
        String deletePlayersSql = """
        DELETE FROM game_players
        WHERE game_id = :gameId
        """;

        jdbcTemplate.update(
                deletePlayersSql,
                new MapSqlParameterSource()
                        .addValue("gameId", game.getId().toString())
        );


        String deleteTokensSql = """
        DELETE FROM game_tokens
        WHERE game_id = :gameId
        """;

        jdbcTemplate.update(
                deleteTokensSql,
                new MapSqlParameterSource()
                        .addValue("gameId", game.getId().toString())
        );


        // 3. Enregistrer les joueurs
        String playerSql = """
        INSERT INTO game_players (game_id, player_id)
        VALUES (:gameId, :playerId)
        """;

        for (UUID playerId : game.getPlayerIds()) {

            MapSqlParameterSource playerParams = new MapSqlParameterSource()
                    .addValue("gameId", game.getId().toString())
                    .addValue("playerId", playerId.toString());

            jdbcTemplate.update(playerSql, playerParams);
        }


        // 4. Enregistrer les tokens présents sur le plateau
        String tokenSql = """
        INSERT INTO game_tokens
            (game_id, owner_id, name, x, y, state)
        VALUES
            (:gameId, :ownerId, :name, :x, :y, :state)
        """;

        for (Token token : game.getBoard().values()) {

            CellPosition position = token.getPosition();

            MapSqlParameterSource tokenParams = new MapSqlParameterSource()
                    .addValue("gameId", game.getId().toString())
                    .addValue(
                            "ownerId",
                            token.getOwnerId()
                                    .map(UUID::toString)
                                    .orElse(null)
                    )
                    .addValue("name", token.getName())
                    .addValue("x", position.x())
                    .addValue("y", position.y())
                    .addValue("state", "BOARD");

            jdbcTemplate.update(tokenSql, tokenParams);
        }


        // 5. Enregistrer les tokens restants
        for (Token token : game.getRemainingTokens()) {

            MapSqlParameterSource tokenParams = new MapSqlParameterSource()
                    .addValue("gameId", game.getId().toString())
                    .addValue(
                            "ownerId",
                            token.getOwnerId()
                                    .map(UUID::toString)
                                    .orElse(null)
                    )
                    .addValue("name", token.getName())
                    .addValue("x", null)
                    .addValue("y", null)
                    .addValue("state", "REMAINING");

            jdbcTemplate.update(tokenSql, tokenParams);
        }


        // 6. Enregistrer les tokens retirés
        for (Token token : game.getRemovedTokens()) {

            MapSqlParameterSource tokenParams = new MapSqlParameterSource()
                    .addValue("gameId", game.getId().toString())
                    .addValue(
                            "ownerId",
                            token.getOwnerId()
                                    .map(UUID::toString)
                                    .orElse(null)
                    )
                    .addValue("name", token.getName())
                    .addValue("x", null)
                    .addValue("y", null)
                    .addValue("state", "REMOVED");

            jdbcTemplate.update(tokenSql, tokenParams);
        }
    }

    @Override
    public void delete(String gameId) {
        String sql = "delete from games where id = :gameId";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("gameId", gameId);

        jdbcTemplate.update(sql, params);
    }
}
