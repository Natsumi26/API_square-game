package com.square_games.api.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @MessageMapping("/test")
    @SendTo("/topic/test")
    public String test(String message) {
        return "serveur reçu : " +message;
    }
}
