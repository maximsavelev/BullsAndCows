package com.example.game.model;

import com.example.game.constants.GameMessage;
import com.example.game.exception.AttemptsEndedException;
import com.example.game.exception.TImeOutException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
public class Game {

    private final String SYMBOLS = "0123456789";
    private final int COUNT_OF_SYMBOLS = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String guessedWord;

    @ManyToOne
    @JoinColumn(name = "PLAYER_ID")
    private Player player;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public List<Record> records = new ArrayList<>();

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private int attemptCount;

    private int timeSeconds;

    private GameMode mode;

    private GameStatus gameStatus;

    public Game(GameMode mode, Player player, int seconds, int attempts) {
        this.mode = mode;
        this.player = player;
        this.guessedWord = getSequence();
        this.gameStatus = GameStatus.UNFINISHED;
        this.startTime = LocalDateTime.now();

        switch (mode) {
            case ATTEMPT_LIMITED: {
                this.timeSeconds = Integer.MAX_VALUE;
                this.attemptCount = attempts;
                break;
            }
            case TIME_LIMITED: {
                this.timeSeconds = seconds;
                this.attemptCount = Integer.MAX_VALUE;
                break;
            }
            case UNLIMITED:
                this.timeSeconds = Integer.MAX_VALUE;
                this.attemptCount = Integer.MAX_VALUE;
                break;
        }

    }



    public String makeGuess(String guess) throws Exception {
        attemptCount--;
        isAttemptsEnded();
        isTimeOut();
        int bulls = 0;
        int cow = 0;
        for (int i = 0; i < guessedWord.length(); i++) {
            if (guessedWord.charAt(i) == guess.charAt(i)) {
                bulls++;
            } else {
                for (int j = 0; j < guess.length(); j++) {
                    if (guessedWord.charAt(i) == guess.charAt(j)) {
                        cow++;
                        break;
                    }
                }
            }
        }
        isGameEnded(bulls);
        return String.format(GameMessage.GAME_STATUS_MESSAGE, bulls, cow);
    }

    public String getCalculatedTime() {
        if(endTime==null) {
            return GameMessage.TIME_NOT_CALCULATED_MESSAGE;
        }
        long minutes = ChronoUnit.MINUTES.between(startTime, endTime);
        long seconds = ChronoUnit.SECONDS.between(startTime, endTime);
        if (minutes < 1) {
            return String.format(GameMessage.TIME_IN_SECONDS_MESSAGE,seconds);
        }
        return String.format(GameMessage.TIME_IN_MINUTES_AND_SECONDS_MESSAGE , minutes, (seconds - minutes * 60));
    }

    private String getSequence() {
        List<Character> chars = SYMBOLS.chars().mapToObj(e -> (char) e).collect(Collectors.toList());
        Collections.shuffle(chars);
        return chars.stream().map(Object::toString).limit(COUNT_OF_SYMBOLS).collect(Collectors.joining());
    }

    private void isTimeOut() throws TImeOutException {
        if (mode.equals(GameMode.TIME_LIMITED) &&
                ChronoUnit.SECONDS.between(LocalDateTime.now(), startTime.plusSeconds(timeSeconds)) < 0) {
            this.endTime = LocalDateTime.now();
            this.gameStatus = GameStatus.LOST;
            throw new TImeOutException(GameMessage.TIME_IS_OVER_MESSAGE);
        }
    }

    private void isAttemptsEnded() throws AttemptsEndedException {
        if (mode.equals(GameMode.ATTEMPT_LIMITED) && attemptCount < 0) {
            this.endTime = LocalDateTime.now();
            this.gameStatus = GameStatus.LOST;
            throw new AttemptsEndedException(GameMessage.ATTEMPTS_IS_ENDED_MESSAGE);
        }
    }

    private void isGameEnded(int bulls) {
        if (bulls == COUNT_OF_SYMBOLS) {
            this.endTime = LocalDateTime.now();
            this.gameStatus = GameStatus.WON;
        }
    }
}
