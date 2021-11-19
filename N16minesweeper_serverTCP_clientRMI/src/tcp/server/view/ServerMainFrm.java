package tcp.server.view;
 
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
 
import model.IPAddress;
import tcp.server.control.ServerCtr;
 
public class ServerMainFrm extends JFrame implements ActionListener{
    private JTextField txtServerHost, txtServerPort;
    private JButton btnStartServer, btnStopServer;  
    private JTextArea mainText;
    private JTextField txtServerHostRMI, txtServerPortRMI, txtServiceRMI;
    private ServerCtr myServer;
    //private ServerCtr myServer;
     
    public ServerMainFrm(){
        super("TCP server view");
         
         
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
         
        JLabel lblTitle = new JLabel("Server TCP/IP");
        lblTitle.setFont(new java.awt.Font("Dialog", 1, 20));
        lblTitle.setBounds(new Rectangle(150, 15, 200, 30));
        mainPanel.add(lblTitle, null);
        
        JLabel lblHostRMI = new JLabel("RMI Server host:");
        lblHostRMI.setBounds(new Rectangle(300, 70, 150, 25));
        mainPanel.add(lblHostRMI, null);
         
        txtServerHostRMI = new JTextField(50);
        txtServerHostRMI.setBounds(new Rectangle(390, 70, 150, 25));
        mainPanel.add(txtServerHostRMI,null);
         
        JLabel lblPortRMI = new JLabel("RMI Server port:");
        lblPortRMI.setBounds(new Rectangle(300, 100, 150, 25));
        mainPanel.add(lblPortRMI, null);
         
        txtServerPortRMI = new JTextField(50);
        txtServerPortRMI.setBounds(new Rectangle(390, 100, 150, 25));
        mainPanel.add(txtServerPortRMI,null);
         
        JLabel lblClientHost = new JLabel("RMI Server service key:");
        lblClientHost.setBounds(new Rectangle(300, 130, 150, 25));
        mainPanel.add(lblClientHost, null);
         
        txtServiceRMI = new JTextField(50);
        txtServiceRMI.setBounds(new Rectangle(390, 130, 150, 25));
        mainPanel.add(txtServiceRMI,null); 
         
        JLabel lblHost = new JLabel("Server host:");
        lblHost.setBounds(new Rectangle(10, 70, 150, 25));
        mainPanel.add(lblHost, null);
         
        txtServerHost = new JTextField(50);
        txtServerHost.setBounds(new Rectangle(130, 70, 150, 25));
        txtServerHost.setText("localhost");
        txtServerHost.setEditable(false);
        mainPanel.add(txtServerHost,null);
         
        JLabel lblPort = new JLabel("Server port:");
        lblPort.setBounds(new Rectangle(10, 100, 150, 25));
        mainPanel.add(lblPort, null);
         
        txtServerPort = new JTextField(50);
        txtServerPort.setBounds(new Rectangle(130, 100, 150, 25));
        mainPanel.add(txtServerPort,null);
         
        btnStartServer = new JButton("Start server");
        btnStartServer.setBounds(new Rectangle(10, 210, 150, 25));
        btnStartServer.addActionListener(this);
        mainPanel.add(btnStartServer,null);
         
        btnStopServer = new JButton("Stop server");
        btnStopServer.setBounds(new Rectangle(170, 210, 150, 25));
        btnStopServer.addActionListener(this);
        btnStopServer.setEnabled(false);
        mainPanel.add(btnStopServer,null);
         
                 
        JScrollPane jScrollPane1 = new JScrollPane();
        mainText = new JTextArea("");
        jScrollPane1.setBounds(new Rectangle(10, 260, 610, 240));
        mainPanel.add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(mainText, null);
        //mainPanel.add(mainText,null);
         
        this.setContentPane(mainPanel);
        this.pack();        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(new Dimension(640, 540));
        this.setResizable(false);
    }
 
    public void showServerInfor(IPAddress addr) {
        txtServerHost.setText(addr.getHost());
        txtServerPort.setText(addr.getPort()+"");
    }
     
    @Override
    public void actionPerformed(ActionEvent ae) {
        // TODO Auto-generated method stub
        if(ae.getSource() instanceof JButton) {
            JButton clicked = (JButton)ae.getSource();
            if(clicked.equals(btnStartServer)) {// start button
                if(!txtServerPort.getText().isEmpty() && (txtServerPort.getText().trim().length() > 0)) {//custom port
                    int port = Integer.parseInt(txtServerPort.getText().trim());
                    myServer = //new ServerCtr(this, port);
                                 new ServerCtr(this, port);
                }else {// default port
                    myServer = //new ServerCtr(this); 
                                new ServerCtr(this);
                }
                
//                if(!txtUDPServerHost.getText().isEmpty() && (txtUDPServerHost.getText().trim().length() > 0) &&
//                        !txtServerPort.getText().isEmpty() && (txtServerPort.getText().trim().length() > 0)) {//custom server port
//                    int UDPserverPort = Integer.parseInt(txtUDPServerPort.getText().trim());
//                    IPAddress serverAddr = new IPAddress(txtUDPServerHost.getText().trim(), UDPserverPort);
//                    myServer.setUDPServerAddress(serverAddr);
//                }
//                
//                if(!txtUDPClientPort.getText().isEmpty() && (txtUDPClientPort.getText().trim().length() > 0)) {
//                    int clientPort = Integer.parseInt(txtUDPClientPort.getText().trim());
//                    myServer.setUDPClientPort(clientPort);
//                }
                btnStopServer.setEnabled(true);
                btnStartServer.setEnabled(false);
                myServer.init();
                myServer.openServer();
            }else if(clicked.equals(btnStopServer)) {// stop button
                if(myServer != null) {
                    myServer.stopServer();
                    myServer = null;
                }               
                btnStopServer.setEnabled(false);
                btnStartServer.setEnabled(true);
                txtServerHost.setText("localhost");
            }
        }
    }
     
    public void showMessage(String s){
          mainText.append("\n"+s);
          mainText.setCaretPosition(mainText.getDocument().getLength());
    }
     
    public void setServerHost(String serverHost, String serverPort, String service) {
        txtServerHostRMI.setText(serverHost);
        txtServerPortRMI.setText(serverPort);
        txtServiceRMI.setText(service);
    }   
    
    public static void main(String[] args) {
        ServerMainFrm view = new ServerMainFrm();
        view.setVisible(true);
    }
}