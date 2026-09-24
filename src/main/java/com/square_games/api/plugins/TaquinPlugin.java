package com.square_games.api.plugins;

import fr.le_campus_numerique.square_games.engine.CellPosition;
import fr.le_campus_numerique.square_games.engine.Game;
import fr.le_campus_numerique.square_games.engine.Token;
import fr.le_campus_numerique.square_games.engine.taquin.TaquinGameFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
public class TaquinPlugin implements GamePlugin {

    private final MessageSource messageSource;

    private final TaquinGameFactory gameFactory;

    @Value("${game.taquin.default-player-count}")
    private Integer defaultPlayerCount;

    @Value("${game.taquin.default-board-size}")
    private Integer defaultBoardSize;

    public TaquinPlugin(MessageSource messageSource, TaquinGameFactory gameFactory) {
        this.messageSource = messageSource;
        this.gameFactory = gameFactory;
    }


    @Override
    public Game createGame(Integer playerCount, Integer boardSize, Set<UUID> playerIds) {
        if(playerCount == null){
            playerCount = defaultPlayerCount;
        }

        if(boardSize == null){
            boardSize = defaultBoardSize;
        }
        return gameFactory.createGame(boardSize, playerIds);
    }

    @Override
    public String getName(Locale locale) {
        return messageSource.getMessage("game.taquin.name", null, locale);
    }

    @Override
    public Set<CellPosition> getAllowedMoves(Game game, CellPosition position) {
        Token token = game.getBoard().get(position);
        if (token == null) {
            return Set.of();
        }
        return token.getAllowedMoves();
    }

    @Override
    public String getGameType() {
        return gameFactory.getGameFactoryId();
    }

    @Override
    public Token getTokenToMove(Game game, CellPosition position) {
        Token token = game.getBoard().get(position);
        if(token == null){
            throw new IllegalStateException("Aucun jeton à cette position");
        }

        return token;
    }

    @Override
    public Optional<UUID> getWinner(Game game) {
        return Optional.empty();
    }
}
