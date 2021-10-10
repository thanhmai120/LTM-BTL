package tcp.server.control;
 
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.sql.Time;
import java.util.List;
import jdbc.dao.GameDAO;
import jdbc.dao.PlayerRankDAO;
import jdbc.dao.UserDAO;
import model.Challenge;
import model.Game;
import model.GamePlayer;
import model.IPAddress;
import model.ObjectWrapper;
import model.PlayerRank;
import model.PlayerStat;
import model.Square;
import model.User;
import tcp.server.view.ServerMainFrm;
 
public class ServerCtr {
    private ServerMainFrm view;
    private ServerSocket myServer;
    private ServerListening myListening;
    private ArrayList<ServerProcessing> myProcess;
    private ArrayList<Challenge> listChallenge;
    private ArrayList<Game> listGame;
    private IPAddress myAddress = new IPAddress("localhost",8888);  //default server host and port
     
    public ServerCtr(ServerMainFrm view){
        myProcess = new ArrayList<ServerProcessing>();
        listChallenge = new ArrayList<Challenge>();
        listGame = new ArrayList<Game>();
        this.view = view;
        openServer();       
    }
     
    public ServerCtr(ServerMainFrm view, int serverPort){
        myProcess = new ArrayList<ServerProcessing>();
        this.view = view;
        myAddress.setPort(serverPort);
        openServer();       
    }
     
     
    private void openServer(){
        try {
            myServer = new ServerSocket(myAddress.getPort());
            myListening = new ServerListening();
            myListening.start();
            myAddress.setHost(InetAddress.getLocalHost().getHostAddress());
            view.showServerInfor(myAddress);
            //System.out.println("server started!");
            view.showMessage("TCP server is running at the port " + myAddress.getPort() +"...");
        }catch(Exception e) {
            e.printStackTrace();;
        }
    }
     
    public void stopServer() {
        try {
            for(ServerProcessing sp:myProcess)
                sp.stop();
            myListening.stop();
            myServer.close();
            view.showMessage("TCP server is stopped!");
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
     
    public void publicClientNumber() {
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.SERVER_INFORM_CLIENT_NUMBER, myProcess.size());
        for(ServerProcessing sp : myProcess) {
            sp.sendData(data);
        }
    }
    
    public void informUserIn(ServerProcessing clientProcess, int userID){
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.SERVER_INFORM_USER_IN, userID);
        clientProcess.sendData(data);
    }
    
    public void publicInformUserIn(int userID){
        for(ServerProcessing clientProcess : myProcess){
            informUserIn(clientProcess,userID);
        }
    }
    
