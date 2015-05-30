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
        if(event.getAudio() != null)
            update.setAudio(event.getAudio());
        if(event.getDate() != null)
            update.setDate(event.getDate());
        if(event.getText() != null)
            update.setText(event.getText());
        if(event.getTitle() != null)
            update.setTitle(event.getTitle());
        if(event.getUsers() != null)
            update.setUsers(event.getUsers());
        return repository.save(update);
    }
}