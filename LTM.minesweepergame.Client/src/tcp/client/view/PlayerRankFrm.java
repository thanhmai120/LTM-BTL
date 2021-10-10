package tcp.client.view;
 
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
 
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
 
import model.ObjectWrapper;
import model.PlayerRank;
import tcp.client.control.ClientCtr;

public class PlayerRankFrm extends JFrame implements ActionListener{
    private ArrayList<PlayerRank> listRank;
    private JTable tblResult;
    private ClientCtr mySocket;
    private JButton btnReload;
     
    public PlayerRankFrm(ClientCtr socket){
        super("Player Ranks");
        mySocket = socket;
        listRank = new ArrayList<PlayerRank>();
         
        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width-5, this.getSize().height-20);       
        pnMain.setLayout(new BoxLayout(pnMain,BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));
         
        JLabel lblHome = new JLabel("Player ranks");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);  
        lblHome.setFont (lblHome.getFont ().deriveFont (20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0,20)));
         
        JPanel pn1 = new JPanel();
        pn1.setLayout(new BoxLayout(pn1,BoxLayout.X_AXIS));
        pn1.setSize(this.getSize().width-5, 20);
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));
        btnReload = new JButton("Reload");
        btnReload.addActionListener(this);
        pn1.add(btnReload);
        pnMain.add(pn1);
 
        JPanel pn2 = new JPanel();
        pn2.setLayout(new BoxLayout(pn2,BoxLayout.Y_AXIS));     
        tblResult = new JTable();
        JScrollPane scrollPane= new  JScrollPane(tblResult);
        tblResult.setFillsViewportHeight(false); 
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 250));
        
        PlayerRankFrm frm = this;
        tblResult.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblResult.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
                int row = e.getY() / tblResult.getRowHeight(); // get the row of the button
 
                // *Checking the row or column is valid or not
                if (row < tblResult.getRowCount() && row >= 0 && column < tblResult.getColumnCount() && column >= 0) {
                    //search and delete all existing previous view
                    PlayerRank r = listRank.get(row);
                    ObjectWrapper existed = null;
                    for(ObjectWrapper func: mySocket.getActiveFunction())
                        if(func.getData() instanceof PlayerDetailFrm) {
                            existed = func;
                        }
                    if(existed != null){
                        JOptionPane.showMessageDialog(frm, "You are seeing another player now !");
                        return;
                    }
                    new PlayerDetailFrm(mySocket,r).setVisible(true);
                }
            }
        });
 
        pn2.add(scrollPane);
        pnMain.add(pn2);    
        this.add(pnMain);
        this.setSize(600,300);              
        this.setLocation(200,10);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_PLAYER_RANK, this));
        mySocket.sendData(new ObjectWrapper(ObjectWrapper.GET_LIST_PLAYER_RANK, null));
    }
 
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        JButton btnClicked = (JButton)e.getSource();
        if(btnClicked == btnReload){
            mySocket.sendData(new ObjectWrapper(ObjectWrapper.GET_LIST_PLAYER_RANK, null));
        }
    }
     
    /**
     * Treatment of search result received from the server
     * @param data
     */
    public void receivedDataProcessing(ObjectWrapper data) {
        if(data.getData() instanceof ArrayList<?>) {
            listRank = (ArrayList<PlayerRank>)data.getData();
            updateResultTable();
        }
    }
    
    
    public void updateResultTable(){
        String[] columnNames = {"Rank","Player","Win number","Win rate","Opponent's win rate","Trms score"};
        String[][] value = new String[listRank.size()][columnNames.length];
        DecimalFormat df = new DecimalFormat("#.##");
        for(int i=0; i<listRank.size(); i++){
            value[i][0] = listRank.get(i).getRank()+"";
            value[i][1] = listRank.get(i).getFullname();
            value[i][2] = listRank.get(i).getWin_number()+"";
            value[i][3] = df.format(listRank.get(i).getWin_rate()*100)+" %";
            value[i][4] = df.format(listRank.get(i).getAvg_opponent_win_rate()*100)+" %";
            value[i][5] = listRank.get(i).getTrm_score()+"";
            
        }
            
        DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               //unable to edit cells
               return false;
            }
        };
        tblResult.setModel(tableModel);
    }
    
    public void informPlayerOnline(ObjectWrapper data){
        int playerID = (Integer)data.getData();
        for(PlayerRank player : listRank)
            if(player.getID() == playerID)
                player.setOnline(true);
        updateResultTable();
    }
    public void informPlayerOffline(ObjectWrapper data){
        int playerID = (Integer)data.getData();
        for(PlayerRank player : listRank)
            if(player.getID() == playerID)
                player.setOnline(false);
        updateResultTable();
    }
}