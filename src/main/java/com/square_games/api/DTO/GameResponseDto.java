package com.square_games.api.DTO;

import fr.le_campus_numerique.square_games.engine.Game;

import java.util.UUID;

public class GameResponseDto {
    private Game game;
    private UUID winnerId;

    public GameResponseDto(Game game, UUID winnerId) {
        this.game = game;
        this.winnerId = winnerId;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public UUID getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(UUID winnerId) {
        this.winnerId = winnerId;
    }
}
