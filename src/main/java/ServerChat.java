import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

public class ServerChat {
    protected List<Message> chat = new CopyOnWriteArrayList<>();

    public void add(int id, String speaker, LocalDateTime dt, String message) {
        if (speaker == null) {
            speaker = "anonymous#" + Thread.currentThread().getName();
        }
        chat.add(new Message(id, speaker, dt, message));
    }

    public String getFromDt() {
        return chat.stream().reduce("",
                (x,y)-> {
                    String mtxt;
                    if (y.name.equals("server")) mtxt = "[" + y.name + "] " + y.message;
                    else mtxt = "[" + y.name + " : " + y.dt.toString() + "] " + y.message;
                    return x + mtxt + "\r\n";
                },
                (x, y)->x+y);
    }

    public String getFromDt(LocalDateTime dt, int id) {
        Stream<Message> stream = chat.stream()
                .filter(m -> (m.dt.isAfter(dt) || m.dt.isEqual(dt)) && m.id != id);
        String s = stream.reduce("",
                (x,y)-> {
                    String mtxt;
                    if (y.name.equals("server")) mtxt = "[" + y.name + "] " + y.message;
                    else mtxt = "[" + y.name + " : " + y.dt.toString() + "] " + y.message;
                    return x + mtxt + "\r\n";
                },
                (x, y)->x+y);
        return s;
    }
}