    public void informUserOut(ServerProcessing clientProcess, int userID){
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.SERVER_INFORM_USER_OUT, userID);
        clientProcess.sendData(data);
    }
    
    public void publicInformUserOut(int userID){
        for(ServerProcessing clientProcess : myProcess){
            informUserOut(clientProcess,userID);
        }
    }
     
    /**
     * The class to listen the connections from client, avoiding the blocking of accept connection
     *
     */
    class ServerListening extends Thread{
         
        public ServerListening() {
            super();
        }
         
        public void run() {
            view.showMessage("server is listening... ");
            try {
                while(true) {
                    Socket clientSocket = myServer.accept();
                    ServerProcessing sp = new ServerProcessing(clientSocket);
                    sp.start();
                    myProcess.add(sp);
                    view.showMessage("Number of client connecting to the server: " + myProcess.size());
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
     
    /**
     * The class to treat the requirement from client
     *
     */
    class ServerProcessing extends Thread{
        private Socket mySocket;
        private User loginUser;
        private PlayerStat stat;
        private Challenge myChallenge;
        //private ObjectInputStream ois;
        //private ObjectOutputStream oos;

        public PlayerStat getStat() {
            return stat;
        }
         
        public ServerProcessing(Socket s) {
            super();
            mySocket = s;
        }
         
        public void sendData(Object obj) {
            try {
                ObjectOutputStream oos= new ObjectOutputStream(mySocket.getOutputStream());
                oos.writeObject(obj);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
         
        public void run() { 
            try {
                while(true) {
                    ObjectInputStream ois = new ObjectInputStream(mySocket.getInputStream());
                    Object o = ois.readObject();
                    view.showMessage("data receive");
                    if(o instanceof ObjectWrapper){
                        view.showMessage("objectwrapper receive");
                        ObjectWrapper data = (ObjectWrapper)o;
                        switch(data.getPerformative()) {
                        case ObjectWrapper.LOGIN_USER:
                            User user = (User)data.getData();
                            UserDAO ud = new UserDAO();
                            loginUser = ud.checkLogin(user);
                            if(loginUser != null) {
                                publicInformUserIn(loginUser.getID());
                                for(ServerProcessing client : myProcess)
                                    if(client.loginUser != null && client != this)
                                        informUserIn(this, client.loginUser.getID());
                                
                            }
                            if(loginUser != null && loginUser.getType().equals("player")) {
                                stat = new PlayerStat(loginUser);
                                stat.setOnline(true);
                                stat.setFree(true);
                            }
                            sendData(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN_USER,loginUser));
                            break;
                        case ObjectWrapper.REGISTER_USER:
                            user = (User)data.getData();
                            ud = new UserDAO();
                            if(ud.addUser(user)){
                                sendData(new ObjectWrapper(ObjectWrapper.REPLY_REGISTER_USER,"ok"));
                            }
                            else
                                sendData(new ObjectWrapper(ObjectWrapper.REPLY_REGISTER_USER,"false")); 
                            break;
                        case ObjectWrapper.GET_LIST_PLAYER_RANK:
                            List<PlayerRank> rl = null;
                            if(stat != null)
                                rl = new PlayerRankDAO().getPlayerRanks(stat.getID());
                            else
                                rl = new PlayerRankDAO().getPlayerRanks();
                            sendData(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_PLAYER_RANK,rl));
                            if(loginUser != null){
                                for(ServerProcessing client : myProcess)
                                    if(client.loginUser != null)
                                        informUserIn(this, client.loginUser.getID());
                            }
                            break;
                        case ObjectWrapper.GET_LIST_FRIEND:
                            List<PlayerRank> frl = new PlayerRankDAO().getFriendRanks(stat.getID());
                            sendData(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_FRIEND,frl));
                            if(loginUser != null){
                                for(ServerProcessing client : myProcess)
                                    if(client.loginUser != null)
                                        informUserIn(this, client.loginUser.getID());
                            }
                            break;
                        case ObjectWrapper.CHALLENGE_PLAYER:
                            view.showMessage("challenge receive");
                            Challenge challenge = (Challenge)data.getData();
                            // insert to list challenge && send the ID to player
                            listChallenge.add(challenge);
                            Integer ID = listChallenge.size();
                            challenge.setID(ID);
                            myChallenge = challenge;
                            if(stat.isFree()){
                                //check toPlayer
                                int toPlayerID = challenge.getToPlayer().getID();
                                if(stat.getID() == toPlayerID){
                                    //cancel
                                    challenge.setExpired(true);
                                }
                                boolean playerOnline = false;
                                for(ServerProcessing player : myProcess){
                                    if(player.stat.getID() == toPlayerID ) {
                                        playerOnline = true;
                                        if(player.stat.isFree()){
                                            player.sendData(
                                                new ObjectWrapper(ObjectWrapper.SERVER_INFORM_CHALLENGE,challenge));
                                        }
                                        else{
                                            // to Player is in game , inform player challenge
                                            challenge.setExpired(true);
                                            sendData(new ObjectWrapper(ObjectWrapper.REPLY_CHALLENGE_PLAYER,"busy"));
                                        }
                                    }
                                }
                                // toPlayer is not online
                                if(!playerOnline){
                                    challenge.setExpired(true);
                                    sendData(new ObjectWrapper(ObjectWrapper.REPLY_CHALLENGE_PLAYER,"offline"));
                                }
                            }else{
                                // sender is in game 
                                challenge.setExpired(true);
                            }
                            break;
                        case ObjectWrapper.ANSWER_CHALLENGE:
                            challenge = (Challenge)data.getData();
                            if(challenge.isAccepted()){
                                for(Challenge c : listChallenge){
                                    if(challenge.getID() == c.getID()){
                                        if(!c.isExpired()){
                                            int fromPlayerID = c.getFromPlayer().getID();
                                            boolean playerOnline = false;
                                            for(ServerProcessing player : myProcess){
                                                playerOnline = true;
                                                if(player.stat.getID() == fromPlayerID){
                                                    if(player.stat.isFree() && stat.isFree()){
                                                        player.stat.setFree(false);
                                                        stat.setFree(false);
                                                        sendData(new ObjectWrapper(ObjectWrapper.SERVER_REPLY_ANSWER,"game start"));
                                                        player.sendData(new ObjectWrapper(ObjectWrapper.REPLY_CHALLENGE_PLAYER
                                                                ,(challenge.isAccepted() ? "accepted" : "refused")));
                                                        GamePlayer p1 = new GamePlayer();
                                                        GamePlayer p2 = new GamePlayer();
                                                        p1.setPlayer(stat);
                                                        p2.setPlayer(player.stat);
                                                        p1.setIs_next(true);
                                                        p2.setIs_next(false);
                                                        p1.setIs_winner(false);
                                                        p2.setIs_winner(false);
                                                        p1.setColor("blue");
                                                        p2.setColor("red");
                                                        Game g = new Game();
                                                        g.setBomb_number(31);
                                                        g.setWidth(8);
                                                        g.setChallenge(challenge);
                                                        g.setTime_begin(new Time(new java.util.Date().getTime()));
                                                        ArrayList<GamePlayer> players = new ArrayList<>();
                                                        players.add(p1);
                                                        players.add(p2);
                                                        g.setPlayers(players);
                                                        // set squares
                                                        
                                                        //save game
                                                        new GameDAO().addGame(g);
                                                        // add to game  list 
                                                        listGame.add(g);
                                                        
                                                        sendData(
                                                        new ObjectWrapper(ObjectWrapper.SERVER_UPDATE_GAME_STAT, g));
                                                        player.sendData(
                                                        new ObjectWrapper(ObjectWrapper.SERVER_UPDATE_GAME_STAT, g));   
                                                    }
                                                    if(!player.stat.isFree() && stat.isFree()){
                                                        //reply answer challenge
                                                        sendData(new ObjectWrapper(ObjectWrapper.SERVER_REPLY_ANSWER,"busy"));
                                                    }
                                                    if(player.stat.isFree() && !stat.isFree()){
                                                        //do smnt
                                                        player.sendData(new ObjectWrapper(ObjectWrapper.REPLY_CHALLENGE_PLAYER,"busy"));
                                                    }
                                                }
                                            }
                                            // player offline
                                            if(!playerOnline){
                                                sendData(new ObjectWrapper(ObjectWrapper.SERVER_REPLY_ANSWER,"offline"));
                                            }
                                            c.setExpired(true);
                                        }else{
                                            sendData(new ObjectWrapper(ObjectWrapper.SERVER_REPLY_ANSWER,"expired"));
                                        }
                                    }
                                }
                                
                            }else{
                                for(Challenge c : listChallenge){
                                    if(challenge.getID() == c.getID() && !challenge.isExpired()){
                                        int fromPlayerID = c.getFromPlayer().getID();
                                        for(ServerProcessing player : myProcess){
                                            if(player.stat.getID() == fromPlayerID){
                                                // send notification
                                                player.sendData(new ObjectWrapper(ObjectWrapper.REPLY_CHALLENGE_PLAYER,challenge));
                                                c.setExpired(true);
                                            }
                                        }
                                    }
                                }
                            }
                            break;
                        case ObjectWrapper.MAKE_A_MOVE:
                            // update game
                            if(data.getData() instanceof Square){
                                Square clicked = (Square)data.getData();
                                for(Game g : listGame){
                                    if(g.getID() == clicked.getGame().getID()){
                                        //update game g
                                        
                                        // send to players
                                        List<GamePlayer> players = g.getPlayers();
                                        int p2ID = (stat.getID()==players.get(0).getPlayer().getID() ? 
                                                players.get(1).getPlayer().getID() : players.get(0).getPlayer().getID());
                                        boolean p2Online = false;
                                        for(ServerProcessing player : myProcess){
                                            if(player.stat.getID() == p2ID){
                                                p2Online = true;
                                                // p2 update gamestat
                                                player.sendData(new ObjectWrapper(ObjectWrapper.SERVER_UPDATE_GAME_STAT, g));
                                            }
                                        }
                                        if(!p2Online){
                                            //game over
                                            for(GamePlayer player : players){
                                                if(player.getPlayer().getID() == p2ID){
                                                    player.setPlus_score(-1);
                                                }else{
                                                    player.setIs_winner(true);
                                                    player.setPlus_score(1);
                                                }
                                            }
                                            g.setTime_end(new Time(new java.util.Date().getTime()));
                                            new GameDAO().saveGame(g);
                                            // set players free
                                            stat.setFree(true);
                                        }
                                        sendData(new ObjectWrapper(ObjectWrapper.SERVER_UPDATE_GAME_STAT, g));
                                    }
                                }
                            }
                            break;
                        case ObjectWrapper.CANCEL_CHALLENGE:
                            if(myChallenge != null){
                                myChallenge.setExpired(true);
                                myChallenge = null;
                            }
                            break;
                        }
 
                    }
                    //ois.reset();
                    //oos.reset();
                }
            }catch (EOFException | SocketException e) {             
                //e.printStackTrace();
                myProcess.remove(this);
                view.showMessage("Number of client connecting to the server: " + myProcess.size());
                if(loginUser != null){
                    publicInformUserOut(loginUser.getID());
                }
                try {
                    mySocket.close();
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
                this.stop();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}