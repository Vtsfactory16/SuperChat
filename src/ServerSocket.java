

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
        public SocketAddress start () throws IOException {
            //System.out.println(" (Servidor) Esperando conexiones...");
            socket=serverSocket.accept();
            is = socket.getInputStream();
            os = socket.getOutputStream();
            System.out.println(" (Servidor) Conexión establecida con cliente "+socket.getRemoteSocketAddress());
            SocketAddress sa = socket.getRemoteSocketAddress();
            return(sa);
        }

        // igual que en el ejemplo anterior
        public void stop() throws IOException {
            //System.out.println(" (Servidor) Cerrando conexiones...");
            is.close();
            os.close();
            socket.close();
            serverSocket.close();
            //System.out.println (" (Servidor) Conexiones cerradas.");
        }

        // abrimos los canales de lectura y de escritura
        public void abrirCanalesDeTexto() {
            //System.out.println(" (Servidor) Abriendo canales de texto...");
            //Canales de lectura
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);
            //Canales de escritura
            pw = new PrintWriter(os, true);
            //System.out.println("(Servidor) Cerrando canales de texto.");
        }

        // cerramos los canales de lectura y de escritura
        public void cerrarCanalesDeTexto() throws IOException {
            //System.out.println(" (Servidor) Cerrando canales de texto...");
            //Canales de lectura
            br.close();
            isr.close();
            //Canales de escritura
            pw.close();
            //System.out.println("(Servidor) Cerrando canales de texto.");
        }

        public String leerMensajeTexto()  throws IOException {
            //System.out.println(" (Servidor) Leyendo mensaje...");
            String mensaje = br.readLine();
            //System.out.println(" (Servidor) Mensaje leido.");
            return mensaje;
        }

        public void enviarMensajeTexto(String mensaje) {
            //System.out.println(" (Servidor) Enviando mensaje...");
            pw.println(mensaje);
            //System.out.println(" (Servidor) Mensaje enviado.");
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

        public static void main (String[] args) {

            String mensaje;
            try {
                //Iniciamos
                ServerSocket servidor = new ServerSocket(49175);
                System.out.println("Sala Abierta.");

                do {
                    SocketAddress IPcliente = servidor.start();
                    String host = servidor.identificarHost(IPcliente);
                    servidor.abrirCanalesDeTexto();

                    // Tomamos la hora en la que se produce la interaccion
                    LocalDateTime horaLocal = LocalDateTime.now();
                    int horas  = horaLocal.getHour();
                    int minutos = horaLocal.getMinute();
                    int segundos = horaLocal.getSecond();

                    //Recepcion del mensaje del cliente
                    mensaje = servidor.leerMensajeTexto();
                    String salida=horas+":"+minutos+":"+segundos+" - Host "+host+": "+mensaje;
                    System.out.println(salida);
                    servidor.guardarMensajeTexto(salida);

                    //Envío de la confirmacion del mensaje al cliente
                    servidor.enviarMensajeTexto("ACK "+horas+":"+minutos+":"+segundos);

                    //Cerramos el canal
                    servidor.cerrarCanalesDeTexto();

                } while (!mensaje.equals("close"));

                //Cerramos el socket
                servidor.stop();
                System.out.println("Sala Cerrada.");

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

}