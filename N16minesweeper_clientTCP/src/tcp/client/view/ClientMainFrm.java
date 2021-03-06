package tcp.client.view;
 
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
 
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
 
import model.IPAddress;
import model.ObjectWrapper;
import model.User;
import tcp.client.control.ClientCtr;
 
public class ClientMainFrm extends JFrame implements ActionListener{
    private JMenuBar mnbMain;
    private JMenu mnUser;
    private JMenu mnPlayer;
    private JMenuItem mniLogin, mniRegister;
    private JMenuItem mniRank, mniTournament, mniFriend, mniChallenge,mniHome,mniGame;
     
    private JTextField txtServerHost;
    private JTextField txtServerPort;
    private JButton btnConnect;
    private JButton btnDisconnect;  
    private JTextArea mainText;
    private ClientCtr myControl;
     
    public ClientMainFrm(){
        super("TCP client view");
         
         
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
         
        mnbMain = new JMenuBar();
        mnUser = new JMenu("User");
        mniLogin = new JMenuItem("Login");
        mniRegister = new JMenuItem("Register");
        mniLogin.addActionListener(this);
        mniRegister.addActionListener(this);
        mnUser.add(mniLogin);
        mnUser.add(mniRegister);
        mnbMain.add(mnUser);
        
        mnPlayer = new JMenu("Player");
        mniRank = new JMenuItem("Ranks");
        mniTournament = new JMenuItem("Tournaments");
        mniFriend = new JMenuItem("Friends");
        mniChallenge = new JMenuItem("Challenge");
        mniHome = new JMenuItem("Home");
        mniGame = new JMenuItem("Game");
        mniRank.addActionListener(this);
        mniTournament.addActionListener(this);
        mniFriend.addActionListener(this);
        mniChallenge.addActionListener(this);
        mniHome.addActionListener(this);
        mniGame.addActionListener(this);
//        mnPlayer.add(mniRank);
//        mnPlayer.add(mniFriend);
//        mnPlayer.add(mniTournament);
//        mnPlayer.add(mniChallenge);
        mnPlayer.add(mniHome);
        mnPlayer.add(mniGame);
        mnbMain.add(mnPlayer);
        this.setJMenuBar(mnbMain);
        mniLogin.setEnabled(false);
        mniRegister.setEnabled(false);
        mniRank.setEnabled(false);
        mniFriend.setEnabled(false);
        mniTournament.setEnabled(false);
        mniHome.setEnabled(false);
        mniGame.setEnabled(false);
        
        JLabel lblTitle = new JLabel("Client TCP/IP");
        lblTitle.setFont(new java.awt.Font("Dialog", 1, 20));
        lblTitle.setBounds(new Rectangle(150, 20, 200, 30));
        mainPanel.add(lblTitle, null);
         
        JLabel lblHost = new JLabel("Server host:");
        lblHost.setBounds(new Rectangle(10, 70, 150, 25));
        mainPanel.add(lblHost, null);
         
        txtServerHost = new JTextField(50);
        txtServerHost.setBounds(new Rectangle(170, 70, 150, 25));
        mainPanel.add(txtServerHost,null);
         
        JLabel lblPort = new JLabel("Server port:");
        lblPort.setBounds(new Rectangle(10, 100, 150, 25));
        mainPanel.add(lblPort, null);
         
        txtServerPort = new JTextField(50);
        txtServerPort.setBounds(new Rectangle(170, 100, 150, 25));
        mainPanel.add(txtServerPort,null);
         
        btnConnect = new JButton("Connect");
        btnConnect.setBounds(new Rectangle(10, 150, 150, 25));
        btnConnect.addActionListener(this);
        mainPanel.add(btnConnect,null);
         
        btnDisconnect = new JButton("Disconnect");
        btnDisconnect.setBounds(new Rectangle(170, 150, 150, 25));
        btnDisconnect.addActionListener(this);
        btnDisconnect.setEnabled(false);
        mainPanel.add(btnDisconnect,null);
         
                 
        JScrollPane jScrollPane1 = new JScrollPane();
        mainText = new JTextArea("");
        jScrollPane1.setBounds(new Rectangle(10, 200, 610, 240));
        mainPanel.add(jScrollPane1, BorderLayout.CENTER);
        jScrollPane1.getViewport().add(mainText, null);
        //mainPanel.add(mainText,null);
         
        this.setContentPane(mainPanel);
        this.pack();    
        this.setSize(new Dimension(640, 480));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);      
        this.addWindowListener( new WindowAdapter(){
           public void windowClosing(WindowEvent e) {
              if(myControl != null) {
                  myControl.closeConnection();
              }
             System.exit(0);
             }
          });
    }

    public ClientCtr getMyControl() {
        return myControl;
    }
 
     
    @Override
    public void actionPerformed(ActionEvent ae) {
        // TODO Auto-generated method stub
        if(ae.getSource() instanceof JButton) {
            JButton btn = (JButton)ae.getSource();
            if(btn.equals(btnConnect)) {// connect button
                if(!txtServerHost.getText().isEmpty() && (txtServerHost.getText().trim().length() > 0) &&
                        !txtServerPort.getText().isEmpty() && (txtServerPort.getText().trim().length() > 0)) {//custom port
                    int port = Integer.parseInt(txtServerPort.getText().trim());
                    myControl = new ClientCtr(this, new IPAddress(txtServerHost.getText().trim(), port));
                                // new ClientCtr(this, port);
                }else {
                    myControl = new ClientCtr(this);
                }
                if(myControl.openConnection()) {
                    btnDisconnect.setEnabled(true);
                    btnConnect.setEnabled(false);
                    mniLogin.setEnabled(true);
                    mniRegister.setEnabled(true);
                    mniRank.setEnabled(true);
                    mniFriend.setEnabled(false);
                    mniChallenge.setEnabled(false);
                    mniTournament.setEnabled(false);
                }else {
                    resetClient();
                }
            }else if(btn.equals(btnDisconnect)) {// disconnect button
                resetClient();
            }
        }else if(ae.getSource() instanceof JMenuItem) {
            JMenuItem mni = (JMenuItem)ae.getSource();
            if(mni.equals(mniLogin)) {// login function
                for(ObjectWrapper func: myControl.getActiveFunction())
                    if(func.getData() instanceof LoginFrm) {
                        ((LoginFrm)func.getData()).setVisible(true);
                        return;
                    }
                LoginFrm clv = new LoginFrm(myControl);
                clv.setVisible(true);
            }
            if(mni.equals(mniRegister)) {
                for(ObjectWrapper func: myControl.getActiveFunction())
                    if(func.getData() instanceof RegisterFrm) {
                        ((RegisterFrm)func.getData()).setVisible(true);
                        return;
                    }
                RegisterFrm clv = new RegisterFrm(myControl);
                clv.setVisible(true);
            }
            if(mni.equals(mniRank)) {
                for(ObjectWrapper func: myControl.getActiveFunction())
                    if(func.getData() instanceof PlayerRankFrm) {
                        ((PlayerRankFrm)func.getData()).setVisible(true);
                        return;
                    }
                PlayerRankFrm prv = new PlayerRankFrm(myControl);
                prv.setVisible(true);
            }
            if(mni.equals(mniFriend)) {
                for(ObjectWrapper func: myControl.getActiveFunction())
                    if(func.getData() instanceof FriendRankFrm) {
                        ((FriendRankFrm)func.getData()).setVisible(true);
                        return;
                    }
                FriendRankFrm frv = new FriendRankFrm(myControl);
                frv.setVisible(true);
            }
            if(mni.equals(mniTournament)) {
                for(ObjectWrapper func: myControl.getActiveFunction())
                    if(func.getData() instanceof PlayerTournamentFrm) {
                        ((PlayerTournamentFrm)func.getData()).setVisible(true);
                        return;
                    }
                PlayerTournamentFrm ptv = new PlayerTournamentFrm(myControl);
                ptv.setVisible(true);
            }
            
            if(mni.equals(mniChallenge)) {
                for(ObjectWrapper func: myControl.getActiveFunction())
                    if(func.getData() instanceof ChallengeFrm) {
                        ((ChallengeFrm)func.getData()).setVisible(true);
                        ((ChallengeFrm)func.getData()).toFront();
                        ((ChallengeFrm)func.getData()).requestFocus();
                        return;
                    }
            }
            
            if(mni.equals(mniHome)) {
                for(ObjectWrapper func: myControl.getActiveFunction())
                    if(func.getData() instanceof PlayerHomeFrm) {
                        ((PlayerHomeFrm)func.getData()).setVisible(true);
                        return;
                    }
                
            }
            if(mni.equals(mniGame)) {
                for(ObjectWrapper func: myControl.getActiveFunction())
                    if(func.getData() instanceof GameFrm) {
                        ((GameFrm)func.getData()).setVisible(true);
                        return;
                    }
                
            }
        }
    }
     
    public void showMessage(String s){
          mainText.append("\n"+s);
          mainText.setCaretPosition(mainText.getDocument().getLength());
    }
     
    public void resetClient() {
        if(myControl != null) {
            myControl.closeConnection();
            myControl.getActiveFunction().clear();
            myControl = null;
        }               
        btnDisconnect.setEnabled(false);
        btnConnect.setEnabled(true);
        mniLogin.setEnabled(false);
        mniRegister.setEnabled(false);
        mniRank.setEnabled(false);
        mniFriend.setEnabled(false);
        mniChallenge.setEnabled(false);
        mniTournament.setEnabled(false);
        mniHome.setEnabled(false);
        mniGame.setEnabled(false);
    }
    
    public void onLoginSuccessfully(){
        mniLogin.setEnabled(false);
        mniRegister.setEnabled(false);
        mniRank.setEnabled(true);
        mniFriend.setEnabled(true);
        mniChallenge.setEnabled(true);
        mniTournament.setEnabled(true);
        mniHome.setEnabled(true);
        mniGame.setEnabled(true);
    }
     
    public static void main(String[] args) {
        ClientMainFrm view = new ClientMainFrm();
        view.setVisible(true);
    }
}