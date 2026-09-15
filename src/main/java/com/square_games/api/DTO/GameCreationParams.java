package com.square_games.api.DTO;

import java.util.List;
import java.util.UUID;

public class GameCreationParams {

    private String type;
    private Integer playerCount;
    private Integer boardSize;
    private List<UUID> opponentIds;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(Integer playerCount) {
        this.playerCount = playerCount;
    }

    public Integer getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(Integer boardSize) {
        this.boardSize = boardSize;
    }

    public List<UUID> getOpponentIds() {
        return opponentIds;
    }

    public void setOpponentIds(List<UUID> opponentIds) {
        this.opponentIds = opponentIds;
    }
}
