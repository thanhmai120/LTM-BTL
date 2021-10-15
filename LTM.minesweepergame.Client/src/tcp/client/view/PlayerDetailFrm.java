package tcp.client.view;
 
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Time;
import java.text.DecimalFormat;
 
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.Challenge;
 
import model.ObjectWrapper;
import model.Player;
import model.PlayerRank;
import model.User;
import tcp.client.control.ClientCtr;
 
public class PlayerDetailFrm extends JFrame implements ActionListener{
    private PlayerRank player;
    private Challenge challenge;
    private JButton btnAddfriend, btnChallenge,btnUnfriend;
    private ClientCtr mySocket;
     
     
    public PlayerDetailFrm(ClientCtr socket, PlayerRank player){
        super("Player Information");
        mySocket = socket;
        this.player = player;
         
        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width-5, this.getSize().height-20);       
        pnMain.setLayout(new BoxLayout(pnMain,BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));
         
        JLabel lblHome = new JLabel("Player Information");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);  
        lblHome.setFont (lblHome.getFont ().deriveFont (20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0,20)));
        btnAddfriend = new JButton("Add friend");
        btnUnfriend = new JButton("Unfriend");
        btnChallenge = new JButton("Challenge");
         
        JPanel content = new JPanel();
        DecimalFormat df = new DecimalFormat("#.##");
        content.setLayout(new GridLayout(8,2));
        content.add(new JLabel("Player name:  "+player.getUsername())); 
        content.add(new JLabel("Full name:  "+player.getFullname()));   
        content.add(new JLabel("Email:  " +player.getEmail()));     
        content.add(new JLabel("Status:  "+(player.isOnline() ? "online":"offline")));    
        content.add(new JLabel("wins number:  "+player.getWin_number()+""));    
        content.add(new JLabel("Win rate:  "+df.format(player.getWin_rate()*100)+" %"));   
        content.add(new JLabel("Opponent's win rate:  "+df.format(player.getAvg_opponent_win_rate()*100)+" %"));   
        content.add(new JLabel("Tournament's score:  "+player.getTrm_score()+""));   
        if(!this.player.isFriend_stat()) 
            content.add(btnAddfriend);
        else
            content.add(btnUnfriend);     
        content.add(btnChallenge);
        pnMain.add(content);          
        btnAddfriend.addActionListener(this);
        btnUnfriend.addActionListener(this);
        btnChallenge.addActionListener(this);
        this.setContentPane(pnMain);
        this.setSize(600,300);              
        this.setLocation(200,10);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_CHALLENGE_PLAYER, this));
        PlayerDetailFrm frm = this;
        
        this.addWindowListener( new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
               if(challenge != null){
                   // send cancelation
                    mySocket.sendData(new ObjectWrapper(ObjectWrapper.CANCEL_CHALLENGE, challenge.getID()));
               }
               
               for(ObjectWrapper func: mySocket.getActiveFunction())
                if(func.getData().equals(frm)){
                    mySocket.getActiveFunction().remove(func);
                    break;
                }
               frm.dispose();
            }
          });
    }
     
 
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        JButton btnClicked = (JButton)e.getSource();
        if(btnClicked.equals(btnChallenge)){
            User u = mySocket.getUser();
            if(u == null){
                JOptionPane.showMessageDialog(this, "You must login to use this function!");
                return;
            }
            if(challenge != null){
                JOptionPane.showMessageDialog(this, "You are waiting for answer from "+challenge.getToPlayer().getUsername());
                return;
            }
            challenge = new Challenge();
            challenge.setExpired(false);
            challenge.setFromPlayer(new Player(u));
            challenge.setToPlayer(player);
            challenge.setTime(new Time(new java.util.Date().getTime()));
            mySocket.sendData(new ObjectWrapper(ObjectWrapper.CHALLENGE_PLAYER, challenge));
        }
        if(btnClicked.equals(btnAddfriend)){
            JOptionPane.showMessageDialog(this, "this function is under construction");
        }
        
        if(btnClicked.equals(btnUnfriend)){
            JOptionPane.showMessageDialog(this, "this function is under construction");
        }
    }
     
    /**
     * Treatment of search result received from the server
     * @param data
     */
    public void receivedDataProcessing(ObjectWrapper data) {
        if(data.getData() instanceof Integer) {
            Integer ID = (Integer) data.getData();
            challenge.setID(ID);
        }
        if(data.getData() instanceof Challenge) {
            challenge = (Challenge)data.getData();
            if(challenge.isAccepted()){
                JOptionPane.showMessageDialog(this, "Challenge accepted !");
                new GameFrm(mySocket).setVisible(true);
                for(ObjectWrapper func: mySocket.getActiveFunction())
                    if(func.getData().equals(this)){
                        mySocket.getActiveFunction().remove(func);
                        break;
                    }
               this.dispose();
            }else{
                JOptionPane.showMessageDialog(this, "Challenge refused !");
            }
            challenge = null;
        }
        if(data.getData() instanceof String && data.getData().equals("busy")) {
            JOptionPane.showMessageDialog(this, "Player "+challenge.getToPlayer().getUsername()+" is busy now!");
            challenge = null;
        }
        if(data.getData() instanceof String && data.getData().equals("offline")) {
            JOptionPane.showMessageDialog(this, "Player "+challenge.getToPlayer().getUsername()+" is not online!");
            challenge = null;
        }
        if(data.getData() instanceof String && data.getData().equals("accepted")) {
                for(ObjectWrapper func: mySocket.getActiveFunction())
                    if(func.getData().equals(this)){
                        mySocket.getActiveFunction().remove(func);
                        break;
                    }
               this.dispose();
        }
        if(data.getData() instanceof String && data.getData().equals("refused")) {
            JOptionPane.showMessageDialog(this, "Challenge refused !");
            challenge = null;
        }
    }
}