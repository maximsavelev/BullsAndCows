package com.example.game.repository;

import com.example.game.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Long> {
    Player findPlayerByUsername(String username);
    boolean existsPlayerByUsername(String username);
}
