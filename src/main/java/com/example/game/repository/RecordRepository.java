package com.example.game.repository;

import com.example.game.model.Game;
import com.example.game.model.Record;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends CrudRepository<Record,Long> {
   List<Record> findRecordsByGame(Game game);
}
