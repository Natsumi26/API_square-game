package com.square_games.api.services;


import com.square_games.api.DTO.GameCreationParams;
import com.square_games.api.DTO.GameResponseDto;
import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.GameStatus;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;


public interface GameService {

    Game createGame(UUID userId, GameCreationParams params);

    GameResponseDto getGameById(UUID userId, String gameId);

    GameStatus getGameStatus(UUID userId,String gameId);

    Collection<Game> getGames(UUID userId);

    void deleteGameById(UUID userId,String gameId);

    Collection<Game> getOngoingGames(UUID userId);

    Set<CellPosition> getAllowedMoves(UUID userId, String gameId, CellPosition position);

    void playMove(UUID userId, String gameId, CellPosition tokenPosition, CellPosition targetPosition);
}
