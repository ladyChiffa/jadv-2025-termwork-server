import java.time.LocalDateTime;

public class Message {
    public int id;
    public String name;
    public String message;
    public LocalDateTime dt;

    public Message(int id, String name, LocalDateTime dt, String message) {
        this.id = id;
        this.name = name;
        this.message = message;
        this.dt = dt;
    }
}
