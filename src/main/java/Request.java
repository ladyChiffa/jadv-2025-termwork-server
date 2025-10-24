import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;

public class Request {
    public String command;
    public String text;

    public Request (BufferedReader in) throws IOException {
        String message = in.readLine();

        if (message.substring(0, 1).equals("/")) {
            int commandIndex = message.indexOf(' ');
            if (commandIndex == -1) {
                command = message;
            }
            else {
                command = message.substring(0, commandIndex);
                text = message.substring(commandIndex + 1);
            }
        }
        else {
            command = "/say";
            text = message;
        }

    }
}
