

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientSocket {

    private String serverIP;
    private int serverPort;
    private Socket socket;
    private InputStream is;
    private OutputStream os;

    // Objetos para envío de cadenas
    private InputStreamReader isr;
    private BufferedReader br;
    private PrintWriter pw;

    public ClientSocket(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public void start () throws UnknownHostException, IOException {
        socket = new Socket(serverIP,serverPort);
        os = socket.getOutputStream();
        is = socket.getInputStream();

        // Abrimos los canales de lectura y de escritura
        abrirCanalesDeTexto();

        // Iniciamos el hilo de recepción de mensajes
        ClientThread receiverThread = new ClientThread(br);
        receiverThread.start();
    }

    public void stop () throws IOException {
        is.close();
        os.close();
        socket.close();
    }

    // abrimos los canales de lectura y de escritura - Igual que en el servidor
    public void abrirCanalesDeTexto() {
        //Canales de lectura
        isr = new InputStreamReader(is);
        br = new BufferedReader(isr);
        //Canales de escritura
        pw = new PrintWriter(os, true);
    }

    // cerramos los canales de lectura y de escritura - Igual que en el servidor
    public void cerrarCanalesDeTexto() throws IOException {
        //Canales de lectura
        br.close();
        isr.close();
        //Canales de escritura
        pw.close();
    }

    public String leerMensajeTexto()  throws IOException {
        String mensaje = br.readLine();
        return mensaje;
    }

    public void enviarMensajeTexto(String mensaje) {
        pw.println(mensaje);
    }


    public static void main (String[] args) {

        String mensaje;

        //Abrimos la comunicación con el puerto de servicio
        ClientSocket cliente = new ClientSocket("localhost",49175);
        try {
            do {
                //Abrimos la comunicación
                cliente.start();
                cliente.abrirCanalesDeTexto();

                //Enviar mensajes al servidor
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

                System.out.println("Mensaje a enviar (END para terminar): ");
                mensaje = br.readLine();
                cliente.enviarMensajeTexto(mensaje);

                //Recepción de la confirmacion
                String mensajeRecibido = cliente.leerMensajeTexto();
                System.out.println ( "Mensaje del servidor:"+mensajeRecibido);

                //Cerramos la comunicación
                cliente.cerrarCanalesDeTexto();

            } while (!mensaje.equals("END"));
            cliente.stop();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}