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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
 
import model.ObjectWrapper;
import model.Challenge;
import tcp.client.control.ClientCtr;

public class ChallengeFrm extends JFrame implements ActionListener{
    private ArrayList<Challenge> listChallenge;
    private JTable tblResult;
    private ClientCtr mySocket;
    public ChallengeFrm(ClientCtr socket){
        super("Your Challenges");
        mySocket = socket;
        listChallenge = new ArrayList<Challenge>();
         
        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width-5, this.getSize().height-20);       
        pnMain.setLayout(new BoxLayout(pnMain,BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0,10)));
        JLabel lblHome = new JLabel("Your Challenges");
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
        
        ChallengeFrm frm = this;
        tblResult.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblResult.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
                int row = e.getY() / tblResult.getRowHeight(); // get the row of the button
 
                // *Checking the row or column is valid or not
                if (row < tblResult.getRowCount() && row >= 0) {
                    
                    Challenge challenge = listChallenge.get(row);
                    if(tblResult.getColumnName(column).equals("Accept")){
                        challenge.setAccepted(true);
                        mySocket.sendData(new ObjectWrapper(ObjectWrapper.ANSWER_CHALLENGE, challenge));
                        listChallenge.remove(challenge);
                        updateResultTable();
                        
                    }
                    else if(tblResult.getColumnName(column).equals("Decline")){
                        challenge.setAccepted(false);
                        mySocket.sendData(new ObjectWrapper(ObjectWrapper.ANSWER_CHALLENGE, challenge));
                        listChallenge.remove(challenge);
                        updateResultTable();
                    }
                }
            }
        });
 
        pn2.add(scrollPane);
        pnMain.add(pn2);    
        this.add(pnMain);
        this.setSize(600,300);              
        this.setLocation(200,10);
        //this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_CHALLENGE, this));
        
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
        if(data.getData() instanceof Challenge) {
            Challenge c = (Challenge)data.getData();
            JOptionPane.showMessageDialog(mySocket.getView(), "new challenge from "+c.getFromPlayer().getFullname());
            listChallenge.add(c);
            updateResultTable();
        }
    }
    
    public void updateResultTable(){
        String[] columnNames = {"Player","State","Accept","Decline"};
        Object[][] value = new Object[listChallenge.size()][columnNames.length];
        
        for(int i=0; i<listChallenge.size(); i++){
            Challenge challenge = listChallenge.get(i);
            value[i][0] = challenge.getFromPlayer().getFullname()+" ("
                    +challenge.getFromPlayer().getUsername() +")";
            value[i][1] = challenge.getTime()+"";
            JButton btnAcc = new JButton("accept");
            value[i][2] = btnAcc;
            JButton btnref= new JButton("refuse");
            value[i][3] = btnref;
        }
        DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               //unable to edit cells
               return false;
            }
        };
        JTableButtonModel model = new JTableButtonModel();
        model.setColumns(columnNames);
        model.setRows(value);
        tblResult.setModel(model);
        TableCellRenderer tableRenderer = tblResult.getDefaultRenderer(JButton.class);
        tblResult.setDefaultRenderer(JButton.class, new JTableButtonRenderer(tableRenderer));
    }
    
    class JTableButtonRenderer implements TableCellRenderer {
        private TableCellRenderer defaultRenderer;
        public JTableButtonRenderer(TableCellRenderer renderer) {
           defaultRenderer = renderer;
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
           if(value instanceof Component)
              return (Component)value;
           return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
     }
     class JTableButtonModel extends AbstractTableModel {
        private Object[][] rows = {{"Button1", new JButton("Button1")},{"Button2", new JButton("Button2")},{"Button3", new JButton("Button3")}, {"Button4", new JButton("Button4")}};
        private String[] columns = {"Count", "Buttons"};

        public Object[][] getRows() {
            return rows;
        }

        public void setRows(Object[][] rows) {
            this.rows = rows;
        }

        public String[] getColumns() {
            return columns;
        }

        public void setColumns(String[] columns) {
            this.columns = columns;
        }
        
        
        public String getColumnName(int column) {
           return columns[column];
        }
        public int getRowCount() {
           return rows.length;
        }
        public int getColumnCount() {
           return columns.length;
        }
        public Object getValueAt(int row, int column) {
           return rows[row][column];
        }
        public boolean isCellEditable(int row, int column) {
           return false;
        }
        public Class getColumnClass(int column) {
           return getValueAt(0, column).getClass();
        }
     }
}