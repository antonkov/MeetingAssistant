package com.zumzoom.meetingassistant;

import org.python.core.PyByteArray;
import org.python.core.PyObject;
import org.python.core.PyTuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.python.util.PythonInterpreter;

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

                Process p = Runtime.getRuntime().exec("python sound_to_text.py audio.dat res.txt");
                p.waitFor();
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("res.txt")));
                String text = br.readLine();
                String[] soffs = br.readLine().split(" ");
                ArrayList<Integer> offs = new ArrayList<>();
                for (String off : soffs) offs.add(Integer.parseInt(off));
                update.setText(text);
                update.sslolOffsets(offs);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            PythonInterpreter interpreter = new PythonInterpreter();
            interpreter.exec("import .");
            PyObject someFunc = interpreter.get("sound_to_text");
            PyTuple result = (PyTuple) someFunc.__call__(new PyByteArray(event.getAudio()));
            Object[] tmp = result.toArray();
            System.out.println((String) tmp[1]);
        }
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
