package com.square_games.api.DTO;

import fr.le_campus_numerique.square_games.engine.CellPosition;

public class MoveParams {

    private CellPosition from;
    private CellPosition to;

    public CellPosition getFrom() {
        return from;
    }

    public void setFrom(CellPosition from) {
        this.from = from;
    }

    public CellPosition getTo() {
        return to;
    }

    public void setTo(CellPosition to) {
        this.to = to;
    }
}
