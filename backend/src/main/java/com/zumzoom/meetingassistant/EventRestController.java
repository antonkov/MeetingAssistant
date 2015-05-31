package com.zumzoom.meetingassistant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


@ResponseStatus(value = HttpStatus.NOT_FOUND)
class ResourceNotFoundException extends RuntimeException {

}

@RestController
@RequestMapping("/events")
public class EventRestController {

    @Autowired
    private EventRepository repository;

    @RequestMapping(method=RequestMethod.GET, value="{id}")
    public @ResponseBody Event handleGetEvent(@PathVariable String id){
        return repository.findOne(id);
    }

    @RequestMapping(method=RequestMethod.GET)
    public @ResponseBody List<Event> handleGetEvents(@RequestParam(value = "user") String user){
        return repository.findEventsByUser(user);
    }

    @RequestMapping(method=RequestMethod.POST)
    public @ResponseBody Event handleCreateEvent(@RequestBody Event event){
        System.out.println(event.getAudio().length);
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
        if(update == null)
            throw new ResourceNotFoundException();
        if(event.getAudio() != null) {
            update.setAudio(event.getAudio());
            update.setHasAudio(true);
            try {
                DataOutputStream os = new DataOutputStream(new FileOutputStream("./audio.dat"));
                os.write(event.getAudio());
                os.close();

                Process p = Runtime.getRuntime().exec("python3 sound_to_text.py audio.dat res.txt");
                int res = p.waitFor();
                Reader reader = new InputStreamReader(p.getErrorStream());
                int ch;
                while ((ch = reader.read()) != -1)
                    System.out.print((char) ch);
                reader.close();
                reader = new InputStreamReader(p.getInputStream());
                while ((ch = reader.read()) != -1)
                    System.out.print((char) ch);
                reader.close();

                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("res.txt")));
                String text = br.readLine();
                if(!text.isEmpty()) {
                    String offsets = br.readLine();
                    String[] soffs = offsets.split(" ");
                    ArrayList<Integer> offs = new ArrayList<>();
                    for (String off : soffs) offs.add(Integer.parseInt(off));
                    update.setText(text);
                    update.sslolOffsets(offs);
                } else {
                    update.setText(text);
                    update.sslolOffsets(new ArrayList<Integer>());
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(event.getDate() != null)
            update.setDate(event.getDate());
        if(event.getTitle() != null)
            update.setTitle(event.getTitle());
        if(event.getUsers() != null)
            update.setUsers(event.getUsers());
        return repository.save(update);
    }

    @RequestMapping(method=RequestMethod.GET, value="query")
    public @ResponseBody List<Integer> handleQueryEvent(@RequestParam(value = "query") String query,
                                                      @RequestParam(value = "id") String id){
        Event event = repository.findOne(id);
        if(event == null)
            throw new ResourceNotFoundException();
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream("./data.txt"));
            pw.println(event.getText());
            for(Integer off : event.gglolOffsets()) pw.print(off + " ");
            pw.close();

            Process p = Runtime.getRuntime().exec("python3 find_start.py data.txt \\\"" + query + "\\\" res.txt");
            int res = p.waitFor();
            Reader reader = new InputStreamReader(p.getErrorStream());
            int ch;
            while ((ch = reader.read()) != -1)
                System.out.print((char) ch);
            reader.close();
            reader = new InputStreamReader(p.getInputStream());
            while ((ch = reader.read()) != -1)
                System.out.print((char) ch);
            reader.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("res.txt")));
            String offsets = br.readLine();
            String[] soffs = offsets.split(" ");
            ArrayList<Integer> offs = new ArrayList<>();
            for (String off : soffs) {
                try {
                    offs.add(Integer.parseInt(off));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            return offs;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
