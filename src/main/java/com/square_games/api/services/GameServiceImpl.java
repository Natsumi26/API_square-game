package com.square_games.api.services;

import com.square_games.api.DTO.GameCreationParams;
import com.square_games.api.plugins.GamePlugin;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.GameStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameServiceImpl implements GameService  {

    private final Map<UUID, Game> games = new HashMap<>();
    private final List<GamePlugin> gamePlugins;

    public GameServiceImpl(List<GamePlugin> gamePlugins) {
        this.gamePlugins = gamePlugins;
    }


    @Override
    public Game createGame(GameCreationParams params) {

        GamePlugin plugin = gamePlugins.stream()
                .filter(p -> p.getClass().getSimpleName()
                        .equalsIgnoreCase(params.getType() + "Plugin"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Type de jeu inconnu"));

        Game game = plugin.createGame(params.getPlayerCount(),  params.getBoardSize());

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
