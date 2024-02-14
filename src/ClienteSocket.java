

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import javax.swing.JOptionPane;

/**
 * Los objetos de esta clase son hilos que al correr escuchan permanentemente
 * lo que el servidor pueda decir, hay un hilo para cada cliente que se conecta al servidor y dicho
 * hilo tiene como función escuchar solamente a ese cliente.
 */
public class ClienteSocket extends Thread {
    /**
     * Socket utilizado para comunicarse con el servidor.
     */
    private Socket socket;
    /**
     * Stream utilizado para el envío de objetos al servidor.
     */
    private ObjectOutputStream objectOutputStream;
    /**
     * Stream utilizado para el envío de objetos al servidor.
     */
    private ObjectInputStream objectInputStream;
    /**
     * Ventana utilizada para la interfaz gráfica del cliente.
     */
    private final VentanaCliente ventana;
    /**
     * Identificador único del cliente dentro del chat.
     */
    private String identificador;
    /**
     * Variable que determina si el cliente escucha o no al servidor, una vez
     * que se arranca el hilo de comunicación del cliente.
     */
    private boolean escuchando;
    /**
     * Variable que almacena la IP del host en el que se ejecuta el servidor.
     */
    private final String host;
    /**
     * Varable que almacena el puerto por el cual el servidor escucha las conexiones
     * de los diversos clientes.
     */
    private final int puerto;

    private String contrasena;
    /**
     * Constructor de la clase cliente.
     * @param ventana
     * @param host
     * @param puerto
     * @param nombre
     * @param contrasena
     */
    ClienteSocket(VentanaCliente ventana, String host, Integer puerto, String nombre, String contrasena) {
        this.ventana=ventana;
        this.host=host;
        this.puerto=puerto;
        this.identificador=nombre;
        this.contrasena =contrasena;
        escuchando=true;
        this.start();
    }
    /**
     * Método run del hilo de comunicación del lado del cliente.
     */
    public void run(){
        try {
            socket=new Socket(host, puerto);
            objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
            objectInputStream=new ObjectInputStream(socket.getInputStream());
            System.out.println("Conexion exitosa!!!!");
            this.enviarSolicitudConexion(identificador, contrasena);
            this.escuchar();
        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(ventana, "Conexión rehusada, servidor desconocido,\n"
                    + "puede que haya ingresado una ip incorrecta\n"
                    + "o que el servidor no este corriendo.\n"
                    + "Esta aplicación se cerrará.");
            System.exit(0);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(ventana, "Conexión rehusada, error de Entrada/Salida,\n"
                    + "puede que haya ingresado una ip o un puerto\n"
                    + "incorrecto, o que el servidor no este corriendo.\n"
                    + "Esta aplicación se cerrará.");
            System.exit(0);
        }

    }

    /**
     * Método que cierra el socket y los streams de comunicación.
     */
    public void desconectar(){
        try {
            objectOutputStream.close();
            objectInputStream.close();
            socket.close();
            escuchando=false;
        } catch (Exception e) {
            System.err.println("Error al cerrar los elementos de comunicación del cliente.");
        }
    }
    /**
     * Método que envia un determinado mensaje hacia el servidor.
     * @param cliente_receptor
     * @param mensaje
     */
    public void enviarMensaje(String cliente_receptor, String mensaje){
        LinkedList<String> lista=new LinkedList<>();

        lista.add("MENSAJE");

        lista.add(identificador);

        lista.add(cliente_receptor);

        lista.add(mensaje);
        try {
            objectOutputStream.writeObject(lista);
        } catch (IOException ex) {
            System.out.println("Error de lectura y escritura al enviar mensaje al servidor.");
        }
    }

    /*
     * Método que escucha constantemente lo que el servidor dice.
     */
    public void escuchar() {
        try {
            while (escuchando) {
                Object aux = objectInputStream.readObject();
                if (aux != null) {
                    if (aux instanceof LinkedList) {
                        //Si se recibe una LinkedList entonces se procesa
                        ejecutar((LinkedList<String>)aux);
                    } else {
                        System.err.println("Se recibió un Objeto desconocido a través del socket");
                    }
                } else {
                    System.err.println("Se recibió un null a través del socket");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana, "La comunicación con el servidor se ha\n"
                    + "perdido, este chat tendrá que finalizar.\n"
                    + "Esta aplicación se cerrará.");
            System.exit(0);
        }
    }
    /**
     * Método que ejecuta una serie de instruccines dependiendo del mensaje que el cliente reciba del servidor.
     * @param lista
     */
    public void ejecutar(LinkedList<String> lista){
        String tipo=lista.get(0);
        switch (tipo) {
            case "CONEXION_ACEPTADA":
                identificador=lista.get(1);
                ventana.sesionIniciada(identificador);
                for(int i=2;i<lista.size();i++){
                    ventana.addContacto(lista.get(i));
                }
                break;
            case "NUEVO_USUARIO_CONECTADO":
                ventana.addContacto(lista.get(1));
                break;
            case "USUARIO_DESCONECTADO":
                ventana.eliminarContacto(lista.get(1));
                break;
            case "MENSAJE":
                ventana.addMensaje(lista.get(1), lista.get(3));
                break;
            default:
                break;
        }
    }
    /**
     * Al conectarse el cliente debe solicitar al servidor que lo agregue a la
     * lista de clientes, para ello se ejecuta este método.
     * @param identificador
     */
    private void enviarSolicitudConexion(String identificador, String contraseña) {
        LinkedList<String> lista=new LinkedList<>();

        lista.add("SOLICITUD_CONEXION");

        lista.add(identificador);

        lista.add(contraseña);
        try {
            objectOutputStream.writeObject(lista);
        } catch (IOException ex) {
            System.out.println("Error de lectura y escritura al enviar mensaje al servidor.");
        }
    }
    /**
     * Método que envía un mensaje al servidor para que este lo elimine de la lista
     * de clientes conectados.
     *
     */
    void confirmarDesconexion() {
        LinkedList<String> lista=new LinkedList<>();
        lista.add("SOLICITUD_DESCONEXION");
        lista.add(identificador);
        try {
            objectOutputStream.writeObject(lista);
        } catch (IOException ex) {
            System.out.println("Error de lectura y escritura al enviar mensaje al servidor.");
        }
    }

    String getIdentificador() {
        return identificador;
    }
}
