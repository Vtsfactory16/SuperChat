

import java.io.*;
import java.net.Socket;
import java.security.Key;
import java.util.LinkedList;

/**
 * Clase encargada de manejar la comunicación con un cliente en particular.
 * Cada cliente que se conecta al servidor tiene un hilo que se encarga de
 * escuchar lo que el cliente dice y de enviarle mensajes.
 */
public class HiloCliente extends Thread{
    /**
     * Socket que se utiliza para comunicarse con el cliente.
     */
    private final Socket socket;
    /**
     * Stream con el que se envían objetos al servidor.
     */
    private ObjectOutputStream objectOutputStream;
    /**
     * Stream con el que se reciben objetos del servidor.
     */
    private ObjectInputStream objectInputStream;
    /**
     * Servidor al que pertenece este hilo.
     */
    private final ServidorSocket server;
    /**
     * Identificador único del cliente con el que este hilo se comunica.
     */
    private String identificador;
    /**
     * Variable booleana que almacena verdadero cuando este hilo est escuchando
     * lo que el cliente que atiende esta diciendo.
     */
    private boolean escuchando;

    private final String directorioChats = "chats/";
    /**
     * Método constructor de la clase hilo cliente.
     * @param socket
     * @param server
     */
    public HiloCliente(Socket socket, ServidorSocket server) {
        this.server=server;
        this.socket = socket;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.err.println("Error en la inicialización del ObjectOutputStream y el ObjectInputStream");
        }
    }
    /**
     * Método que cierra el socket de comunicación con el cliente.
     */
    public void desconnectar() {
        try {
            socket.close();
            escuchando=false;
        } catch (IOException ex) {
            System.err.println("Error al cerrar el socket de comunicación con el cliente.");
        }
    }
    /**
     * Método que se ejecuta cuando se inicia el hilo.
     */
    public void run() {
        try{
            escuchar();
        } catch (Exception ex) {
            System.err.println("Error al llamar al método readLine del hilo del cliente.");
        }
        desconnectar();
    }

    /**
     * Método que se encarga de escuchar lo que el cliente dice y de enviarle mensajes.
     */
    public void escuchar(){
        escuchando=true;
        while(escuchando){
            try {
                Object aux=objectInputStream.readObject();
                if(aux instanceof LinkedList){
                    ejecutar((LinkedList<String>)aux);
                }
            } catch (Exception e) {
                System.err.println("Error al leer lo enviado por el cliente.");
            }
        }
    }
    /**
     * Método que se encarga de ejecutar las acciones que el cliente solicita.
     * @param lista
     */
    public void ejecutar(LinkedList<String> lista){
        String tipo = lista.get(0);
        switch (tipo) {
            case "SOLICITUD_CONEXION":
                confirmarConexion(lista.get(1));
                break;
            case "SOLICITUD_DESCONEXION":
                confirmarDesConexion();
                break;
            case "MENSAJE":
                String remitente = lista.get(1);
                String destinatario = lista.get(2);
                String mensaje = lista.get(3);
                // Envío del mensaje a todos los clientes destinatarios, excluyendo al remitente
                server.clientes.stream().filter(h -> !remitente.equals(h.getIdentificador()) && destinatario.equals(h.getIdentificador()))
                        .forEach(h -> h.enviarMensaje(lista));
                // Guardar el mensaje en el archivo correspondiente al remitente
                guardarMensaje(remitente, destinatario, mensaje);
                break;
            default:
                break;
        }
    }

    /**
     * Método para enviar un mensaje al cliente através del socket.
     * @param lista
     */
    private void enviarMensaje(LinkedList<String> lista){
        try {
            objectOutputStream.writeObject(lista);
        } catch (Exception e) {
            System.err.println("Error al enviar el objeto al cliente.");
        }
    }
    /**
     * Método que se encarga de confirmar la conexión del cliente.
     * @param identificador
     */
    private void confirmarConexion(String identificador) {
        ServidorSocket.correlativo++;
        this.identificador= ServidorSocket.correlativo+" - "+identificador;
        LinkedList<String> lista=new LinkedList<>();
        lista.add("CONEXION_ACEPTADA");
        lista.add(this.identificador);
        lista.addAll(server.getUsuariosConectados());
        enviarMensaje(lista);
        server.agregarLog("\nNuevo cliente: "+this.identificador);
        //enviar a todos los clientes el nombre del nuevo usuario conectado excepto a él mismo
        LinkedList<String> auxLista=new LinkedList<>();
        auxLista.add("NUEVO_USUARIO_CONECTADO");
        auxLista.add(this.identificador);
        server.clientes
                .stream()
                .forEach(cliente -> cliente.enviarMensaje(auxLista));
        server.clientes.add(this);
    }
    /**
     * Método que devuelve el identificador del cliente.
     * @return
     */
    public String getIdentificador() {
        return identificador;
    }
    /**
     * Método que se encarga de confirmar la desconexión del cliente.
     */
    private void confirmarDesConexion() {
        LinkedList<String> auxLista=new LinkedList<>();
        auxLista.add("USUARIO_DESCONECTADO");
        auxLista.add(this.identificador);
        server.agregarLog("\nEl cliente \""+this.identificador+"\" se ha desconectado.");
        this.desconnectar();
        for(int i=0;i<server.clientes.size();i++){
            if(server.clientes.get(i).equals(this)){
                server.clientes.remove(i);
                break;
            }
        }
        server.clientes
                .stream()
                .forEach(h -> h.enviarMensaje(auxLista));
    }
    // Método para guardar el mensaje en el archivo correspondiente

   /* private void guardarMensaje(String remitente, String destinatario, String mensaje) {
        String rutaArchivo = directorioChats + remitente + "_" + destinatario + ".txt";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo, true));
            writer.write(mensaje);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
   // Método para guardar el mensaje en el archivo correspondiente
   private void guardarMensaje(String remitente, String destinatario, String mensaje) {
       String rutaArchivo = directorioChats + remitente + "_" + destinatario + ".txt";
       try {
           // Encriptar el mensaje
           Key clave = AESSimpleManager.obtenerClave("tu_clave_secreta", 16); // Cambia "tu_clave_secreta" por una clave segura
           String mensajeEncriptado = AESSimpleManager.cifrar(mensaje, clave);

           // Guardar el mensaje en el archivo
           BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo, true));
           writer.write(mensajeEncriptado);
           writer.newLine();
           writer.close();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
}