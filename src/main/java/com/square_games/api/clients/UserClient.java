package com.square_games.api.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Service
public class UserClient {

    private final RestClient restClient;
    private final String usersApiUrl;


    public UserClient(RestClient restClient,@Value("${users.api.url}") String usersApiUrl) {
        this.restClient = restClient;
        this.usersApiUrl = usersApiUrl;
    }

    public boolean isUserValid(UUID userId){
        return Boolean.TRUE.equals(restClient.get()
                .uri(usersApiUrl + "/users/" + userId + "/valid")
                .retrieve()
                .body(Boolean.class));
    }

}
