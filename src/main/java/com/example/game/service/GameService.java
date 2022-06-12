package com.example.game.service;

import com.example.game.exception.EntityNotFound;
import com.example.game.model.Game;
import com.example.game.model.GameMode;
import com.example.game.model.Player;
import com.example.game.repository.GameRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

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
        return gameRepository.countByPlayer(player) > 1;
    }

    public boolean existsById(Long id){
       return gameRepository.existsById(id);
    }

    public List<Game> findGamesByPlayer(Player player) {
        return gameRepository.findGameByPlayer(player);
    }

    public Game createGameFromPropertiesFile(String resourceName,Player player) throws IOException {
        Properties props = PropertiesLoaderUtils.loadAllProperties(resourceName);
        GameMode gameMode = GameMode.valueOf(props.getProperty("mode"));
        int attempts = Integer.parseInt(props.getProperty("attempts"));
        int time = Integer.parseInt(props.getProperty("time"));
        return   new Game(gameMode, player, time, attempts);
    }

}
