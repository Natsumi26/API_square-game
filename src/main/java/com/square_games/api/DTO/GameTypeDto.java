package com.square_games.api.DTO;

public class GameTypeDto {
    private String id;
    private String name;

    public GameTypeDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
