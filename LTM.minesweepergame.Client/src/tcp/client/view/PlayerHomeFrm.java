/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp.client.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import model.Challenge;
import model.FriendRequest;
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
public class PlayerHomeFrm extends javax.swing.JFrame {
    private ClientCtr mySocket;
    private ArrayList<PlayerRank> listFriend;
    private ArrayList<FriendRequest> listFriendRequest;
    private ArrayList<TournamentPlayer> listTournamentPlayer;
    private ArrayList<PlayerRank> listRank;
    private ArrayList<Challenge> listChallenge;
    
    /**
     * Creates new form PlayerHomeFrm
     */
    public PlayerHomeFrm(ClientCtr socket) {
        super("Player Home - Hello "+ socket.getUser().getFullname());
        mySocket = socket;
        listFriend = new ArrayList<PlayerRank>();
        listFriendRequest = new ArrayList<FriendRequest>();
        listTournamentPlayer = new ArrayList<TournamentPlayer>();
        listRank = new ArrayList<PlayerRank>();
        listChallenge = new ArrayList<Challenge>();
        initComponents();
        addTableListener();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mySocket.getActiveFunction().add(new ObjectWrapper(0, this));
        mySocket.sendData(new ObjectWrapper(ObjectWrapper.GET_LIST_FRIEND, null));
        mySocket.sendData(new ObjectWrapper(ObjectWrapper.GET_LIST_PLAYER_TOURNAMENT, mySocket.getUser().getID()));
        mySocket.sendData(new ObjectWrapper(ObjectWrapper.GET_LIST_PLAYER_RANK, null));
    }
    
