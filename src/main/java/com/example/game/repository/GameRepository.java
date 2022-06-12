package com.example.game.repository;

import com.example.game.model.Game;
import com.example.game.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface GameRepository extends CrudRepository<Game, Long> {
    int countByPlayer(Player player);

    List<Game> findGameByPlayer(Player player);

}
