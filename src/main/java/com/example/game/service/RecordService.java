package com.example.game.service;

import com.example.game.model.Game;
import com.example.game.model.Record;
import com.example.game.repository.RecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class RecordService {
    private final RecordRepository recordRepository;

    public void saveRecord(Record record) {
        recordRepository.save(record);
    }

    public List<Record> findRecordsByGame(Game game) {
        return recordRepository.findRecordsByGame(game);
    }
}
