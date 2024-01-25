

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.net.Socket;
import java.net.SocketAddress;

import java.time.LocalDateTime;
import java.io.FileWriter;
import java.util.concurrent.Semaphore;


public class ServerSocket {

        // Objetos para conexion
        private java.net.ServerSocket serverSocket;
        private Socket socket;
        private InputStream is;
        private OutputStream os;

        // Objetos para envío de cadenas
        private InputStreamReader isr;
        private BufferedReader br;
        private PrintWriter pw;

        public ServerSocket(int puerto) throws IOException {
            serverSocket = new java.net.ServerSocket(puerto);
        }



        // igual que en el ejemplo anterior
        public void stop() throws IOException {

            is.close();
            os.close();
            socket.close();
            serverSocket.close();

        }

        // abrimos los canales de lectura y de escritura
        public void abrirCanalesDeTexto() {
            //Canales de lectura
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            //Canales de escritura
            pw = new PrintWriter(os, true);
        }

        // cerramos los canales de lectura y de escritura
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

        public void guardarMensajeTexto(String mensaje) {

            try {
                FileWriter fw = new FileWriter("chat.txt", true);
                fw.write("\r\n"+mensaje);
                fw.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        public String identificarHost (SocketAddress IPcliente) {
            String cliente=IPcliente.toString();

            //Buscamos la posicion del tercer punto
            int puntos=0;
            int pos3punto=0;
            int i=0;
            while (puntos<3) {
                if(cliente.charAt(i) == '.') {
                    puntos++;
                    pos3punto=i;
                }
                i++;
            }
            //Buscamos la posicion de los dos puntos
            int posDosPuntos=cliente.indexOf(":");
            cliente=cliente.substring(pos3punto+1,posDosPuntos);

            return cliente;
        }

        private final int MAX_CLIENTS = 10;
        private final Semaphore semaphore = new Semaphore(MAX_CLIENTS);



        private void startServer() {
            try {
                java.net.ServerSocket serverSocket = new java.net.ServerSocket(49175);
                System.out.println("Sala Abierta.");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println(" (Servidor) Conexión establecida con cliente " + clientSocket.getRemoteSocketAddress());

                    // Intenta adquirir un permiso del semáforo antes de crear un nuevo hilo
                    try {
                        semaphore.acquire();
                        // Crea un nuevo hilo para manejar la conexión con el cliente
                        ClientHandler clientHandler = new ClientHandler(clientSocket);
                        new Thread(clientHandler).start();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } finally {
                        // Libera el permiso del semáforo, incluso si ocurre una excepción
                        semaphore.release();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(49176);
            serverSocket.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    }
