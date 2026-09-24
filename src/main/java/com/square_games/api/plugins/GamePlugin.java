package com.square_games.api.plugins;

import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.Token;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface GamePlugin {
    Game createGame(Integer playerCount, Integer boardSize, Set<UUID> playerIds);
    String getName(Locale locale);
    Set<CellPosition> getAllowedMoves(Game game, CellPosition position);
    String getGameType();
    Token getTokenToMove(Game game, CellPosition position);
    Optional<UUID> getWinner(Game game);
}
