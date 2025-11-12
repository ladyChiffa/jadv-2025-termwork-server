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

    protected void processHello(ServerChat chatlog, LocalDateTime dt, Request request) {
        if (name == null) {
            chatlog.add(0, "server", dt, "К чату присоединился " + request.text);
        }
        else if (!name.equals(request.text)) {
            chatlog.add(0,"server", dt, "Участник " + name + " сменил имя, теперь он " + request.text);
        }
        name = request.text;
    }

    protected void processExit(ServerChat chatlog, LocalDateTime dt, Request request) {
        if (name == null) {
            name = "anonymous#" + Thread.currentThread().getName();;
            chatlog.add(0, "server", dt, "К чату присоединился " + name);
        }
        chatlog.add(0,"server", dt, "Из чата вышел " + name);
    }

    protected void processPoll(ServerChat chatlog, LocalDateTime dt, Request request, BufferedOutputStream out) throws IOException {
        String newMessages = chatlog.getFromDt(lastRequestTime, id);
        lastRequestTime = dt;
        newMessages += "\n";
        out.write((
                newMessages
        ).getBytes());
        out.flush();
    }
    protected void processPollAll(ServerChat chatlog, LocalDateTime dt, Request request, BufferedOutputStream out) throws IOException {
        String newMessages = chatlog.getFromDt();
        out.write((
                newMessages
        ).getBytes());
        out.flush();
    }
    protected void processMessage(ServerChat chatlog, LocalDateTime dt, Request request){
        if (name == null) {
            name = "anonymous#" + Thread.currentThread().getName();;
            chatlog.add(0, "server", dt, "К чату присоединился " + name);
        }
        chatlog.add(id, name, dt, request.text);
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

            lastRequestTime = LocalDateTime.now();

            while(true) {
                LocalDateTime dt = LocalDateTime.now();
                Request request = new Request(in);

                if (request.command == null ) continue; // прислали пустую строку

                if (request.command.equals("/hello")) {
                    processHello(chatlog, dt, request);
                } else if (request.command.equals("/exit")) {
                    processExit(chatlog, dt, request);
                    break;
                } else if (request.command.equals("/poll")) {
                    processPoll(chatlog, dt, request, out);
                } else if (request.command.equals("/pollall")) { // недокументированная команда)))
                    processPollAll(chatlog, dt, request, out);
                } else {
                    processMessage(chatlog, dt, request);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
