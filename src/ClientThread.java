import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientThread extends Thread {
    private BufferedReader br;

    public ClientThread(BufferedReader br) {
        this.br = new BufferedReader(new BufferedReader(br));
    }

    @Override
    public void run() {
        try {
            String receivedMessage;
            while ((receivedMessage = br.readLine()) != null) {
                System.out.println("Mensaje del servidor: " + receivedMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
