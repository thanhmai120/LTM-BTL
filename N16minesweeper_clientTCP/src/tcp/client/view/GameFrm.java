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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import model.Game;
import model.GamePlayer;
import model.ObjectWrapper;
import model.PlayerStat;
import model.Round;
import model.RoundResult;
import model.Square;
import tcp.client.control.ClientCtr;


//Following is the implementation of Minesweeper.



public class GameFrm extends JFrame implements ActionListener{
    private JButton btnGiveup;   
    private JButton[] buttons;
    private JPanel pnPreRounds;
    private JLabel lblRound;
    private JLabel lblMineNumb;
    private JLabel lblMyscore;
    private JLabel lblOpscore;
    private JLabel lblMytime;
    private JLabel lblNowplay;
    private JLabel lbRoundTime;
    private ClientCtr mySocket; 
    private Game game;
    private boolean lockGrid;
    private boolean buttonRendered;
    private boolean gameOver;
    private int time;
    private Timer timer;
    private Timer roundTimer;
    private GamePlayer you;
    private GamePlayer opponent;
    private RoundResult yourResult;
    private RoundResult opponentResult;
    private PlayerStat opponentStat;
    private int nextRound;
    int roundTime;
    private Container grid;
    /**
    @param size determines the size of the board
    */

    public GameFrm(ClientCtr socket){
        super("Minesweeper Game");
        mySocket = socket;      
        lockGrid = true;
        buttonRendered = false;
        gameOver = false;
        this.setLayout(new BorderLayout());  
        JPanel ButtonPanel = new JPanel();
        this.add(ButtonPanel,BorderLayout.SOUTH);      
        btnGiveup = new JButton("Give up");
        btnGiveup.addActionListener(this);
        ButtonPanel.add(btnGiveup);
        
        JPanel pnlStatus = new JPanel();
        lblRound = new JLabel("Round: --");
        lblMineNumb = new JLabel("Mines: --");
        lblMyscore = new JLabel("Current score: 0");
        lblOpscore = new JLabel("Opponent score: 0");
        lblMytime = new JLabel("Time");
        lblNowplay = new JLabel("Next: ");
        JPanel pnRounds = new JPanel();
        pnRounds.setLayout(new BoxLayout(pnRounds, BoxLayout.Y_AXIS));
        pnPreRounds = new JPanel();
        pnPreRounds.setLayout(new BoxLayout(pnPreRounds, BoxLayout.Y_AXIS));
        pnPreRounds.setForeground(Color.gray);
        pnlStatus.setLayout(new FlowLayout(FlowLayout.LEFT,20,5));
        pnRounds.add(pnPreRounds);
        pnRounds.add(pnlStatus);
        pnlStatus.add(lblRound);
        pnlStatus.add(lblMineNumb);
        pnlStatus.add(lblMyscore);
        pnlStatus.add(lblOpscore);
        pnlStatus.add(lblNowplay);
        pnlStatus.add(lblMytime);
        this.add(pnRounds,BorderLayout.NORTH); 
        
        this.setResizable(true);                   
        this.setLocation(200,10);   
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.SERVER_UPDATE_GAME_STAT, this));
        
    }
    
    public void receivedDataProcessing(ObjectWrapper data) {
        if(data.getData() instanceof Game){
            game = (Game)data.getData();
            Round round = game.getRounds().get(nextRound-1);
            List<RoundResult> results = round.getResults();
            List<GamePlayer> players = game.getPlayers();
            lockGrid = false;
            if(game.getTime_end() != null) 
                gameOver = true;
            // you 
            for(GamePlayer p : players) 
                if(p.getPlayer().getID() == mySocket.getUser().getID())
                    you = p;
                else
                    opponent = p;
            for(RoundResult r : results) 
                if(r.getGamePlayer().getPlayer().getID() == mySocket.getUser().getID())
                    yourResult = r;
                else
                    opponentResult = r;
            //check turn
            for(GamePlayer p : players)
                if(!yourResult.isNext())
                    lockGrid = true;
            // render frm
            renderView();
            //check round end ? inform winner, next round?
            if(round.getTime_end()!= null){ //round over
                //game over ? cancel timer
                if(gameOver) {
                    if(timer != null) {
                        timer.cancel();
                        timer.purge();
                    }
                    String message = "Round "+nextRound+" over ! You "+ (yourResult.isWinner() ? "win":"lose. ");
                    JLabel l = new JLabel(message);
                    JPanel pn = new JPanel();
                    pn.setLayout(new FlowLayout(FlowLayout.LEFT,20,5));
                    pn.add(l);
                    pnPreRounds.add(pn);
                    this.setSize(this.getWidth(), this.getHeight()+11);
                    
                } else {
                // refresh new round 
                
                    String message = "Round "+nextRound+" over ! You "+ (yourResult.isWinner() ? "win":"lose");
                    JLabel l = new JLabel(message);
                    lbRoundTime = new JLabel(". Next round in 5 seconds ...");
                    JPanel pn = new JPanel();
                    pn.setLayout(new FlowLayout(FlowLayout.LEFT,20,5));
                    pn.add(l);
                    pn.add(lbRoundTime);
                    pnPreRounds.add(pn);
                    this.setSize(this.getWidth(), this.getHeight()+11);
                    roundTime = 5;
                    //timer count time
                    roundTimer = new Timer();
                    roundTimer.schedule(new CountRoundTime(), 1000);
                    //wait 5 seconds 
                    lockGrid = true;
                    try {
                    //wait 5 second
                    Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    lockGrid = false;
                    nextRound ++;
//                    for(int i=0; i<buttons.length; i++)
//                        grid.remove(buttons[i]);
                    this.remove(grid);
                    round = game.getRounds().get(nextRound-1);
                    for(RoundResult r : round.getResults()) 
                        if(r.getGamePlayer().getPlayer().getID() == mySocket.getUser().getID())
                            yourResult = r;
                        else
                            opponentResult = r;
                    //check turn
                    if(!yourResult.isNext())
                        lockGrid = true;
                    buttonRendered = false;
                    renderView();
                }
                
            }
            if(gameOver) {
                //show game over message
                JOptionPane.showMessageDialog(this, "Game over ! "
                        + (you.getPlus_score() > opponent.getPlus_score()? "You win !": you.getPlus_score() == opponent.getPlus_score()
                                ? "Draw !" : "You lose !"));
                // remove from active func
                ObjectWrapper existed = null;
                for(ObjectWrapper func: mySocket.getActiveFunction())
                    if(func.getData() == this ){
                        existed = func;
                    }
                if(existed != null)
                    mySocket.getActiveFunction().remove(existed);
                this.dispose();
                
            }
        }
    }
    
    private void renderView(){
        if(!buttonRendered){
            this.setSize(45*game.getWidth(), 45*game.getWidth()+50);
            int sqNumber = game.getWidth()*game.getWidth();
            //render board
            buttons = new JButton[sqNumber];
            grid = new Container();
            grid.setLayout(new GridLayout(game.getWidth(),game.getWidth()));
            for(int i=0; i<sqNumber; i++){
                buttons[i] = new JButton();
                buttons[i].addActionListener(this);
                buttons[i].setBackground(Color.GRAY);
                grid.add(buttons[i]);
            }
            this.add(grid,BorderLayout.CENTER);
            // render game status
            lblRound.setText("Round "+nextRound);
            lblMineNumb.setText("Mines: "+game.getBomb_number());
            lblMyscore.setForeground(Color.decode(you.getColor()));
            lblOpscore.setForeground(Color.decode(opponent.getColor()));
            // create opponent stat
            opponentStat = new PlayerStat(opponent.getPlayer());
            opponentStat.setOnline(true);
            buttonRendered = true;
        }
        
        // render score
        lblMyscore.setText("You: "+yourResult.getScore());
        lblOpscore.setText(opponent.getPlayer().getUsername()+(!opponentStat.isOnline() ? " (out!)" : "") +": "
                + opponentResult.getScore());
        
        //render next play, time
        if(yourResult.isNext()){
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
        Image bombicon = null;
        try{
            bombicon = ImageIO.read(new FileInputStream(new File("rsc/mr_bomb.png")));
        }catch(Exception e){
            e.printStackTrace();
        }
        // get round
        Round round = game.getRounds().get(nextRound-1);
        List<Square> squares = round.getSquares();
        for(Square sq : squares) {
            JButton btn = buttons[sq.getID()-1];
            if(sq.isClicked()){
                btn.setEnabled(false);
                if(sq.isBomb()){
                    //btn.setIcon(defaultIcon);
                    //btn.setText("B");
                    if(bombicon != null) {
                        btn.setIcon(new ImageIcon(bombicon));
                        btn.setDisabledIcon(new ImageIcon(bombicon));
                    }else {
                        btn.setText("B");
                    }
                }else{
                    btn.setText(sq.getValue()+"");
                }
                btn.setBackground(Color.decode(sq.getColor()));
            }
        }
        //do smt
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if(lockGrid) return;
        for(int i=0; i<buttons.length; i++){
            if(ae.getSource() == buttons[i]){
                //get round
                Round round = game.getRounds().get(nextRound-1);
                Square sq = round.getSquares().get(i);
                if(sq.isClicked())
                    return;
                sq.setRound(round);
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
    public void informPlayerIn(ObjectWrapper data) {
        if(opponent != null) {
            int playerID = (Integer)data.getData();
            if(opponent.getPlayer().getID() == playerID)
                opponentStat.setOnline(true);
        }
    }
    
    public void informPlayerOut(ObjectWrapper data) {
        if(opponent != null) {
            int playerID = (Integer)data.getData();
            if(opponent.getPlayer().getID() == playerID)
                opponentStat.setOnline(false);
        }
    }
    
    public int getNowRound() {
        for(int i=0; i<game.getRounds().size(); i++){
            if(game.getRounds().get(i).getTime_end() == null) 
                return i;
        }
        return game.getRounds().size() -1;
    }
    public void setNextRound(int nextRound){
        this.nextRound = nextRound;
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
    
    private JFrame frm = this;
    
    class CountRoundTime extends TimerTask {
        public CountRoundTime() {
            super();
        }
        @Override
        public void run() {
            roundTime -= 1;
            lbRoundTime.setText(" Next round in "+roundTime+" seconds ...");
            if(roundTime > 0) {
                roundTimer.cancel();
                roundTimer = new Timer();
                roundTimer.schedule(new CountRoundTime(), 1000);
            } else {
                Container x = lbRoundTime.getParent();
                x.remove(lbRoundTime);
                frm.setSize(frm.getWidth(), frm.getHeight());
            }
        }
    }

}