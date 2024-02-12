

import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Clase que gestiona la interfaz gráfica del servidor.
 */
public class VentanaServer extends javax.swing.JFrame {
    private final String DEFAULT_PORT="60000";
    private final ServidorSocket servidor;
    /**
     * Constructor de la clase.
     */
    public VentanaServer() {
        initComponents();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String puerto=getPuerto();
        servidor=new ServidorSocket(puerto, this);
    }

    /**
     * Método que inicializa los componentes de la ventana.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtClientes = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Servidor");

        jScrollPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Log del Servidor"));

        txtClientes.setEditable(false);
        txtClientes.setColumns(20);
        txtClientes.setRows(5);
        jScrollPane1.setViewportView(txtClientes);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // Se establece el aspecto de la interfaz gráfica.

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }


        // Se crea y se muestra la ventana del servidor.
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaServer().setVisible(true);
            }
        });
    }


    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtClientes;

    /**
     * Método que agrega una línea de texto al log.
     * @param texto
     */
    void agregarLog(String texto) {
        txtClientes.append(texto);
    }
    /**
     * Método que abre una ventana para que el usuario ingrese el puerto que
     * desea utilizar para que el servidor escuche.
     * @return
     */
    private String getPuerto() {
        String p=DEFAULT_PORT;
        JTextField puerto = new JTextField(20);
        puerto.setText(p);
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridLayout(2, 1));
        myPanel.add(new JLabel("Puerto de la conexión(Sala de Chat):"));
        myPanel.add(puerto);
        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Configuraciones de la comunicación", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            p=puerto.getText();
        }else{
            System.exit(0);
        }
        return p;
    }
    /**
     * Método que agrega un mensaje de confirmación al log cuando el servidor está
     * corriendo correctamente.
     */
    void addServidorIniciado() {
        txtClientes.setText("Servidor Inicializandose... [Ok].");
    }
}
