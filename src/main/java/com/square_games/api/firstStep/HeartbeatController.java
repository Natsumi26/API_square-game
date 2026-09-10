package com.square_games.api.firstStep;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HeartbeatController {

    @Autowired
    private HeartbeatSensor heartbeatSensor;

    @GetMapping("/heartbeat")
    public int random(){
        RandomHeartbeat randomHeartbeat = new RandomHeartbeat();
        return randomHeartbeat.get();
    }
}
