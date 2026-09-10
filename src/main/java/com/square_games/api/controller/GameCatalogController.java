package com.square_games.api.controller;

import com.square_games.api.service.GameCatalog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/games")
public class GameCatalogController {

    private final GameCatalog gameCatalog;

    public GameCatalogController(GameCatalog gameCatalog) {
        this.gameCatalog = gameCatalog;
    }

    @GetMapping
    public Collection<String> getGames() {
        return gameCatalog.getAvailableGames();
    }
}
