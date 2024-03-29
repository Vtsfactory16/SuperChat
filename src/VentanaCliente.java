
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Clase que gestiona la interfaz gráfica del cliente, que muestra un log de
 * las conexiones y desconexiones de los diferentes clientes, así como un mensaje
 * de confirmación de que el servidor esta corriendo correctamente.
 */
public class VentanaCliente extends javax.swing.JFrame {

    /**
     * Constructor de la ventana.
     */
    public VentanaCliente() {
        initComponents();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String ip_puerto_nombre[]=getIP_Puerto_Nombre();
        String ip=ip_puerto_nombre[0];
        String puerto=ip_puerto_nombre[1];
        String nombre=ip_puerto_nombre[2];
        cliente=new ClienteSocket(this, ip, Integer.valueOf(puerto), nombre, "1234");
        txtHistorial.setBackground(new Color(144, 238, 144));
        Color verdeFuerte = new Color(0, 128, 0);
        this.getContentPane().setBackground(verdeFuerte);



    }

    /**
     * Método que inicializa los componentes de la ventana.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtHistorial = new javax.swing.JTextArea();
        txtMensaje = new javax.swing.JTextField();
        cmbContactos = new javax.swing.JComboBox();
        btnEnviar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        txtHistorial.setEditable(false);
        txtHistorial.setColumns(20);
        txtHistorial.setRows(5);
        jScrollPane1.setViewportView(txtHistorial);

        btnEnviar.setText("Enviar");
        btnEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarActionPerformed(evt);
            }
        });

        jLabel1.setText("Destinatario:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtMensaje)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnEnviar))
                                        .addComponent(jScrollPane1)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(cmbContactos, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cmbContactos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnEnviar))
                                .addContainerGap())
        );

        pack();
    }
    /**
     * Al hacer clic en el botón de enviar, se debe pedir al cliente del chat que
     * envíe al servidor el mensaje.
     * @param evt
     */
    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {
        //Si no hay más clientes del chat con quien comunicarse.
        if(cmbContactos.getSelectedItem()==null){
            JOptionPane.showMessageDialog(this, "Debe escoger un destinatario válido, si no \n"
                    + "hay uno, espere a que otro usuario se conecte\n"
                    + "para poder chatear con él.");
            return;
        }
        String cliente_receptor=cmbContactos.getSelectedItem().toString();
        String mensaje=txtMensaje.getText();
        cliente.enviarMensaje(cliente_receptor, mensaje);
        //se agrega en el historial de la conversación lo que el cliente ha dicho
        String nombreCliente = "Yo"; // Prefijo para indicar que el mensaje fue enviado por el cliente
        txtHistorial.append("## " + nombreCliente +": " + "\n"+ mensaje + "\n");
        txtMensaje.setText("");
    }

    private void formWindowClosed(java.awt.event.WindowEvent evt) {
    }
    /**
     * Cuando la ventana se este cerrando se notifica al servidor que el cliente
     * se ha desconectado, por lo que los demás clientes del chat no podrán enviarle
     * más mensajes.
     * @param evt
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        cliente.confirmarDesconexion();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaCliente().setVisible(true);
            }
        });
    }


    private javax.swing.JButton btnEnviar;
    private javax.swing.JComboBox cmbContactos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtHistorial;
    private javax.swing.JTextField txtMensaje;

    /**
     * Constante que almacena el puerto por defecto para la aplicación.
     */
    private final String DEFAULT_PORT="60000";
    /**
     * Constante que almacena la IP por defecto (localhost) para el servidor.
     */
    private final String DEFAULT_IP="127.0.0.1";
    /**
     * Constante que almacena el cliente, con el cual se gestiona la comunicación
     * con el servidor.
     */
    private final ClienteSocket cliente;
    /**
     * Agrega un contacto al JComboBox de contactos.
     * @param contacto
     */
    void addContacto(String contacto) {
        cmbContactos.addItem(contacto);
    }
    /**
     * Agrega un nuevo mensaje al historial de la conversación.
     * @param emisor
     * @param mensaje
     */
    void addMensaje(String emisor, String mensaje) {
        txtHistorial.append("##### "+emisor + " ##### : \n" + mensaje+"\n");
    }
    /**
     * Se configura el título de la ventana para una nueva sesión.
     * @param identificador
     */
    void sesionIniciada(String identificador) {
        this.setTitle(" --- "+identificador+" --- ");
    }
    /**
     * Método que abre una ventana para que el usuario ingrese la IP del host en
     * el que corre el servidor, el puerto con el que escucha y el nombre con el
     * que quiere participar en el chat.
     * @return
     */
    private String[] getIP_Puerto_Nombre() {
        String s[]=new String[3];
        s[0]=DEFAULT_IP;
        s[1]=DEFAULT_PORT;
        JTextField ip = new JTextField(20);
        JTextField puerto = new JTextField(20);
        JTextField usuario = new JTextField(20);
        ip.setText(DEFAULT_IP);
        puerto.setText(DEFAULT_PORT);
        usuario.setText("Usuario Cliente");
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridLayout(3, 2));
        myPanel.add(new JLabel("IP del Servidor:"));
        myPanel.add(ip);
        myPanel.add(new JLabel("Puerto de la conexión(Sala de chat):"));
        myPanel.add(puerto);
        myPanel.add(new JLabel("Escriba su nombre:"));
        myPanel.add(usuario);
        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Configuraciones de la comunicación", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            s[0]=ip.getText();
            s[1]=puerto.getText();
            s[2]=usuario.getText();
        }else{
            System.exit(0);
        }
        return s;
    }
    /**
     * Método que elimina cierto cliente de la lista de contactos, este se llama
     * cuando cierto usuario cierra sesión.
     * @param identificador
     */
    void eliminarContacto(String identificador) {
        for (int i = 0; i < cmbContactos.getItemCount(); i++) {
            if(cmbContactos.getItemAt(i).toString().equals(identificador)){
                cmbContactos.removeItemAt(i);
                return;
            }
        }
    }
}
