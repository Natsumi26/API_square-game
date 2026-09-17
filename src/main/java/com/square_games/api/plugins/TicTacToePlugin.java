package com.square_games.api.plugins;

import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.Token;
import fr.le_campus_numerique.square_games.engine.tictactoe.TicTacToeGameFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Component
public class TicTacToePlugin implements GamePlugin {

    private final MessageSource messageSource;

    private final TicTacToeGameFactory gameFactory;

    @Value("${game.tictactoe.default-player-count}")
    private Integer defaultPlayerCount;

    @Value("${game.tictactoe.default-board-size}")
    private Integer defaultBoardSize;

    public TicTacToePlugin(MessageSource messageSource, TicTacToeGameFactory gameFactory) {
        this.messageSource = messageSource;
        this.gameFactory = gameFactory;
    }

    @Override
    public Game createGame(Integer playerCount, Integer boardSize, Set<UUID> playerIds) {
        if (playerCount == null) {
            playerCount = defaultPlayerCount;
        }

        if(boardSize == null) {
            boardSize = defaultBoardSize;
        }

        return gameFactory.createGame(boardSize, playerIds);
    }

    @Override
    public String getName(Locale locale) {

        return messageSource.getMessage("game.tictactoe.name", null, locale);
    }

    @Override
    public Set<CellPosition> getAllowedMoves(Game game, CellPosition position) {
        return game.getRemainingTokens()
                .stream()
                .filter(Token::canMove)
                .findFirst()
                .map(Token::getAllowedMoves)
                .orElseThrow(() ->
                        new IllegalArgumentException("Aucun jeton ne peut jouer")
                );
    }

    @Override
    public String getGameType() {
        return gameFactory.getGameFactoryId();
    }

    @Override
    public Token getTokenToMove(Game game, CellPosition position) {
        return game.getRemainingTokens()
                .stream()
                .filter(Token::canMove)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Aucun jeton ne peut jouer")
                );
    }
}
