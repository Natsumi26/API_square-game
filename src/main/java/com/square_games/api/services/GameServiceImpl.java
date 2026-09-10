package com.square_games.api.services;

import com.square_games.api.DTO.GameCreationParams;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.GameFactory;
import fr.le_campus_numerique.square_games.engine.GameStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private final Map<UUID, Game> games = new HashMap<>();
    private final Map<String, GameFactory> gameFactories;

    public GameServiceImpl(List<GameFactory> gameFactories) {
        this.gameFactories = gameFactories.stream()
                .collect(Collectors.toMap(
                        GameFactory::getGameFactoryId,
                        Function.identity()
                ));
    }


    @Override
    public Game createGame(GameCreationParams params) {

        GameFactory factory = gameFactories.get(params.getType());

        if(factory == null) {
            throw new IllegalArgumentException(
                    "unknown game type: " + params.getType()
            );
        }

        Game game = factory.createGame(
                params.getPlayerCount(),
                params.getBoardSize()
        );

        games.put(game.getId(), game);

        return game;
    }

    @Override
    public Game getGameById(UUID gameId) {
        return games.get(gameId);
    }

    @Override
    public GameStatus getGameStatus(UUID gameId) {
        Game game = getGameById(gameId);
        return game.getStatus();
    }
    @Override
    public Collection<Game> getGames(){
        return games.values();
    }

    @Override
    public String deleteGameById(UUID gameId) {
        games.remove(gameId);
        return "Game deleted";
    }

}
