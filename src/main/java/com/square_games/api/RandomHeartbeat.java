package com.square_games.api;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomHeartbeat implements HeartbeatSensor {

    @Override
    public int get() {
        Random random = new Random();
        return random.nextInt(40,230);
    }
}
