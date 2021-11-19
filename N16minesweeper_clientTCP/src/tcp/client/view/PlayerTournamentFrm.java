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
import javax.swing.table.DefaultTableModel;
 
import model.ObjectWrapper;
import model.TournamentPlayer;
import tcp.client.control.ClientCtr;

public class PlayerTournamentFrm extends JFrame implements ActionListener{
    private ArrayList<TournamentPlayer> listTournamentPlayer;
    private JTable tblResult;
    private ClientCtr mySocket;
    public PlayerTournamentFrm(ClientCtr socket){
        super("Your Tournaments");
        mySocket = socket;
        listTournamentPlayer = new ArrayList<TournamentPlayer>();
         
        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width-5, this.getSize().height-20);       
        pnMain.setLayout(new BoxLayout(pnMain,BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));
        JLabel lblHome = new JLabel("Your Tournaments");
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
        
        PlayerTournamentFrm frm = this;
        tblResult.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblResult.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
                int row = e.getY() / tblResult.getRowHeight(); // get the row of the button
 
                // *Checking the row or column is valid or not
                if (row < tblResult.getRowCount() && row >= 0 && column < tblResult.getColumnCount() && column >= 0) {
                    //search and delete all existing previous view
                    JOptionPane.showMessageDialog(frm, "this function is under construction");
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
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_PLAYER_TOURNAMENT, this));
        mySocket.sendData(new ObjectWrapper(ObjectWrapper.GET_LIST_PLAYER_TOURNAMENT, mySocket.getUser().getID()));
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
            listTournamentPlayer = (ArrayList<TournamentPlayer>)data.getData();
            updateResultTable();
        }
    }
    
    public void updateResultTable(){
        String[] columnNames = {"Tournament name","ID","time begin","time end","day join"};
        String[][] value = new String[listTournamentPlayer.size()][columnNames.length];
        for(int i=0; i<listTournamentPlayer.size(); i++){
            value[i][0] = listTournamentPlayer.get(i).getTournament().getTrm_name();
            value[i][1] = listTournamentPlayer.get(i).getTournament().getID()+"";
            value[i][2] = listTournamentPlayer.get(i).getTournament().getTime_begin().toString();
            value[i][3] = listTournamentPlayer.get(i).getTournament().getTime_end().toString();
            value[i][4] = listTournamentPlayer.get(i).getDay_join().toString();
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
    
}