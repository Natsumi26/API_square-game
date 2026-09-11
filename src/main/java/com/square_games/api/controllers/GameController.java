package com.square_games.api.controllers;

import com.square_games.api.DTO.GameCreationParams;
import com.square_games.api.services.GameService;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.GameStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public Collection<Game> getGames() {
        return gameService.getGames();
    }

    @PostMapping
    public Game createGame(@RequestBody GameCreationParams params) {
        return gameService.createGame(params);
    }

    @GetMapping("/{gameId}")
    public Game getGame(@PathVariable String gameId) {
        return gameService.getGameById(gameId);
    }

    @GetMapping("/status/{gameId}")
    public GameStatus getGameStatus(@PathVariable String gameId) {
        return gameService.getGameStatus(gameId);
    }

    @DeleteMapping("/{gameId}")
    public void deleteGame(@PathVariable String gameId) {
        gameService.deleteGameById(gameId);
    }

}
