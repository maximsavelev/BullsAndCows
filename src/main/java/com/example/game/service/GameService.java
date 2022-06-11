package com.example.game.service;

import com.example.game.exception.EntityNotFound;
import com.example.game.model.Game;
import com.example.game.model.Player;
import com.example.game.repository.GameRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public void saveGame(Game game) {
        gameRepository.save(game);
    }

    public Game findGameById(Long id) {
       return gameRepository.findById(id).orElseThrow(EntityNotFound::new);
    }

    public boolean isPlayerHasMultipleGames(Player player) {
        System.out.println(gameRepository.countByPlayer(player));
        return gameRepository.countByPlayer(player) > 1;
    }

    public boolean existsById(Long id){
       return gameRepository.existsById(id);
    }

    public List<Game> findGamesByPlayer(Player player) {
        return gameRepository.findGameByPlayer(player);
    }

}
