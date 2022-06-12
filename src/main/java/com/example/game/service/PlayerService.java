package com.example.game.service;

import com.example.game.model.Player;
import com.example.game.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    public void savePlayer(Player player) {

        playerRepository.save(player);
    }

    public Player findPlayerByUsername(String username){
      return  playerRepository.findPlayerByUsername(username);

    }

    public boolean existByUsername(String username) {
        return playerRepository.existsPlayerByUsername(username);
    }

}