    public void addTableListener(){
        PlayerHomeFrm frm = this;
        tblFriend.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblFriend.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
                int row = e.getY() / tblFriend.getRowHeight(); // get the row of the button
 
                // *Checking the row or column is valid or not
                if (row < tblFriend.getRowCount() && row >= 0 && column < tblFriend.getColumnCount() && column >= 0) {
                    //search and delete all existing previous view
                    PlayerRank r = listFriend.get(row);
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
        tblTournament.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblTournament.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
                int row = e.getY() / tblTournament.getRowHeight(); // get the row of the button
 
                // *Checking the row or column is valid or not
                if (row < tblTournament.getRowCount() && row >= 0 && column < tblTournament.getColumnCount() && column >= 0) {
                    //search and delete all existing previous view
                    JOptionPane.showMessageDialog(frm, "this function is under construction");
                }
            }
        });
        tblRank.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblRank.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
                int row = e.getY() / tblRank.getRowHeight(); // get the row of the button
 
                // *Checking the row or column is valid or not
                if (row < tblRank.getRowCount() && row >= 0 && column < tblRank.getColumnCount() && column >= 0) {
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
        tblChallenge.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblChallenge.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
                int row = e.getY() / tblChallenge.getRowHeight(); // get the row of the button
 
                // *Checking the row or column is valid or not
                if (row < tblChallenge.getRowCount() && row >= 0) {
                    
                    Challenge challenge = listChallenge.get(row);
                    if(tblChallenge.getColumnName(column).equals("Accept")){
                        challenge.setAccepted(true);
                        mySocket.sendData(new ObjectWrapper(ObjectWrapper.ANSWER_CHALLENGE, challenge));
                        listChallenge.remove(challenge);
                        updateTableChallenge();
                        
                    }
                    else if(tblChallenge.getColumnName(column).equals("Decline")){
                        challenge.setAccepted(false);
                        mySocket.sendData(new ObjectWrapper(ObjectWrapper.ANSWER_CHALLENGE, challenge));
                        listChallenge.remove(challenge);
                        updateTableChallenge();
                    }
                }
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane5 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblFriend = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblGroup = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblTournament = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblRank = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblChallenge = new javax.swing.JTable();
        jPanel12 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblFriendRequest = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setText("Your Friends");

        tblFriend.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblFriend);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(jLabel3)
                .addContainerGap(158, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setText("Your Groups");

        tblGroup.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Group describe"
            }
        ));
        jScrollPane2.setViewportView(tblGroup);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(105, 105, 105)
                .addComponent(jLabel4)
                .addContainerGap(114, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane5.addTab("Friends", jPanel4);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText("Your Tournaments");

        tblTournament.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tournament", "Begin", "End", "Day join"
            }
        ));
        jScrollPane5.setViewportView(tblTournament);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(197, 197, 197))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane5.addTab("Tournaments", jPanel6);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("Player Ranks");

        tblRank.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Rank", "Player name", "Number of win", "Win rate"
            }
        ));
        jScrollPane6.setViewportView(tblRank);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 625, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(213, 213, 213)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addGap(9, 9, 9))
        );

        jTabbedPane5.addTab("Ranks", jPanel7);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setText("Your Challenges");

        tblChallenge.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "From", "Accept", "Decline"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblChallenge);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addComponent(jLabel5)
                .addContainerGap(131, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setText("Friend requests");

        tblFriendRequest.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "From", "Accept", "Delete"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tblFriendRequest);
        if (tblFriendRequest.getColumnModel().getColumnCount() > 0) {
            tblFriendRequest.getColumnModel().getColumn(2).setResizable(false);
        }

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(105, 105, 105)
                .addComponent(jLabel6)
                .addContainerGap(91, Short.MAX_VALUE))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane5.addTab("Challenges & Friend requests", jPanel10);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 650, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane5)
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
            java.util.logging.Logger.getLogger(PlayerHomeFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PlayerHomeFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PlayerHomeFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PlayerHomeFrm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PlayerHomeFrm(null).setVisible(true);
            }
        });
    }
    
    public void receivedDataProcessing(ObjectWrapper data) {
        if(data.getPerformative() == ObjectWrapper.SERVER_INFORM_CHALLENGE){
            if(data.getData() instanceof Challenge) {
                Challenge c = (Challenge)data.getData();
                JOptionPane.showMessageDialog(this, "new challenge from "+c.getFromPlayer().getFullname());
                listChallenge.add(c);
                updateTableChallenge();
            }
        }
        else if(data.getPerformative() == ObjectWrapper.REPLY_GET_LIST_FRIEND) {
            if(data.getData() instanceof ArrayList<?>) {
                listFriend = (ArrayList<PlayerRank>)data.getData();
                updateTableFriend();
            }
        }
        else if(data.getPerformative() == ObjectWrapper.REPLY_GET_LIST_PLAYER_RANK) {
            if(data.getData() instanceof ArrayList<?>) {
                listRank = (ArrayList<PlayerRank>)data.getData();
                updateTableRank();
            }
        }
        else if(data.getPerformative() == ObjectWrapper.REPLY_GET_LIST_PLAYER_TOURNAMENT) {
            if(data.getData() instanceof ArrayList<?>) {
                listTournamentPlayer = (ArrayList<TournamentPlayer>)data.getData();
                updateTableTournament();
            }
        }
        else if(data.getPerformative() == ObjectWrapper.SERVER_INFORM_USER_IN) {
            informPlayerOnline(data);
        }
        else if(data.getPerformative() == ObjectWrapper.SERVER_INFORM_USER_OUT) {
            informPlayerOffline(data);
        }
        else if(data.getPerformative() == ObjectWrapper.SERVER_REPLY_ANSWER) {
            if(data.getData() instanceof String) {
                if(data.getData().equals("expired")){
                    JOptionPane.showMessageDialog(this, "Challenge is expired !");
                }
                if(data.getData().equals("busy")){
                    JOptionPane.showMessageDialog(this, "Player challenging is busy !");
                }
                if(data.getData().equals("offline")){
                    JOptionPane.showMessageDialog(this, "Player challenging is offline !");
                }
                if(data.getData().equals("game start")){
                    // ksdjfiObjectWrapper existed = null;

                    JOptionPane.showMessageDialog(this, "Game start !");
                    new GameFrm(mySocket).setVisible(true);
                }
            }
        }
    }
    
    public void updateTableFriend(){
        String[] columnNames = {"Player","State"};
        String[][] value = new String[listFriend.size()][columnNames.length];
        for(int i=0; i<listFriend.size(); i++){
            value[i][0] = listFriend.get(i).getFullname() +" (Rank: "+ listFriend.get(i).getRank()+")";
            value[i][1] = listFriend.get(i).isOnline() ? "online" : "offline";
        }
        DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
               //unable to edit cells
               return false;
            }
        };
        tblFriend.setModel(tableModel);
        tblFriend.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        for(PlayerRank player : listFriend)
            if(player.getID() == playerID)
                player.setOnline(true);
        updateTableFriend();
        for(PlayerRank player : listRank)
            if(player.getID() == playerID)
                player.setOnline(true);
        updateTableRank();
    }
    public void informPlayerOffline(ObjectWrapper data){
        int playerID = (Integer)data.getData();
        for(PlayerRank player : listFriend)
            if(player.getID() == playerID)
                player.setOnline(false);
        updateTableFriend();
        for(PlayerRank player : listRank)
            if(player.getID() == playerID)
                player.setOnline(false);
        updateTableRank();
    }
    
    public void updateTableFriendRequest(){
        String[] columnNames = {"From player","Accept","Delete"};
        Object[][] value = new String[listFriendRequest.size()][columnNames.length];
        for(int i=0; i<listFriendRequest.size(); i++){
            value[i][0] = listFriendRequest.get(i).getFromPlayer().getFullname()
                    +" ("+listFriendRequest.get(i).getFromPlayer().getUsername()+")";
            value[i][1] = new JButton("accept");
            value[i][2] = new JButton("delete");
        }
        JTableButtonModel model = new JTableButtonModel();
        model.setColumns(columnNames);
        model.setRows(value);
        tblFriendRequest.setModel(model);
        TableCellRenderer tableRenderer = tblChallenge.getDefaultRenderer(JButton.class);
        tblFriendRequest.setDefaultRenderer(JButton.class, new JTableButtonRenderer(tableRenderer));
    }
    
    public void updateTableTournament(){
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
        tblTournament.setModel(tableModel);
    }
    
    public void updateTableRank(){
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
        tblRank.setModel(tableModel);
    }
    
    public void updateTableChallenge(){
        String[] columnNames = {"Player","Time","Accept","Decline"};
        Object[][] value = new Object[listChallenge.size()][columnNames.length];
        
        for(int i=0; i<listChallenge.size(); i++){
            Challenge challenge = listChallenge.get(i);
            value[i][0] = challenge.getFromPlayer().getFullname()+" ("
                    +challenge.getFromPlayer().getUsername() +")";
            value[i][1] = challenge.getTime()+"";
            value[i][2] = new JButton("accept");
            value[i][3] = new JButton("refuse");
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
        tblChallenge.setModel(model);
        TableCellRenderer tableRenderer = tblChallenge.getDefaultRenderer(JButton.class);
        tblChallenge.setDefaultRenderer(JButton.class, new JTableButtonRenderer(tableRenderer));
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JTable tblChallenge;
    private javax.swing.JTable tblFriend;
    private javax.swing.JTable tblFriendRequest;
    private javax.swing.JTable tblGroup;
    private javax.swing.JTable tblRank;
    private javax.swing.JTable tblTournament;
    // End of variables declaration//GEN-END:variables
}
