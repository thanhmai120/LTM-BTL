/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp.client.view;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import model.Challenge;
import model.FriendRequest;
import model.Game;
import model.GamePlayer;
import model.Invitation;
import model.ObjectWrapper;
import model.Player;
import model.PlayerRank;
import model.Tournament;
import model.TournamentPlayer;
import tcp.client.control.ClientCtr;

/**
 *
 * @author Administrator
 */
public class TournamentFrm extends javax.swing.JFrame {
    private ClientCtr mySocket;
    private List<TournamentPlayer> listTournamentPlayer;
    private Tournament myTournament;
    private TournamentPlayer myTournamentPlayer;
    private List<PlayerRank> listFriendInvite;
    
    /**
     * Creates new form TournamentFrm
     */
    public TournamentFrm(ClientCtr socket,Tournament tournament, List<PlayerRank> listFriend) {
        super("Tournament "+ tournament.getTrm_name());
        initComponents();
        jLabel3.setText(""+tournament.getTrm_name());
        mySocket = socket;
        myTournament = tournament;
        listTournamentPlayer = new ArrayList<>();
        listFriendInvite = new ArrayList<>();
        for(PlayerRank fr : listFriend) {
            listFriendInvite.add(fr);
        }
//        addTableListener();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_TOURNAMENT_PLAYER, this));
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_SEND_INVITATION, this));
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_NEW_TOURNAMENT_PLAYER, this));
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_START_TRM_GAME, this));
        tblPlayer.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblPlayer.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
                int row = e.getY() / tblPlayer.getRowHeight(); // get the row of the button
 
                // *Checking the row or column is valid or not
                if (row < tblPlayer.getRowCount() && row >= 0) {
                    
                    PlayerRank toPlayer = listFriendInvite.get(row);
                    if(tblPlayer.getColumnName(column).equals("Invite")){
                        Invitation inv = new Invitation();
                        inv.setTime(new Timestamp(System.currentTimeMillis()));
                        inv.setExpired(null);
                        inv.setPlayer(toPlayer);
                        inv.setTournament(myTournament);
                        inv.setUser(mySocket.getUser());
                        mySocket.sendData(new ObjectWrapper(ObjectWrapper.SEND_INVITATION, inv));
                    }
                }
            }
        });
        
        tblTournamentPlayer.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblTournamentPlayer.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
                int row = e.getY() / tblTournamentPlayer.getRowHeight(); // get the row of the button
 
                // *Checking the row or column is valid or not
                if (row < tblTournamentPlayer.getRowCount() && row >= 0) {
                    TournamentPlayer tp = listTournamentPlayer.get(row);
                    if(tp == myTournamentPlayer)
                        return;
                    if(tblTournamentPlayer.getColumnName(column).equals("Action")){
                        if(tp.getGamePlayers().size() > 0)
                            mySocket.sendData(new ObjectWrapper(ObjectWrapper.SEE_GAME_RESULT, tp.getGamePlayers().get(0).getGame()));
                        else {
                            Game g = new Game();
                            g.setTournament(myTournament);
                            List<GamePlayer> players = new ArrayList<>();
                            GamePlayer p1 = new GamePlayer();
                            p1.setTournamentPlayer(tp);
                            p1.setPlayer(tp.getPlayer());
                            GamePlayer p2 = new GamePlayer();
                            p2.setTournamentPlayer(myTournamentPlayer);
                            p2.setPlayer(myTournamentPlayer.getPlayer());
                            players.add(p1);
                            players.add(p2);
                            g.setPlayers(players);
                            mySocket.sendData(new ObjectWrapper(ObjectWrapper.START_TRM_GAME, g));
                        }
                    }
                }
            }
        });
        updateTableTournamentPlayer();
        refreshListFriendInvite();
         mySocket.sendData(new ObjectWrapper(ObjectWrapper.GET_LIST_TOURNAMENT_PLAYER, myTournament));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblTournamentPlayer = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPlayer = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblTournamentPlayer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Player", "Full name", "Email", "Tel", "Action"
            }
        ));
        jScrollPane1.setViewportView(tblTournamentPlayer);

        tblPlayer.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Player", "Fullname", "Invite"
            }
        ));
        jScrollPane2.setViewportView(tblPlayer);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 17)); // NOI18N
        jLabel1.setText("Tournament Player");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Invite your friend");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 22)); // NOI18N
        jLabel3.setText("Tournament ...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 238, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TournamentFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TournamentFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TournamentFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TournamentFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
//                new TournamentFrm().setVisible(true);
            }
        });
    }
    
    public void receivedDataProcessing(ObjectWrapper data) {
        if(data.getPerformative() == ObjectWrapper.REPLY_GET_LIST_TOURNAMENT_PLAYER){
            listTournamentPlayer = (List<TournamentPlayer>)data.getData();
            for(TournamentPlayer tp : listTournamentPlayer) {
                if(tp.getPlayer().getID() == mySocket.getUser().getID()) {
                    myTournamentPlayer = tp;
                    break;
                }
            }
            updateTableTournamentPlayer();
            refreshListFriendInvite();
            
        } else if (data.getPerformative() == ObjectWrapper.SERVER_INFORM_NEW_TOURNAMENT_PLAYER) {
            TournamentPlayer p = (TournamentPlayer) data.getData();
            if(p.getID()>0 && p.getTournament().getID() == myTournament.getID()) {
                listTournamentPlayer.add(p);
                refreshListFriendInvite();
                updateTableTournamentPlayer();
            }
        } else if(data.getPerformative() == ObjectWrapper.REPLY_SEND_INVITATION) {
            if(data.getData() instanceof String && data.getData().equals("ok")) 
                JOptionPane.showMessageDialog(this, "invitation sent !");
            else
                JOptionPane.showMessageDialog(this, "invitation fail !");
        } else if(data.getPerformative() == ObjectWrapper.REPLY_START_TRM_GAME) {
            if(data.getData() instanceof String && data.getData().equals("busy")) 
                JOptionPane.showMessageDialog(this, "players is busy !");
            else if(data.getData() instanceof String && data.getData().equals("offline")) 
                JOptionPane.showMessageDialog(this, "players is offline !");
        }
    }
    
    public void updateTableTournamentPlayer() {
        String[] columnNames = {"Player","Fullname","Email","Action"};
        String[][] value = new String[listTournamentPlayer.size()][columnNames.length];
        for(int i=0; i<listTournamentPlayer.size(); i++){
            value[i][0] = listTournamentPlayer.get(i).getPlayer().getUsername();
            value[i][1] = listTournamentPlayer.get(i).getPlayer().getFullname();
            value[i][2] = listTournamentPlayer.get(i).getPlayer().getEmail();
            if(listTournamentPlayer.get(i).getGamePlayers().size() > 0)
                value[i][3] = "game result";
            else 
                value[i][3] = "start game";
            if(listTournamentPlayer.get(i) == myTournamentPlayer)
                value[i][3] = ""; 
        }
        DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               //unable to edit cells
               return false;
            }
        };
        tblTournamentPlayer.setModel(tableModel);
    }

    public void updateTablePlayerInvite(){
        String[] columnNames = {"Player","Fullname","Invite"};
        Object[][] value = new Object[listFriendInvite.size()][columnNames.length];
        
        for(int i=0; i<listFriendInvite.size(); i++){
            PlayerRank p = listFriendInvite.get(i);
            value[i][0] = p.getUsername();
            value[i][1] = p.getFullname();
            value[i][2] = new JButton("invite");
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
        tblPlayer.setModel(model);
        TableCellRenderer tableRenderer = tblPlayer.getDefaultRenderer(JButton.class);
        tblPlayer.setDefaultRenderer(JButton.class, new JTableButtonRenderer(tableRenderer));
    }
    
    public void refreshListFriendInvite() {
        for(TournamentPlayer tp : listTournamentPlayer) {
            for(PlayerRank r : listFriendInvite) 
                if(r.getID() == tp.getPlayer().getID()) {
                    listFriendInvite.remove(r);
                    break;
                }
        }
        updateTablePlayerInvite();
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblPlayer;
    private javax.swing.JTable tblTournamentPlayer;
    // End of variables declaration//GEN-END:variables
}
