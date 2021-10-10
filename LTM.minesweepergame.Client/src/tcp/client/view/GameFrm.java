package tcp.client.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.Game;
import model.GamePlayer;
import model.ObjectWrapper;
import model.Square;
import tcp.client.control.ClientCtr;


//Following is the implementation of Minesweeper.



public class GameFrm extends JFrame implements ActionListener{
          
    JButton reset = new JButton("Reset");       //Reset Button as a side.
    JButton giveUp = new JButton("Give Up");    //Similarly, give up button.  
    JPanel ButtonPanel = new JPanel();          
    int[][] counts;                             //integer array to store counts of each cell. Used as a back-end for comparisons.
    JButton[][] buttons;                        //Buttons array to use as a front end for the game.
    int size=10,diff;                              
    final int MINE = 10;        
    private ClientCtr mySocket; 
    Game game;
    /**
    @param size determines the size of the board
    */

    public GameFrm(ClientCtr socket){
        super("Minesweeper Game");
        mySocket = socket;      

        counts = new int[size][size];
        buttons = new JButton[size][size];  
             
        this.setLayout(new BorderLayout());           
        this.add(ButtonPanel,BorderLayout.SOUTH);     
        reset.addActionListener(this);                 
        giveUp.addActionListener(this);                


       Container grid = new Container();  
        grid.setLayout(new GridLayout(size,size));    

        for(int a = 0; a < buttons.length; a++)
        {
            for(int b = 0; b < buttons[0].length; b++)
            {
                buttons[a][b] = new JButton();            
                buttons[a][b].addActionListener(this);     
                grid.add(buttons[a][b]);                  
            }
        }
        // above initializes each button in the minesweeper board and gives it functionality. 

        ButtonPanel.add(reset);                        
        ButtonPanel.add(giveUp);       // adding buttons to the panel.

        this.add(grid,BorderLayout.CENTER);           //calling function to start the game by filling mines.

        this.setLocationRelativeTo(null);          //this stuff
        
        
        this.setSize(300,300);                   
        this.setLocation(200,10);   
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.SERVER_UPDATE_GAME_STAT, this));
        
    }
    
    public void receivedDataProcessing(ObjectWrapper data) {
        // set game
        if(data.getData() instanceof Game){
            game = (Game)data.getData();
            this.setVisible(true);
        }
        
        //update frm
        
        //inform winner
        List<GamePlayer> players = game.getPlayers();
        GamePlayer you = (players.get(0).getPlayer().getID() == mySocket.getUser().getID() ? players.get(0) : players.get(1));
        if(game.getTime_end()!= null){
            String message = "Game over ! You "+ (you.isIs_winner() ? "win":"lose .");
            JOptionPane.showMessageDialog(this, message);
            
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

    @Override
    public void actionPerformed(ActionEvent ae) {
        for(int x = 0; x < size; x++) {
            for( int y = 0; y < size; y++) {
                if(ae.getSource() == buttons[x][y]){
                    Square sq = new Square();
                    sq.setGame(game);
                    mySocket.sendData(new ObjectWrapper(ObjectWrapper.MAKE_A_MOVE, sq));
                }
            }
        }
        
    }
    /**
     * Function to check whether user has lost the game ( i.e clicked a mine).
     * @param m indicated whether the function has been called when user clicks a mine( m=1)
     * or when he clicks the give up button.(m = any other integer).
     * Shows a dialog box which tells the user that they have lost the game.
     */
    

}