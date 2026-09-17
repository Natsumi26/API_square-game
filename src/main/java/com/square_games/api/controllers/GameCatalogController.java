package com.square_games.api.controllers;

import com.square_games.api.services.GameCatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Locale;

@RestController
@RequestMapping("/listegames")
public class GameCatalogController {

    private final GameCatalogService gameCatalogService;

    public GameCatalogController(GameCatalogService gameCatalogService) {
        this.gameCatalogService = gameCatalogService;
    }

    @GetMapping
    public Collection<String> getGames(Locale locale) {
        return gameCatalogService.getAvailableGames(locale);
    }
}