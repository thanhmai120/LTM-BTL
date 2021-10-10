package tcp.client.view;
 
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
 
import model.ObjectWrapper;
import model.PlayerRank;
import tcp.client.control.ClientCtr;

public class FriendRankFrm extends JFrame implements ActionListener{
    private ArrayList<PlayerRank> listFriendRank;
    private JTable tblResult;
    private ClientCtr mySocket;
     
    public FriendRankFrm(ClientCtr socket){
        super("Your Friends");
        mySocket = socket;
        listFriendRank = new ArrayList<PlayerRank>();
         
        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width-5, this.getSize().height-20);       
        pnMain.setLayout(new BoxLayout(pnMain,BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));
         
        JLabel lblHome = new JLabel("Your Friends");
        lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);  
        lblHome.setFont (lblHome.getFont ().deriveFont (20.0f));
        pnMain.add(lblHome);
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));
 
        JPanel pn2 = new JPanel();
        pn2.setLayout(new BoxLayout(pn2,BoxLayout.Y_AXIS));     
        tblResult = new JTable();
        JScrollPane scrollPane= new  JScrollPane(tblResult);
        tblResult.setFillsViewportHeight(false); 
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 250));
        
        FriendRankFrm frm = this;
        tblResult.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblResult.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
                int row = e.getY() / tblResult.getRowHeight(); // get the row of the button
 
                // *Checking the row or column is valid or not
                if (row < tblResult.getRowCount() && row >= 0 && column < tblResult.getColumnCount() && column >= 0) {
                    //search and delete all existing previous view
                    PlayerRank r = listFriendRank.get(row);
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
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_FRIEND, this));
        mySocket.sendData(new ObjectWrapper(ObjectWrapper.GET_LIST_FRIEND, null));
    }
 
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        JButton btnClicked = (JButton)e.getSource();
        
    }
     
    /**
     * Treatment of search result received from the server
     * @param data
     */
    public void receivedDataProcessing(ObjectWrapper data) {
        if(data.getData() instanceof ArrayList<?>) {
            listFriendRank = (ArrayList<PlayerRank>)data.getData();
            updateResultTable();
        }
    }
    
    public void updateResultTable(){
        String[] columnNames = {"Player","State"};
        String[][] value = new String[listFriendRank.size()][columnNames.length];
        for(int i=0; i<listFriendRank.size(); i++){
            value[i][0] = listFriendRank.get(i).getFullname() +" (Rank: "+ listFriendRank.get(i).getRank()+")";
            value[i][1] = listFriendRank.get(i).isOnline() ? "online" : "offline";
        }
        DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               //unable to edit cells
               return false;
            }
        };
        tblResult.setModel(tableModel);
        tblResult.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                c.setForeground(((String)value).equals("online") ? Color.GREEN : ((String)value).equals("offline") ?
                        Color.RED : Color.BLACK);
                return c;
            }
        });
    }
    
    public void informPlayerOnline(ObjectWrapper data){
        int playerID = (Integer)data.getData();
        for(PlayerRank player : listFriendRank)
            if(player.getID() == playerID)
                player.setOnline(true);
        updateResultTable();
    }
    public void informPlayerOffline(ObjectWrapper data){
        int playerID = (Integer)data.getData();
        for(PlayerRank player : listFriendRank)
            if(player.getID() == playerID)
                player.setOnline(false);
        updateResultTable();
    }
}