import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.LocalDateTime;

public class ClientChat {
    protected int id;
    protected String name = null;
    protected LocalDateTime lastRequestTime = null;

    public ClientChat (int id) {
        this.id = id;
    }

    public void process (ServerChat chatlog, Socket socket) {
        try (
                BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            out.write((
                    "Привет, подключение к чату установлено!~" +
                            "* представьтесь командой /hello т/и~" +
                            "* попрощайтесь командой /exit~\r\n"
            ).getBytes());
            out.flush();

            LocalDateTime dt_last = LocalDateTime.now();

            while(true) {
                LocalDateTime dt = LocalDateTime.now();
                Request request = new Request(in);

                if (request.command == null ) continue; // прислали пустую строку

                System.out.println("CHAT перед командой");
                System.out.println("DT PREV: " + dt_last);
                System.out.println(chatlog.toString());

                if (request.command.equals("/hello")) {
                    if (name == null) {
                        chatlog.add(0, "server", dt, "К чату присоединился " + request.text);
                    }
                    else if (!name.equals(request.text)) {
                        chatlog.add(0,"server", dt, "Участник " + name + " сменил имя, теперь он " + request.text);
                    }
                    name = request.text;
                } else if (request.command.equals("/exit")) {
                    if (name == null) {
                        name = "anonymous#" + Thread.currentThread().getName();;
                        chatlog.add(0, "server", dt, "К чату присоединился " + name);
                    }
                    chatlog.add(0,"server", dt, "Из чата вышел " + name);
                    break;
                } else if (request.command.equals("/poll")) {
                    String newMessages = chatlog.getFromDt(dt_last, id);
                    dt_last = dt;
                    newMessages += "\n";
                    out.write((
                            newMessages
                    ).getBytes());
                    out.flush();
                } else if (request.command.equals("/pollall")) { // недокументированная команда)))
                    String newMessages = chatlog.getFromDt();
                    out.write((
                            newMessages
                    ).getBytes());
                    out.flush();
                } else {
                    if (name == null) {
                        name = "anonymous#" + Thread.currentThread().getName();;
                        chatlog.add(0, "server", dt, "К чату присоединился " + name);
                    }
                    chatlog.add(id, name, dt, request.text);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
