package com.zumzoom.meetingassistant;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {

    @Query(value = "{ users : ?0 }", fields = "{ audio : ?1, text : ?2 }")
    public List<Event> findEventsByUser(String user, int includeAudio, int includeText);
}
