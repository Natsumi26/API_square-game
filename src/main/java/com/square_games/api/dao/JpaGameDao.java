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
    public Collection<Game> findAll() {

        return gameEntityRepository.findAll()
                .stream()
                .map(this::toGame)
                .toList();
    }

    @Override
    public Game findById(String gameId) {
        return gameEntityRepository.findById(gameId)
                .map(this::toGame)
                .orElse(null);
    }

    @Override
    public void upsert(Game game) {
        GameEntity entity = GameEntity.fromGame(game);
        gameEntityRepository.save(entity);
    }

    @Override
    public void delete(String gameId) {
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

        try {

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
