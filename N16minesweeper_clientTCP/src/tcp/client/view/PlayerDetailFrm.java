package tcp.client.view;
 
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
 
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.Challenge;
import model.FriendRequest;
 
import model.ObjectWrapper;
import model.Player;
import model.PlayerRank;
import model.PlayerStat;
import model.User;
import tcp.client.control.ClientCtr;
 
public class PlayerDetailFrm extends JFrame implements ActionListener{
    private PlayerRank player;
    private Challenge challenge;
    private JButton btnAddfriend, btnChallenge,btnUnfriend,btnAccept,btnCancelrequest;
    private ClientCtr mySocket;
     
     
    public PlayerDetailFrm(ClientCtr socket, PlayerRank player){
        super("Player Information");
        mySocket = socket;
        this.player = player;
         
        
        btnAddfriend = new JButton("Add friend");
        btnUnfriend = new JButton("Unfriend");
        btnChallenge = new JButton("Challenge");
        btnAccept = new JButton("Accept friend request");
        btnCancelrequest = new JButton("Cancel friend request");
         
        renderComponent();    
        btnAddfriend.addActionListener(this);
        btnUnfriend.addActionListener(this);
        btnChallenge.addActionListener(this);
        btnAccept.addActionListener(this);
        btnCancelrequest.addActionListener(this);
        this.setSize(600,300);              
        this.setLocation(200,10);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_CHALLENGE_PLAYER, this));
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_FRIEND_REQUEST, this));
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_UNFRIEND, this));
        PlayerDetailFrm frm = this;
        
        this.addWindowListener( new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
               if(challenge != null){
                   // send cancelation
                    mySocket.sendData(new ObjectWrapper(ObjectWrapper.CANCEL_CHALLENGE, challenge.getID()));
               }
               ArrayList<ObjectWrapper> existed = new ArrayList<>();
               for(ObjectWrapper func: mySocket.getActiveFunction())
                if(func.getData().equals(frm)){
                    existed.add(func);
                }
               for(ObjectWrapper func : existed) {
                   mySocket.getActiveFunction().remove(func);
               }
               frm.dispose();
            }
          });
    }
     
    private void renderComponent() {
        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width-5, this.getSize().height-20);       
        pnMain.setLayout(new BoxLayout(pnMain,BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));
         
        JLabel lblHome = new JLabel("Player Information");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);  
        lblHome.setFont (lblHome.getFont ().deriveFont (20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0,20)));
        
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
        if(this.player.getFriend_stat() == PlayerStat.BE_FRIEND) 
            content.add(btnUnfriend);
        else if(this.player.getFriend_stat() == PlayerStat.REQUESTED)
            content.add(btnCancelrequest);
        else if(this.player.getFriend_stat() == PlayerStat.REQUESTING)
            content.add(btnAccept);
        else
            content.add(btnAddfriend);
        content.add(btnChallenge);
        pnMain.add(content);      
        this.setContentPane(pnMain);
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
            challenge.setTime(new Timestamp(System.currentTimeMillis()));
            mySocket.sendData(new ObjectWrapper(ObjectWrapper.CHALLENGE_PLAYER, challenge));
        }
        if(btnClicked.equals(btnAddfriend)){
            FriendRequest fr = new FriendRequest();
            fr.setAccepted(null);
            fr.setFromPlayer(new Player(mySocket.getUser()));
            fr.setTime_request(new Timestamp(System.currentTimeMillis()));
            fr.setToPlayer(player);
            mySocket.sendData(new ObjectWrapper(ObjectWrapper.SEND_FRIEND_REQUEST, fr));
        }
        
        if(btnClicked.equals(btnUnfriend)){
            FriendRequest fr = new FriendRequest();
            fr.setFromPlayer(new Player(mySocket.getUser()));
            fr.setToPlayer(player);
            mySocket.sendData(new ObjectWrapper(ObjectWrapper.UNFRIEND, fr));
        }
        
        if(btnClicked.equals(btnAccept)) {
            JOptionPane.showMessageDialog(this, "this function is under construction");
        }
        
        if(btnClicked.equals(btnCancelrequest)) {
            JOptionPane.showMessageDialog(this, "this function is under construction");
        }
    }
     
    /**
     * Treatment of search result received from the server
     * @param data
     */
    public void receivedDataProcessing(ObjectWrapper data) {
        if(data.getPerformative() == ObjectWrapper.REPLY_CHALLENGE_PLAYER) {
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
                    ArrayList<ObjectWrapper> existed = new ArrayList<>();
                    for(ObjectWrapper func: mySocket.getActiveFunction())
                     if(func.getData().equals(this)){
                         existed.add(func);
                     }
                    for(ObjectWrapper func : existed) {
                        mySocket.getActiveFunction().remove(func);
                    }
                   this.dispose();
            }
            if(data.getData() instanceof String && data.getData().equals("refused")) {
                JOptionPane.showMessageDialog(this, "Challenge refused !");
                challenge = null;
            }
        }
        
        else if(data.getPerformative() == ObjectWrapper.REPLY_FRIEND_REQUEST) {
            if(data.getData() instanceof String && data.getData().equals("ok")) {
                JOptionPane.showMessageDialog(this, "Friend request to "+player.getFullname()+" sent");
                player.setFriend_stat(PlayerStat.REQUESTED);
                renderComponent();
            }
            if(data.getData() instanceof String && data.getData().equals("fail")) {
                JOptionPane.showMessageDialog(this, "Friend request to "+player.getFullname()+" fail");
            }
        }
        else if(data.getPerformative() == ObjectWrapper.REPLY_UNFRIEND) {
            if(data.getData() instanceof String && data.getData().equals("ok")) {
                JOptionPane.showMessageDialog(this, "Unfriend to "+player.getFullname()+" sent");
                player.setFriend_stat(0);
                renderComponent();
                for(ObjectWrapper func : mySocket.getActiveFunction()) 
                    if(func.getData() instanceof PlayerHomeFrm)
                        ((PlayerHomeFrm)func.getData()).refreshListFriend();
            }
            if(data.getData() instanceof String && data.getData().equals("fail")) {
                JOptionPane.showMessageDialog(this, "Unfriend to "+player.getFullname()+" fail");
            }
        }
    }
}