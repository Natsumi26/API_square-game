package com.square_games.api.dao;

import com.square_games.api.models.GameEntity;
import com.square_games.api.models.GameEntityRepository;
import com.square_games.api.models.GameTokenEntity;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.GameFactory;
import fr.le_campus_numerique.square_games.engine.InconsistentGameDefinitionException;
import fr.le_campus_numerique.square_games.engine.TokenPosition;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class JpaGameDao implements GameDao{

    private final GameEntityRepository gameEntityRepository;
    private final Map<String, GameFactory> gameFactories;

    public JpaGameDao(GameEntityRepository gameEntityRepository, List<GameFactory> gameFactories) {
        this.gameEntityRepository = gameEntityRepository;
        this.gameFactories = gameFactories.stream()
                .collect(Collectors.toMap(
                        GameFactory::getGameFactoryId,
                        Function.identity()
                ));
    }


    @Override
    public Collection<Game> findAll(UUID userId) {

        return gameEntityRepository.findAll()
                .stream()
                .map(this::toGame)
                .filter(game -> game.getPlayerIds().contains(userId))
                .toList();
    }

    @Override
    public Game findById(UUID userId, String gameId) {
        return gameEntityRepository.findById(gameId)
                .map(this::toGame)
                .orElse(null);
    }

    @Override
    public void upsert(UUID userId, Game game) {
        GameEntity entity = GameEntity.fromGame(game);
        System.out.println("===== SAUVEGARDE CONNECT FOUR =====");

        game.getBoard().forEach((position, token) -> {
            System.out.println(
                    "Position : " + position
                            + " | nom : " + token.getName()
                            + " | owner : " + token.getOwnerId()
            );
        });

        System.out.println("==================================");
        gameEntityRepository.save(entity);
    }

    @Override
    public void delete(UUID userId, String gameId) {
        gameEntityRepository.deleteById(gameId);
    }



    public Game toGame(GameEntity entity) {

        GameFactory factory = gameFactories.get(entity.factoryId);

        if (factory == null) {
            throw new IllegalStateException(
                    "Unknown game factory: " + entity.factoryId
            );
        }

        List<UUID> players = Arrays.stream(entity.playerIds.split(","))
                .map(UUID::fromString)
                .toList();

        List<TokenPosition<UUID>> boardTokens = entity.tokens
                .stream()
                .filter(token -> !token.removed)
                .map(this::toTokenPosition)
                .toList();

        List<TokenPosition<UUID>> removedTokens = entity.tokens.stream()
                .filter(token -> token.removed)
                .map(this::toTokenPosition)
                .toList();

        UUID gameId = UUID.fromString(entity.id);

        if ("connect4".equals(entity.factoryId)) {

            return ConnectFourGameReconstructed.reconstruct(
                    gameId,
                    new LinkedHashSet<>(players),
                    entity.tokens
            );
        }
        /*
         * Reconstruction spécifique du Taquin.
         */
        if ("15 puzzle".equals(entity.factoryId)) {

            if (players.size() != 1) {
                throw new IllegalStateException(
                        "Taquin must have exactly one player"
                );
            }

            List<TaquinGameReconstructed.TileData> tokens =
                    boardTokens.stream()
                            .map(token -> new TaquinGameReconstructed.TileData(
                                    token.tokenName(),
                                    token.x(),
                                    token.y()
                            ))
                            .toList();

            return new TaquinGameReconstructed(
                    gameId,
                    players.get(0),
                    entity.boardSize,
                    tokens
            );
        }


        /*
         * Reconstruction normale pour les autres jeux.
         */

        try {
            System.out.println("===== RECONSTRUCTION CONNECT FOUR =====");
            System.out.println("Players : " + players);

            boardTokens.forEach(token ->
                    System.out.println(
                            "Token : " + token.tokenName()
                                    + " | owner : " + token.owner()
                                    + " | position : (" + token.x() + "," + token.y() + ")"
                    )
            );

            System.out.println("======================================");


            return factory.createGameWithIds(
                    UUID.fromString(entity.id),
                    entity.boardSize,
                    players,
                    boardTokens,
                    removedTokens
            );

        } catch (InconsistentGameDefinitionException e) {

            throw new IllegalStateException("Unable to reconstruct game " + entity.id, e);
        }
    }

    private TokenPosition<UUID> toTokenPosition(GameTokenEntity token) {

        return new TokenPosition<>(
                UUID.fromString(token.ownerId),
                token.name,
                token.x,
                token.y
        );
    }
}
