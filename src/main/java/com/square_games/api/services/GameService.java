package com.square_games.api.services;


import com.square_games.api.DTO.GameCreationParams;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.GameStatus;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface GameService {

    Game createGame(GameCreationParams params);

    Game getGameById(UUID gameId);

    GameStatus getGameStatus(UUID gameId);

    Collection<Game> getGames();

    String deleteGameById(UUID gameId);
}
