package com.example.game.controller;

import com.example.game.exception.AttemptsEndedException;
import com.example.game.exception.EntityNotFound;
import com.example.game.exception.TImeOutException;
import com.example.game.model.Record;
import com.example.game.model.*;
import com.example.game.service.GameService;
import com.example.game.service.PlayerService;
import com.example.game.service.RecordService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Properties;

@Controller
@AllArgsConstructor
public class MainController {


    private final GameService gameService;
    private final RecordService recordService;

    private final PlayerService playerService;


    private final String GAME_SETTINGS_PROPERTIES = "game-settings.properties";
    private final String MESSAGE_PROPERTIES = "message.properties";


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
        Game game = gameService.createGameFromPropertiesFile(GAME_SETTINGS_PROPERTIES, player);
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
        Properties props = PropertiesLoaderUtils.loadAllProperties(MESSAGE_PROPERTIES);
        Game game = gameService.findGameById(id);
        String guess;
        try {
            guess = game.makeGuess(wrapper.getGuest().trim());
            wrapper.setAnswer(guess);
            recordService.saveRecord(new Record(game, LocalDateTime.now(), wrapper.getGuest()));
            if (game.getGameStatus().equals(GameStatus.WON)) {
                redirectAttributes.addFlashAttribute("message", props.getProperty("win_message"));
                redirectAttributes.addFlashAttribute("game", game);
                return "redirect:/result/" + id;
            }
            model.addAttribute("wrapper", wrapper);
            return "game";

        } catch (TImeOutException e) {
            redirectAttributes.addFlashAttribute("message", props.getProperty("no_time_message"));
            redirectAttributes.addFlashAttribute("game", game);
            recordService.saveRecord(new Record(game, LocalDateTime.now(), "error"));
            return "redirect:/result/" + id;

        } catch (AttemptsEndedException e) {
            redirectAttributes.addFlashAttribute("message", props.getProperty("no_attempts_message"));
            redirectAttributes.addFlashAttribute("game", game);
            recordService.saveRecord(new Record(game, LocalDateTime.now(), "error"));
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
