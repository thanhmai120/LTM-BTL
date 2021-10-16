package tcp.server.control;
 
import java.awt.Color;
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
import java.util.Timer;
import java.util.TimerTask;
import jdbc.dao.GameDAO;
import jdbc.dao.PlayerRankDAO;
import jdbc.dao.TournamentPlayerDAO;
import jdbc.dao.UserDAO;
import model.Challenge;
import model.Game;
import model.GamePlayer;
import model.IPAddress;
import model.ObjectWrapper;
import model.PlayerRank;
import model.PlayerStat;
import model.Square;
import model.TournamentPlayer;
import model.User;
import tcp.server.view.ServerMainFrm;
import java.util.concurrent.atomic.AtomicBoolean;
 
public class ServerCtr {
    private ServerMainFrm view;
    private ServerSocket myServer;
    private ServerListening myListening;
    private ArrayList<ServerProcessing> myProcess;
    private ArrayList<Challenge> listChallenge;
    private ArrayList<GameProcess> listGameProcess;
    private IPAddress myAddress = new IPAddress("localhost",8888);  //default server host and port
     
    public ServerCtr(ServerMainFrm view){
        myProcess = new ArrayList<ServerProcessing>();
        listChallenge = new ArrayList<Challenge>();
        listGameProcess = new ArrayList<GameProcess>();
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
                    if(o instanceof ObjectWrapper){
                        ObjectWrapper data = (ObjectWrapper)o;
                        switch(data.getPerformative()) {
                        case ObjectWrapper.LOGIN_USER:
                            User user = (User)data.getData();
                            UserDAO ud = new UserDAO();
                            loginUser = ud.checkLogin(user);
                            for(ServerProcessing client : myProcess)
                                if(client.getStat() != null && loginUser != null
                                        && client.getStat().getID() == loginUser.getID())
                                    loginUser = null;
                            sendData(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN_USER,loginUser));
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
                                for(GameProcess gp : listGameProcess) 
                                    if(gp.hasGamePlayer(stat.getID())){
                                        stat.setFree(false);
                                        if(gp.isNextPlayer(stat.getID()))
                                            gp.updateGameStat();
                                        break;
//                                        sendData(new ObjectWrapper(ObjectWrapper.SERVER_UPDATE_GAME_STAT, gp.getMyGame()));
                                    }
                                
                            }
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
                            view.showMessage("Challenge come");
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
                                                view.showMessage("Challenge sent");
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
//                                                        sendData(new ObjectWrapper(ObjectWrapper.SERVER_REPLY_ANSWER,"game start"));
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
                                                        p1.setColor("#FF7878");
                                                        p2.setColor("#CAB8FF");
                                                        Game g = new Game();
                                                        g.setBomb_number(31);
                                                        g.setWidth(10);
                                                        g.setChallenge(challenge);
                                                        g.setTime_begin(new Time(new java.util.Date().getTime()));
                                                        ArrayList<GamePlayer> players = new ArrayList<>();
                                                        players.add(p1);
                                                        players.add(p2);
                                                        g.setPlayers(players);
                                                        //save game
                                                        new GameDAO().addGame(g);
                                                        // add to game  list 
                                                        listGameProcess.add(new GameProcess(g));  
                                                    }
                                                    else if(!player.stat.isFree() && stat.isFree()){
                                                        //reply answer challenge
                                                        sendData(new ObjectWrapper(ObjectWrapper.SERVER_REPLY_ANSWER,"busy"));
                                                    }
                                                    else if(player.stat.isFree() && !stat.isFree()){
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
                                for(Challenge c : listChallenge)
                                    if(challenge.getID() == c.getID() && !challenge.isExpired()){
                                        int fromPlayerID = c.getFromPlayer().getID();
                                        for(ServerProcessing player : myProcess)
                                            if(player.stat.getID() == fromPlayerID){
                                                // send notification
                                                player.sendData(new ObjectWrapper(ObjectWrapper.REPLY_CHALLENGE_PLAYER
                                                                ,(challenge.isAccepted() ? "accepted" : "refused")));
                                                c.setExpired(true);
                                            }
                                    }
                            }
                            break;
                        case ObjectWrapper.MAKE_A_MOVE:
                            if(data.getData() instanceof Square){
                                Square clicked = (Square)data.getData();
                                for(GameProcess process : listGameProcess){
                                    if(process.getMyGame().getID() == clicked.getGame().getID()){
                                        //update game
                                        process.onSquareClicked(clicked.getID());
                                        break;
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
                        case ObjectWrapper.GET_LIST_PLAYER_TOURNAMENT:
                            if(stat != null){
                                List<TournamentPlayer> tpl = (new TournamentPlayerDAO()).getTournamentPlayersOfPlayer(stat.getID());
                                sendData(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_PLAYER_TOURNAMENT, tpl));
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
                if(stat != null) 
                    if(!stat.isFree()) 
                        //update game of player
                        for(GameProcess gp : listGameProcess)
                            if(gp.isNextPlayer(stat.getID())){
                                gp.updateGameStat();
                                break;
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
    
    class GameProcess{
        private Game myGame;
        private Timer timer;
        private boolean gameOver = false;
        private AtomicBoolean  processingStat = new AtomicBoolean(false);
        public GameProcess(Game game){
            super();
            myGame = game;
            //generate game property
            int sqNumb = myGame.getWidth()*myGame.getWidth();
            ArrayList<Square> squares = new ArrayList<Square>();
            for(int i=0; i<sqNumb; i++){
                Square sq = new Square();
                sq.setID(i+1);
                sq.setIs_bomb(false);
                sq.setIs_clicked(false);
                squares.add(sq);
            }
            //bomb position
            for(int i=0; i<myGame.getBomb_number(); i++){
                int rand = (int)(Math.random()*sqNumb) ;
                if(squares.get(rand).isIs_bomb()){
                    i--;
                    continue;
                }
                squares.get(rand).setIs_bomb(true);
            }
            myGame.setSquares(squares);
            //dbh
            updateGameStat();
        }

        public Game getMyGame() {
            return myGame;
        }
        
        public void onSquareClicked(int squareID) {
            boolean isNotProcessing = processingStat.compareAndSet(false, true);
            if(!isNotProcessing) return;
            // cancel timer
            if(timer != null) {
                timer.cancel();
                timer.purge();
            }
            List<Square> squares = myGame.getSquares();
            List<GamePlayer> players = myGame.getPlayers();
            GamePlayer nextPlayer = null;
            // if square is cliked , return
            if(squares.get(squareID-1).isIs_clicked()){
                processingStat.set(false);
                return;
            }
            // get the player clicking
            for(GamePlayer p : players){
                if(p.isIs_next()){
                    nextPlayer = p;
                    break;
                }
            }
            //if is bomb , set to clicked , else cal
            if(squares.get(squareID-1).isIs_bomb()){
                squares.get(squareID-1).setIs_clicked(true);
                squares.get(squareID -1).setColor(nextPlayer.getColor());
                // increase the player score
                nextPlayer.setScore(nextPlayer.getScore() +1);
            } else {            
                calSquareValue(squareID,nextPlayer.getColor());
                //set turn 
                for(GamePlayer p : players){
                    p.setIs_next(!p.isIs_next());
                }
            }
            // dong bo hoa
            updateGameStat();
            processingStat.set(false);
        }
        
        private void calSquareValue(int squareID, String color){
            List<Square> squares = myGame.getSquares();
            // if square is cliked , return
            if(squares.get(squareID-1).isIs_clicked())
                return;
            squares.get(squareID-1).setColor(color);
            // set the init total = 0
            int value = 0;
            // set clicked
            squares.get(squareID -1).setIs_clicked(true);
            //find out the position of the square
            boolean isLeftEdge = (squareID % myGame.getWidth() == 1);
            boolean isRightEdge = (squareID % myGame.getWidth() == 0);
            boolean isTopEdge = (squareID <= myGame.getWidth());
            boolean isBottomEdge = (squareID > myGame.getWidth()*(myGame.getWidth()-1));
            //check the square nearby to cal value
            if(!isLeftEdge){
                value += squares.get(squareID -1 -1).isIs_bomb() ? 1 : 0;
            }
            if(!isRightEdge){
                value += squares.get(squareID -1 +1).isIs_bomb() ? 1 : 0;
            }
            if(!isTopEdge) {
                value += squares.get(squareID -1 - myGame.getWidth()).isIs_bomb() ? 1 : 0;
            }
            if(!isBottomEdge) {
                value += squares.get(squareID -1 + myGame.getWidth()).isIs_bomb() ? 1 : 0;
            }
            if(!isTopEdge && !isLeftEdge){
                // goc trai tren
                value += squares.get(squareID -2 - myGame.getWidth()).isIs_bomb() ? 1 : 0;
            }
            if(!isTopEdge && !isRightEdge){
                // goc phai tren
                value += squares.get(squareID - myGame.getWidth()).isIs_bomb() ? 1 : 0;
            }
            if(!isBottomEdge && !isLeftEdge) {
                // goc trai duoi
                value += squares.get(squareID -2 + myGame.getWidth()).isIs_bomb() ? 1 : 0;
            }
            if(!isBottomEdge && !isRightEdge) {
                //goc phai duoi
                value += squares.get(squareID + myGame.getWidth()).isIs_bomb() ? 1 : 0;
            }
            squares.get(squareID -1).setValue(value);
            //if the vlue is not 0, return, else cal nearby squares value
            if(value != 0){
                return;
            }
            if(!isLeftEdge){
                calSquareValue(squareID -1,color);
            }
            if(!isRightEdge){
                calSquareValue(squareID +1,color);
            }
            if(!isTopEdge) {
                calSquareValue(squareID - myGame.getWidth(),color);
            }
            if(!isBottomEdge) {
                calSquareValue(squareID + myGame.getWidth(),color);
            }
            if(!isTopEdge && !isLeftEdge){
                // goc trai tren
                calSquareValue(squareID -1 - myGame.getWidth(),color);
            }
            if(!isTopEdge && !isRightEdge){
                // goc phai tren
                calSquareValue(squareID - myGame.getWidth() + 1,color);
            }
            if(!isBottomEdge && !isLeftEdge) {
                // goc trai duoi
                calSquareValue(squareID -1 + myGame.getWidth(),color);
            }
            if(!isBottomEdge && !isRightEdge) {
                //goc phai duoi
                calSquareValue(squareID + myGame.getWidth() + 1,color);
            }
        }
        
        private void updateGameStat() {
            //if timer not cancel yet, cancel
            if(timer != null){
                timer.cancel();
                timer.purge();
            }
            List<GamePlayer> players = myGame.getPlayers();
            int winScore = (int)(myGame.getBomb_number()/2);
            // check game over
            for(GamePlayer p : players)
                if(p.getScore() > winScore){
                    p.setIs_winner(true);
                    p.setPlus_score(1);
                    myGame.setTime_end(new Time(new java.util.Date().getTime()));
                    gameOver = true;
                }
            if(gameOver) {
                for(GamePlayer p : players)
                    if(!p.isIs_winner()){
                        p.setPlus_score(-1);
                        break;
                    }
            }
            //send Stat to players 
            for(ServerProcessing socket : myProcess){
                if( socket.getStat()!=null && hasGamePlayer(socket.getStat().getID())){
                    if(gameOver)
                        socket.getStat().setFree(true);
                    socket.sendData(new ObjectWrapper(ObjectWrapper.SERVER_UPDATE_GAME_STAT, myGame));
                }
            }
            if(gameOver){
                //save game
                new GameDAO().saveGame(myGame);
                // remove this from list game process
                listGameProcess.remove(this);
                // cap nhat bxh
                for(ServerProcessing socket : myProcess){
                    List<PlayerRank> ranks = null;
                    if(socket.getStat() != null){
                        ranks = new PlayerRankDAO().getPlayerRanks(socket.getStat().getID());
                        List<PlayerRank> friends = new PlayerRankDAO().getFriendRanks(socket.getStat().getID());
                        socket.sendData(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_FRIEND, friends));
                    }
                    else
                        ranks = new PlayerRankDAO().getPlayerRanks();
                    socket.sendData(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_PLAYER_RANK, ranks));
                }
                return;
            }
            //set new timer
            timer = new Timer();
            timer.schedule(new AutomaticClick(), 10000);
        }
        
        private void clickARandomSquare(){
            //generate a random square position
            int id = (int)(Math.random()*(myGame.getSquares().size())) + 1;
            if(myGame.getSquares().get(id-1).isIs_clicked())
                for(Square sq : myGame.getSquares())
                    if(!sq.isIs_clicked()){
                        id = sq.getID();
                        break;
                    }
            onSquareClicked(id);
        }
        
        public boolean hasGamePlayer(int playerID){
            for(GamePlayer p : myGame.getPlayers())
                if(p.getPlayer().getID() == playerID)
                    return true;
            return false;
        }
        
        public boolean isNextPlayer(int playerID) {
            for(GamePlayer p : myGame.getPlayers())
                if(p.getPlayer().getID() == playerID && p.isIs_next())
                    return true;
            return false;
        }
        
        class AutomaticClick  extends TimerTask {
            @Override
            public void run() {
                clickARandomSquare();
            }
        }
    }
}