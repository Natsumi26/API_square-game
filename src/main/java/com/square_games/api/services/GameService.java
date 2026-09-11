package com.square_games.api.services;


import com.square_games.api.DTO.GameCreationParams;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.GameStatus;

import java.util.Collection;



public interface GameService {

    Game createGame(GameCreationParams params);

    Game getGameById(String gameId);

    GameStatus getGameStatus(String gameId);

    Collection<Game> getGames();

    void deleteGameById(String gameId);
}
