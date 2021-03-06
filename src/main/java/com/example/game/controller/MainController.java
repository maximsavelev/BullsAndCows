package com.example.game.controller;

import com.example.game.constants.MessageConstants;
import com.example.game.exception.AttemptsEndedException;
import com.example.game.exception.EntityNotFound;
import com.example.game.exception.TimeOutException;
import com.example.game.model.Record;
import com.example.game.model.*;
import com.example.game.service.GameService;
import com.example.game.service.PlayerService;
import com.example.game.service.RecordService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
@AllArgsConstructor
public class MainController {


    private final GameService gameService;
    private final RecordService recordService;

    private final PlayerService playerService;

    @GetMapping("/start")
    public String startGame(Model model) {
        model.addAttribute("player", new Player());
        return "start";

    }
    @PostMapping("/start")
    public String startGame(@ModelAttribute("player") Player player,
                            RedirectAttributes redirectAttributes) throws IOException {
        String username = player.getUsername();
        if (playerService.existByUsername(username)) {
            player = playerService.findPlayerByUsername(username);
        } else {
            playerService.savePlayer(player);
        }
        Game game = gameService.createGameFromPropertiesFile(player);
        gameService.saveGame(game);
        redirectAttributes.addFlashAttribute("wrapper", new Wrapper());
        return "redirect:/game/" + game.getId();
    }

    @GetMapping("/game/{id}")
    public String game(@PathVariable Long id, @ModelAttribute("wrapper") Wrapper wrapper) {
        if (!gameService.existsById(id) || wrapper == null) {
            throw new EntityNotFound("The game not found");
        }
        return "game";
    }

    @PostMapping("/game/{id}")
    public String game(@ModelAttribute("wrapper") Wrapper wrapper, Model model,
                       RedirectAttributes redirectAttributes, @PathVariable Long id) throws Exception {
        Game game = gameService.findGameById(id);
        String guess;
        try {
            guess = game.makeGuess(wrapper.getGuest().trim());
            wrapper.setAnswer(guess);
            gameService.saveGame(game);
            recordService.saveRecord(new Record(game, LocalDateTime.now(), wrapper.getGuest()));
            if (game.getGameStatus().equals(GameStatus.WON)) {
                redirectAttributes.addFlashAttribute("message", MessageConstants.WIN_MESSAGE);
                redirectAttributes.addFlashAttribute("game", game);
                return "redirect:/result/" + id;
            }
            model.addAttribute("wrapper", wrapper);
            return "game";

        } catch (TimeOutException | AttemptsEndedException e) {
            gameService.saveGame(game);
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            redirectAttributes.addFlashAttribute("game", game);
            recordService.saveRecord(new Record(game, LocalDateTime.now(), e.getMessage()));
            return "redirect:/result/" + id;
        }

    }
    @GetMapping("/result/{id}")
    public String result(@ModelAttribute("game") Game game, Model model) {
        Boolean playerHasMultipleGames = gameService.isPlayerHasMultipleGames(game.getPlayer());
        model.addAttribute("count", playerHasMultipleGames);
        model.addAttribute("games", gameService.findGamesByPlayer(game.getPlayer()));
        return "result";
    }
}
