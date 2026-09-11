package com.square_games.api.services;

import com.square_games.api.DTO.GameCreationParams;
import com.square_games.api.dao.GameDao;
import com.square_games.api.plugins.GamePlugin;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.GameStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameServiceImpl implements GameService  {

    private final GameDao gameDao;

    private final List<GamePlugin> gamePlugins;

    public GameServiceImpl(GameDao gameDao, List<GamePlugin> gamePlugins) {
        this.gameDao = gameDao;
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

        gameDao.upsert(game);

        return game;
    }

    @Override
    public Game getGameById(String gameId) {
        return gameDao.findById(gameId);
    }

    @Override
    public GameStatus getGameStatus(String gameId) {
        Game game = getGameById(gameId);
        return game.getStatus();
    }
    @Override
    public Collection<Game> getGames(){
        return gameDao.findAll();
    }

    @Override
    public void deleteGameById(String gameId) {
        gameDao.delete(gameId);
    }

}
