package com.square_games.api.models;

import jakarta.persistence.*;

@Entity
@Table
public class GameTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String ownerId;
    public String name;
    public boolean removed;
    public Integer x;
    public Integer y;
}
