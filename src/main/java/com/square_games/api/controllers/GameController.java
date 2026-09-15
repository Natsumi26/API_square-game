package com.square_games.api.controllers;

import com.square_games.api.DTO.GameCreationParams;
import com.square_games.api.DTO.MoveParams;
import com.square_games.api.services.GameService;
import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.GameStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public Collection<Game> getGames(@RequestHeader("X-UserId") UUID userId) {
        return gameService.getGames(userId);
    }

    @PostMapping
    public Game createGame(@RequestBody GameCreationParams params, @RequestHeader("X-UserId") UUID userId) {
        return gameService.createGame(userId, params);
    }

    @GetMapping("/{gameId}")
    public Game getGame(@PathVariable String gameId,@RequestHeader("X-UserId") UUID userId) {
        return gameService.getGameById(userId, gameId);
    }

    @GetMapping("/status/{gameId}")
    public GameStatus getGameStatus(@PathVariable String gameId,@RequestHeader("X-UserId") UUID userId) {
        return gameService.getGameStatus(userId,gameId);
    }

    @GetMapping("/ongoing")
    public Collection<Game> getOngoingGames(@RequestHeader("X-UserId") UUID userId) {
        return gameService.getOngoingGames(userId);
    }

    @DeleteMapping("/{gameId}")
    public void deleteGame(@PathVariable String gameId,@RequestHeader("X-UserId") UUID userId) {
        gameService.deleteGameById(userId, gameId);
    }

    @GetMapping("/{gameId}/tokens/{x}/{y}/possiblemoves")
    public Set<CellPosition> getAllowedMoves(@RequestHeader("X-UserId") UUID userId, @PathVariable String gameId, @PathVariable int x, @PathVariable int y) {
        CellPosition position = new CellPosition(x, y);

        return gameService.getAllowedMoves(
                userId,
                gameId,
                position
        );
    }

    @PostMapping("/{gameId}/moves")
    public void playMove(@RequestHeader("X-UserId") UUID userId, @PathVariable String gameId, @RequestBody MoveParams params) {
        gameService.playMove(
                userId,
                gameId,
                params.getFrom(),
                params.getTo()
        );
    }

}
