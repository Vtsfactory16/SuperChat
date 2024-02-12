

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import javax.swing.JOptionPane;

/**
 * Clase en la que se maneja la comunicación del lado del servidor, esta clase
 * es la que se encarga de escuchar las conexiones de los clientes y de crear
 * un hilo para cada cliente que se conecta, dicho hilo es el que se encarga de
 * escuchar lo que el cliente dice y de enviar mensajes a dicho cliente.
 */
public class ServidorSocket extends Thread{
    /**
     * Socket utilizado para escuchar las conexiones de los clientes.
     */
    private ServerSocket serverSocket;
    /**
     * Lista de hilos de clientes que se conectan al servidor.
     */
    LinkedList<HiloCliente> clientes;
    /**
     * Variable que almacena la ventana que gestiona la interfaz gráfica del servidor.
     */
    private final VentanaServer ventana;
    /**
     * Variable que almacena el puerto que el servidor usará para escuchar.
     */
    private final String puerto;
    /**
     * Variable que almacena el identificador del cliente que se conecta.
     */
    static int correlativo;
    /**
     * Constructor del servidor.
     * @param puerto
     * @param ventana
     */
    public ServidorSocket(String puerto, VentanaServer ventana) {
        correlativo=0;
        this.puerto=puerto;
        this.ventana=ventana;
        clientes=new LinkedList<>();
        this.start();
    }
    /**
     * Método que devuelve el puerto que el servidor usará para escuchar.
     */
    public void run() {
        try {
            serverSocket = new ServerSocket(Integer.valueOf(puerto));
            ventana.addServidorIniciado();
            while (true) {
                HiloCliente h;
                Socket socket;
                socket = serverSocket.accept();
                System.out.println("Nueva conexion entrante: "+socket);
                h=new HiloCliente(socket, this);
                h.start();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana, "El servidor no se ha podido iniciar,\n"
                    + "puede que haya ingresado un puerto incorrecto.\n"
                    + "Esta aplicación se cerrará.");
            System.exit(0);
        }
    }
    /**
     * Método que envía un mensaje a todos los clientes conectados al servidor.
     * @return
     */
    LinkedList<String> getUsuariosConectados() {
        LinkedList<String>usuariosConectados=new LinkedList<>();
        clientes.stream().forEach(c -> usuariosConectados.add(c.getIdentificador()));
        return usuariosConectados;
    }
    /**
     * Método que agrega una linea al log de la interfaz gráfica del servidor.
     * @param texto
     */
    void agregarLog(String texto) {
        ventana.agregarLog(texto);
    }

}