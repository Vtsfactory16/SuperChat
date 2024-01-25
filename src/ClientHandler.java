import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class ClientHandler implements Runnable{
    private Socket clientSocket;
    private BufferedReader br;
    private PrintWriter pw;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            InputStream is = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();

            // Objetos para envío de cadenas
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            PrintWriter pw = new PrintWriter(os, true);

            // Inicia el hilo de recepción de mensajes
            ClientThread receiverThread = new ClientThread(br);
            new Thread(receiverThread).start();

            // Tomamos la hora en la que se produce la interacción
            LocalDateTime horaLocal = LocalDateTime.now();
            int horas = horaLocal.getHour();
            int minutos = horaLocal.getMinute();
            int segundos = horaLocal.getSecond();

            do {
                // Recepcion del mensaje del cliente
                String mensaje = br.readLine();
                String salida = horas + ":" + minutos + ":" + segundos + " - Host " + clientSocket.getRemoteSocketAddress() + ": " + mensaje;
                System.out.println(salida);

                // Envío de la confirmación del mensaje al cliente
                pw.println("ACK " + horas + ":" + minutos + ":" + segundos);

            } while (true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

