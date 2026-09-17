package com.square_games.api.services;

import com.square_games.api.DTO.GameCreationParams;
import com.square_games.api.clients.UserClient;
import com.square_games.api.dao.GameDao;
import com.square_games.api.plugins.GamePlugin;
import fr.le_campus_numerique.square_games.engine.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class GameServiceImpl implements GameService  {

    private final GameDao gameDao;

    private final List<GamePlugin> gamePlugins;

    private final UserClient userClient;

    public GameServiceImpl(GameDao gameDao, List<GamePlugin> gamePlugins, UserClient userClient) {
        this.gameDao = gameDao;
        this.gamePlugins = gamePlugins;
        this.userClient = userClient;
    }


    @Override
    public Game createGame(UUID userId, GameCreationParams params) {

        if (!userClient.isUserValid(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Utilisateur inconnu"
            );
        }

        GamePlugin plugin = gamePlugins.stream()
                .filter(p -> p.getClass().getSimpleName()
                        .equalsIgnoreCase(params.getType() + "Plugin"))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Type de jeu inconnu"));

        Set<UUID> playerIds = new LinkedHashSet<>();
        playerIds.add(userId);

        if (params.getOpponentIds() != null) {
            playerIds.addAll(params.getOpponentIds());
        }

        if (playerIds.size() != params.getPlayerCount()) {
            throw new IllegalArgumentException(
                    "Le nombre de joueurs ne correspond pas au playerCount"
            );
        }

        Game game = plugin.createGame(params.getPlayerCount(),  params.getBoardSize(), playerIds );

        gameDao.upsert(userId, game);

        return game;
    }

    @Override
    public Game getGameById(UUID userId, String gameId) {
        if (!userClient.isUserValid(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Utilisateur inconnu"
            );
        }

        return gameDao.findById(userId, gameId);
    }

    @Override
    public GameStatus getGameStatus(UUID userId, String gameId) {
        if (!userClient.isUserValid(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Utilisateur inconnu"
            );
        }
        Game game = getGameById(userId, gameId);
        return game.getStatus();
    }
    @Override
    public Collection<Game> getGames(UUID userId){
        if (!userClient.isUserValid(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Utilisateur inconnu"
            );
        }
        return gameDao.findAll(userId);
    }

    @Override
    public void deleteGameById(UUID userId, String gameId) {
        if (!userClient.isUserValid(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Utilisateur inconnu"
            );
        }
        gameDao.delete(userId, gameId);
    }

    @Override
    public Collection<Game> getOngoingGames(UUID userId) {
        if (!userClient.isUserValid(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Utilisateur inconnu"
            );
        }
        return getGames(userId)
                .stream()
                .filter(game -> game.getStatus() == GameStatus.ONGOING)
                .toList();
    }

    @Override
    public Set<CellPosition> getAllowedMoves(UUID userId, String gameId, CellPosition position) {
        if (!userClient.isUserValid(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Utilisateur inconnu"
            );
        }
        Game game = gameDao.findById(userId, gameId);

        if (game == null) {
            throw new IllegalArgumentException("Partie inconnue");
        }
        GamePlugin plugin = gamePlugins.stream()
                .filter(p -> p.getGameType()
                        .equals(game.getFactoryId()))
                        .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Plugin de jeu inconnu")
                );

        return plugin.getAllowedMoves(game, position);
    }

    @Override
    public void playMove(UUID userId, String gameId, CellPosition tokenPosition, CellPosition targetPosition) {

        if (!userClient.isUserValid(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Utilisateur inconnu"
            );
        }

        Game game = gameDao.findById(userId, gameId);

        if (game == null) {
            throw new IllegalArgumentException("Partie inconnue");
        }

        if (!userId.equals(game.getCurrentPlayerId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Ce n'est pas votre tour"
            );
        }

        GamePlugin plugin = gamePlugins.stream()
                .filter(p -> p.getGameType()
                        .equals(game.getFactoryId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Plugin de jeu inconnu")
                );

        Token token = plugin.getTokenToMove(game, tokenPosition);

        try {
            token.moveTo(targetPosition);
        } catch (InvalidPositionException e) {
            System.out.println(e.getMessage());
        }

        gameDao.upsert(userId, game);
    }

}
