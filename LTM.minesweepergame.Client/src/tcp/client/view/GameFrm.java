package tcp.client.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.util.Timer;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import model.Game;
import model.GamePlayer;
import model.ObjectWrapper;
import model.Square;
import tcp.client.control.ClientCtr;


//Following is the implementation of Minesweeper.



public class GameFrm extends JFrame implements ActionListener{
    private JButton btnGiveup;   
    private int[][] counts;
    private JButton[] buttons;
    private JLabel lblMineNumb;
    private JLabel lblMyscore;
    private JLabel lblOpscore;
    private JLabel lblMytime;
    private JLabel lblNowplay;
    private JLabel lblOptime;
    private ClientCtr mySocket; 
    private Game game;
    private boolean lockGrid;
    private boolean buttonRendered;
    private int time;
    private Timer timer;
    private GamePlayer you;
    private GamePlayer opponent;
    /**
    @param size determines the size of the board
    */

    public GameFrm(ClientCtr socket){
        super("Minesweeper Game");
        mySocket = socket;      
        lockGrid = true;
        buttonRendered = false;
        this.setLayout(new BorderLayout());  
        JPanel ButtonPanel = new JPanel();
        this.add(ButtonPanel,BorderLayout.SOUTH);      
        btnGiveup = new JButton("Give up");
        btnGiveup.addActionListener(this);
        ButtonPanel.add(btnGiveup);
        
        JPanel pnlStatus = new JPanel();
        lblMineNumb = new JLabel("Mines: --");
        lblMyscore = new JLabel("Current score: 0");
        lblOpscore = new JLabel("Opponent score: 0");
        lblMytime = new JLabel("Time");
        lblNowplay = new JLabel("Next: ");
        pnlStatus.setLayout(new FlowLayout(FlowLayout.LEFT,10,20));
        pnlStatus.add(lblMineNumb);
        pnlStatus.add(lblMyscore);
        pnlStatus.add(lblOpscore);
        pnlStatus.add(lblNowplay);
        pnlStatus.add(lblMytime);
        this.add(pnlStatus,BorderLayout.NORTH); 
        
        this.setResizable(true);                   
        this.setLocation(200,10);   
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.SERVER_UPDATE_GAME_STAT, this));
        
    }
    
    public void receivedDataProcessing(ObjectWrapper data) {
        if(data.getData() instanceof Game){
            this.setVisible(true);
            game = (Game)data.getData();
            List<GamePlayer> players = game.getPlayers();
            lockGrid = false;
            // you 
            for(GamePlayer p : players) 
                if(p.getPlayer().getID() == mySocket.getUser().getID())
                    you = p;
                else
                    opponent = p;
            
            //check turn
            for(GamePlayer p : players)
                if(!you.isIs_next())
                    lockGrid = true;
            // render frm
            renderView();
            //inform winner
            if(game.getTime_end()!= null){ //Game over
                //render time
                if(timer != null){
                    timer.cancel();
                    timer.purge();
                }
                String message = "Game over ! You "+ (you.isIs_winner() ? "win":"lose .");
                JOptionPane.showMessageDialog(this, message);
                // remove from active func
                ObjectWrapper existed = null;
                for(ObjectWrapper func: mySocket.getActiveFunction())
                    if(func.getData() == this ){
                        ((GameFrm)func.getData()).dispose();
                        existed = func;
                    }
                if(existed != null)
                    mySocket.getActiveFunction().remove(existed);
            }
        }
    }
    
    private void renderView(){
        if(!buttonRendered){
            this.setSize(45*game.getWidth(), 45*game.getWidth()+50);
            int sqNumber = game.getWidth()*game.getWidth();
            buttons = new JButton[sqNumber];
            Container grid = new Container();
            grid.setLayout(new GridLayout(game.getWidth(),game.getWidth()));
            for(int i=0; i<sqNumber; i++){
                buttons[i] = new JButton();
                buttons[i].addActionListener(this);
                buttons[i].setBackground(Color.GRAY);
                grid.add(buttons[i]);
            }
            this.add(grid,BorderLayout.CENTER);
            lblMineNumb.setText("Mines: "+game.getBomb_number());
            buttonRendered = true;
        }
        // render score
        for(GamePlayer p : game.getPlayers())
            if(p == you){
                lblMyscore.setText("You: "+p.getScore());
                lblMyscore.setForeground(Color.decode(p.getColor()));
            }else{
                lblOpscore.setText(p.getPlayer().getUsername()+": "+p.getScore());
                lblOpscore.setForeground(Color.decode(p.getColor()));
            }
        //render next play, time
        if(you.isIs_next()){
            lblNowplay.setText("Next: you");
            lblNowplay.setForeground(Color.decode(you.getColor()));
            lblMytime.setForeground(Color.decode(you.getColor()));
        }else{
            lblNowplay.setText("Next: "+opponent.getPlayer().getUsername());
            lblNowplay.setForeground(Color.decode(opponent.getColor()));
            lblMytime.setForeground(Color.decode(opponent.getColor()));
        }
        //render time
        if(timer != null){
            timer.cancel();
            timer.purge();
        }
        lblMytime.setText(" (10)");
        time = 10;
        timer = new Timer();
        timer.schedule(new CountTime(), 1000);
        // render square
        List<Square> squares = game.getSquares();
        for(Square sq : squares) {
            JButton btn = buttons[sq.getID()-1];
            if(sq.isIs_clicked()){
                btn.setBackground(Color.decode(sq.getColor()));
                btn.setEnabled(false);
                if(sq.isIs_bomb()){
                    //btn.setIcon(defaultIcon);
                    //btn.setText("B");
                    try{
                        Image icon = ImageIO.read(new FileInputStream(new File("rsc/bomb4.ico")));
                        btn.setIcon(new ImageIcon(icon));
                    }catch(Exception e){
                        btn.setText("B");
                        e.printStackTrace();
                    }
                }else{
                    btn.setText(sq.getValue()+"");
                }
            }
        }
        //do smt
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if(lockGrid) return;
        for(int i=0; i<buttons.length; i++){
            if(ae.getSource() == buttons[i]){
                Square sq = game.getSquares().get(i);
                if(sq.isIs_clicked())
                    return;
                sq.setGame(game);
                mySocket.sendData(new ObjectWrapper(ObjectWrapper.MAKE_A_MOVE, sq));
                if(timer != null){
                    timer.cancel();
                    timer.purge();
                }
                lblMytime.setText(" (10)");
                time = 10;
                timer = new Timer();
                timer.schedule(new CountTime(), 1000);
                lockGrid = true;
                break;
            }
        }
    }
    
    public void updateTime(){
        time --;
        if(time == 0)
            time = 10;
        lblMytime.setText(" ("+time+")");
        // reset timer
        timer.cancel();
        timer.purge();
        timer = new Timer();
        timer.schedule(new CountTime(), 1000);
    }
    public static void main(String[] args) {
        new GameFrm(null);
    }
    
    class CountTime extends TimerTask{

        public CountTime() {
        }
        
        @Override
        public void run() {
            updateTime();
        }
        
    }

}