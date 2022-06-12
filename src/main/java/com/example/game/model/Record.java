package com.example.game.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor

public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="GAME_ID")
    private Game game;
    private LocalDateTime time;

    private String answer;

    public Record(Game game, LocalDateTime time, String answer) {
        this.game = game;
        this.time = time;
        this.answer = answer;
    }
}
