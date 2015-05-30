package com.zumzoom.meetingassistant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventRestController {

    @Autowired
    private EventRepository repository;

    @RequestMapping(method=RequestMethod.GET)
    public @ResponseBody List<Event> handleGetEvent(@RequestParam("user") String user){
        return repository.findEventsByUser(user);
    }

    @RequestMapping(method=RequestMethod.POST)
    public @ResponseBody Event handleCreateEvent(@RequestBody Event event){
        return repository.save(event);
    }

    @RequestMapping(method=RequestMethod.DELETE, value="{id}")
    public @ResponseBody void handleDeleteEvent(@PathVariable String id){
        repository.delete(id);
    }

    @RequestMapping(method=RequestMethod.PUT, value="{id}")
    public @ResponseBody Event handleUpdateEvent(@PathVariable String id,
                                                  @RequestBody Event event){
        Event update = repository.findOne(id);
        update.setAudio(event.getAudio());
        update.setDate(event.getDate());
        update.setText(event.getText());
        update.setTitle(event.getTitle());
        update.setUsers(event.getUsers());
        return repository.save(update);
    }
}
