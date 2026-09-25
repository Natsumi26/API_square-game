package com.square_games.api.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameSocketService {

    private final SimpMessagingTemplate  messagingTemplate;

    public GameSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void notifyGameUpdate(String gameId, Object game) {

        messagingTemplate.convertAndSend(
                "/topic/games/" + gameId,
                game
        );
    }
}
