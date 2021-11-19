package tcp.server.control;
 
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import model.Challenge;
import model.Game;
import model.GamePlayer;
import model.IPAddress;
import model.ObjectWrapper;
import model.PlayerStat;
import model.Square;
import model.TournamentPlayer;
import model.User;
import tcp.server.view.ServerMainFrm;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.FriendRequest;
import model.Invitation;
import model.PlayerRank;
import model.Round;
import model.RoundResult;
import model.Tournament;
import rmi.general.FriendRequestInterface;
import rmi.general.GameInterface;
import rmi.general.InvitationInterface;
import rmi.general.PlayerRankInterface;
import rmi.general.TournamentPlayerInterface;
import rmi.general.UserInterface;
 
public class ServerCtr {
    private ServerMainFrm view;
    private ServerSocket myServer;
    private ServerListening myListening;
    private ArrayList<ServerProcessing> myProcess;
    private ArrayList<Challenge> listChallenge;
    private ArrayList<GameProcess> listGameProcess;
    private IPAddress myAddress = new IPAddress("localhost",8888);  //default server host and port
    private IPAddress serverAddress = new IPAddress("localhost", 9999); //default server address
    private String rmiService = "rmiServer";  
    private UserInterface userRO;
    private InvitationInterface invRO;
    private PlayerRankInterface playerRankRO;
    private TournamentPlayerInterface trmPlayerRO;
    private FriendRequestInterface friendRequestRO;
    private GameInterface gameRO;
    
    public ServerCtr(ServerMainFrm view){
        myProcess = new ArrayList<ServerProcessing>();
        listChallenge = new ArrayList<Challenge>();
        listGameProcess = new ArrayList<GameProcess>();
        this.view = view;
    }
     
    public ServerCtr(ServerMainFrm view, int serverPort){
        myProcess = new ArrayList<ServerProcessing>();
        this.view = view;
        myAddress.setPort(serverPort);
    }
    
    public void setRMIService(int rmiService) {
        this.rmiService = this.rmiService;
    }
    
    public void setRMIServerAddress(IPAddress serverAddr) {
        this.serverAddress = serverAddr;
        
    }
    
    
    public void openServer(){
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
    
    public boolean init(){
        try{
            // get the registry
            Registry registry = LocateRegistry.getRegistry(serverAddress.getHost(), serverAddress.getPort());
            // lookup the remote objects
            userRO = (UserInterface)(registry.lookup(rmiService));
             invRO = (InvitationInterface)(registry.lookup(rmiService));
             playerRankRO = (PlayerRankInterface)(registry.lookup(rmiService));
             trmPlayerRO = (TournamentPlayerInterface)(registry.lookup(rmiService));
             friendRequestRO = (FriendRequestInterface)(registry.lookup(rmiService));
             gameRO = (GameInterface)(registry.lookup(rmiService));
            view.setServerHost(serverAddress.getHost(), serverAddress.getPort()+"", rmiService);
            view.showMessage("Found the remote objects at the host: " + serverAddress.getHost() + ", port: " + serverAddress.getPort());
        }catch(Exception e){
            e.printStackTrace();
            view.showMessage("Error to lookup the remote objects!");
            return false;
        }
        return true;
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
                        //sendUDPData(data);
                        switch(data.getPerformative()) {
                        case ObjectWrapper.LOGIN_USER:
//                            //data = receiveUDPData();
                            if(data.getData() instanceof User) {
                                loginUser = remoteCheckLogin((User)data.getData());
                            }
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
//                            //data = receiveUDPData();
                            String result= null;
                            if(data.getData() instanceof User) {
                                result = remoteAddUser((User)data.getData());
                            }
                            data = new ObjectWrapper(ObjectWrapper.REPLY_REGISTER_USER, result);
                            sendData(data);
                            break;
                        case ObjectWrapper.GET_LIST_PLAYER_RANK:
//                            //data = receiveUDPData();
                            List<PlayerRank> lr = remoteGetPlayerRank(loginUser.getID());
                            data = new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_PLAYER_RANK, lr);
                            sendData(data);
                            if(loginUser != null){
                                for(ServerProcessing client : myProcess)
                                    if(client.loginUser != null)
                                        informUserIn(this, client.loginUser.getID());
                            }
                            break;
//                        case ObjectWrapper.GET_LIST_FRIEND:
//                            List<PlayerRank> frl = new PlayerRankDAO().getFriendRanks(stat.getID());
//                            sendData(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_FRIEND,frl));
//                            if(loginUser != null){
//                                for(ServerProcessing client : myProcess)
//                                    if(client.loginUser != null)
//                                        informUserIn(this, client.loginUser.getID());
//                            }
//                            break;
                        case ObjectWrapper.CHALLENGE_PLAYER:
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
                                    if(player.stat != null && player.stat.getID() == toPlayerID ) {
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
                                                if(player.stat != null && player.stat.getID() == fromPlayerID){
                                                    playerOnline = true;
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
                                                        p1.setWinner(false);
                                                        p2.setWinner(false);
                                                        p1.setColor("#FF7878");
                                                        p2.setColor("#CAB8FF");
                                                        Game g = new Game();
                                                        g.setBomb_number(3);
                                                        g.setWidth(10);
                                                        g.setChallenge(challenge);
                                                        g.setTime_begin(new Timestamp(System.currentTimeMillis()));
                                                        ArrayList<GamePlayer> players = new ArrayList<>();
                                                        players.add(p1);
                                                        players.add(p2);
                                                        g.setPlayers(players);
                                                            //round
                                                        List<Round> rounds = new ArrayList<Round>();
                                                        Round r1 = new Round();
                                                        Round r2 = new Round();
                                                        rounds.add(r1);
                                                        rounds.add(r2);
                                                        g.setRounds(rounds);
                                                        //p1
                                                        RoundResult p1r1 = new RoundResult();
                                                        p1r1.setGamePlayer(p1);
                                                        p1r1.setNext(true);
                                                        RoundResult p1r2 = new RoundResult();
                                                        p1r2.setGamePlayer(p1);
                                                        p1r2.setNext(false);
                                                        
                                                        //p2
                                                        RoundResult p2r1 = new RoundResult();
                                                        p2r1.setGamePlayer(p2);
                                                        p2r1.setNext(false);
                                                        RoundResult p2r2 = new RoundResult();
                                                        p2r2.setGamePlayer(p2);
                                                        p2r2.setNext(true);
                                                        
                                                        List<RoundResult> results1 = new ArrayList<RoundResult>();
                                                        List<RoundResult> results2 = new ArrayList<RoundResult>();
                                                        results1.add(p1r1);
                                                        results1.add(p2r1);
                                                        results2.add(p1r2);
                                                        results2.add(p2r2);
                                                        r1.setResults(results1);
                                                        r2.setResults(results2);
                                                        //add game
                                                        
                                                        g = remoteAddGame(g);
                                                        // add to game  list 
                                                        if(g.getID() > 0)
                                                            listGameProcess.add(new GameProcess(g));  
                                                        else {
                                                                // tb failure in add game
                                                        }
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
                                            if(player.stat != null && player.stat.getID() == fromPlayerID){
                                                // send notification
                                                player.sendData(new ObjectWrapper(ObjectWrapper.REPLY_CHALLENGE_PLAYER
                                                                ,(challenge.isAccepted() ? "accepted" : "refused")));
                                                c.setExpired(true);
                                            }
                                    }
                            }
                            break;
                        case ObjectWrapper.START_TRM_GAME:
                            PlayerStat op = null;
                            Game g = (Game)data.getData();
                            GamePlayer p1 = g.getPlayers().get(0);
                            GamePlayer p2 = g.getPlayers().get(1);
                            for(ServerProcessing client : myProcess) 
                                if(client.stat != null && client.stat.getID() == p1.getPlayer().getID()) {
                                   op = client.stat; 
                                   break;
                                }
                            if(op != null) {
                                if( op.isFree() && stat.isFree() ) {
                                    stat.setFree(false);
                                    op.setFree(false);
                                    p1.setWinner(false);
                                    p2.setWinner(false);
                                    p1.setColor("#FF7878");
                                    p2.setColor("#CAB8FF");
                                    g.setBomb_number(3);
                                    g.setWidth(10);
                                    g.setTime_begin(new Timestamp(System.currentTimeMillis()));
                                    //round
                                    List<Round> rounds = new ArrayList<Round>();
                                    Round r1 = new Round();
                                    Round r2 = new Round();
                                    rounds.add(r1);
                                    rounds.add(r2);
                                    g.setRounds(rounds);
                                    //p1
                                    RoundResult p1r1 = new RoundResult();
                                    p1r1.setGamePlayer(p1);
                                    p1r1.setNext(true);
                                    RoundResult p1r2 = new RoundResult();
                                    p1r2.setGamePlayer(p1);
                                    p1r2.setNext(false);

                                    //p2
                                    RoundResult p2r1 = new RoundResult();
                                    p2r1.setGamePlayer(p2);
                                    p2r1.setNext(false);
                                    RoundResult p2r2 = new RoundResult();
                                    p2r2.setGamePlayer(p2);
                                    p2r2.setNext(true);

                                    List<RoundResult> results1 = new ArrayList<RoundResult>();
                                    List<RoundResult> results2 = new ArrayList<RoundResult>();
                                    results1.add(p1r1);
                                    results1.add(p2r1);
                                    results2.add(p1r2);
                                    results2.add(p2r2);
                                    r1.setResults(results1);
                                    r2.setResults(results2);
                                    //add game

                                    g = remoteAddGame(g);
                                    // add to game  list 
                                    if(g.getID() > 0)
                                        listGameProcess.add(new GameProcess(g));  
                                    else {
                                            // tb failure in add game
                                    }
                                } else{
                                    //inform busy
                                    sendData (new ObjectWrapper(ObjectWrapper.REPLY_START_TRM_GAME, "busy"));
                                }
                            } else {
                                //inform off line
                                sendData (new ObjectWrapper(ObjectWrapper.REPLY_START_TRM_GAME, "offline"));
                            }
                            break;
                        case ObjectWrapper.MAKE_A_MOVE:
                            if(data.getData() instanceof Square){
                                Square clicked = (Square)data.getData();
                                for(GameProcess process : listGameProcess){
                                    if(process.getMyGame().getID() == clicked.getRound().getGame().getID()){
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
//                            //data = receiveUDPData();
                            List<TournamentPlayer> ltp = remoteGetTournamentPlayersOfPlayer(loginUser.getID());
                            data = new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_PLAYER_TOURNAMENT, ltp);
                            sendData(data);
                            break;
                        case ObjectWrapper.SEND_FRIEND_REQUEST:
//                            //data = receiveUDPData();
                            if(data.getData() instanceof FriendRequest){
                                FriendRequest fr = remoteAddFriendRequest((FriendRequest)data.getData());
                                for(ServerProcessing client : myProcess) 
                                    if(client.stat != null && client.stat.getID() == fr.getToPlayer().getID())
                                        client.sendData(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_FRIEND_REQUEST,fr));
                                data = new ObjectWrapper(ObjectWrapper.REPLY_FRIEND_REQUEST, fr.getID()>0 ? "ok" : "fail");
                                sendData(data);
                            }
                            break;
                        case ObjectWrapper.GET_LIST_FRIEND_REQUEST:
//                            //data = receiveUDPData();
                            List<FriendRequest> lfr = remoteGetPlayerFriendRequest(loginUser.getID());
                            data = new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_FRIEND_REQUEST,lfr);
                            sendData(data);
                            break;
                        case ObjectWrapper.ANSWER_FRIEND_REQUEST:
                            if(data.getData() instanceof FriendRequest) {
                                FriendRequest fr = (FriendRequest)data.getData();
                                result = remoteSaveFriendRequest(fr);
                                data = new ObjectWrapper(ObjectWrapper.REPLY_ANSWER_FRIEND_REQUEST, result);
                                sendData(data);
                                if(result.equals("ok")) {
                                for(ServerProcessing client : myProcess) 
                                    if(client.stat != null && client.stat.getID() == fr.getFromPlayer().getID())
                                        client.sendData(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_ANSWER_FRIEND_REQUEST,fr));
                                }
                            }
                            break;
                        case ObjectWrapper.UNFRIEND:
                            if(data.getData() instanceof FriendRequest) {
                                FriendRequest fr = (FriendRequest) data.getData();
                                result = remoteDeleteFriendRequest(fr);
                                data = new ObjectWrapper(ObjectWrapper.REPLY_UNFRIEND, result);
                                sendData(data);
                                if(result.equals("ok")) {
                                    for(ServerProcessing client : myProcess) 
                                        if(client.stat != null && client.stat.getID() == fr.getToPlayer().getID())
                                            client.sendData(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_UNFRIEND,fr));
                                }
                            }
                            break;
                        case ObjectWrapper.SEND_INVITATION:
                            //data = receiveUDPData();
                            if(data.getData() instanceof Invitation) {
                                Invitation inv = remoteAddInvitation((Invitation) data.getData());
                                data = new ObjectWrapper(ObjectWrapper.REPLY_SEND_INVITATION, inv.getID() > 0 ? "ok":"fail");
                                sendData(data);
                                if(inv.getID() > 0)
                                    for(ServerProcessing client : myProcess) 
                                        if(client.stat != null && client.stat.getID() == inv.getPlayer().getID())
                                        client.sendData(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_INVITATION,inv));
                            }
                            break;
                        case ObjectWrapper.GET_LIST_TOURNAMENT_PLAYER:
                            //data = receiveUDPData();
                            if(data.getData() instanceof Tournament) {
                                Tournament trm = (Tournament)data.getData();
                                ltp = remoteGetTournamentPlayers(trm.getID(),loginUser.getID());
                                data = new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_TOURNAMENT_PLAYER, ltp);
                                sendData(data);
                            }
                            break;
                        case ObjectWrapper.GET_LIST_INVITATION:
                            //data = receiveUDPData();
                            List<Invitation> li = remoteGetListInvitation(loginUser.getID());
                            data = new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_INVITATION, li);
                            sendData(data);
                            break;
                        case ObjectWrapper.ANSWER_INVITATION:
                            //data = receiveUDPData();
                            if(data.getData() instanceof Invitation) {
                                Invitation inv = (Invitation) data.getData();
                                result = remoteSaveInvitation(inv);
                                if(result.equals("ok") && inv.isAccepted()) {
                                    TournamentPlayer tp = new TournamentPlayer();
                                    tp.setDay_join(new Date());
                                    tp.setInvitation(inv);
                                    tp.setPlayer(inv.getPlayer());
                                    tp.setTournament(inv.getTournament());
                                    tp = remoteAddTournamentPlayer(tp);
                                    if(tp.getID() > 0) 
                                        for(ServerProcessing client : myProcess)
                                            client.sendData(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_NEW_TOURNAMENT_PLAYER,tp));
                                    else result = "fail";
                                }
                                data = new ObjectWrapper(ObjectWrapper.REPLY_ANSWER_INVITATION, result);
                                sendData(data);
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
        private int sqLeft;
        private int nextRound;
        public GameProcess(Game game){
            super();
            myGame = game;
            nextRound =1 ;
            initNewBoard();
            //dbh
            updateGameStat();
        }
        
        public void initNewBoard() {
            Round round = myGame.getRounds().get(nextRound-1);
            //generate game property
            int sqNumb = myGame.getWidth()*myGame.getWidth();
            sqLeft = sqNumb;
            ArrayList<Square> squares = new ArrayList<Square>();
            for(int i=0; i<sqNumb; i++){
                Square sq = new Square();
                sq.setID(i+1);
                sq.setBomb(false);
                sq.setClicked(false);
                squares.add(sq);
            }
            //bomb position
            for(int i=0; i<myGame.getBomb_number(); i++){
                int rand = (int)(Math.random()*sqNumb) ;
                if(squares.get(rand).isBomb()){
                    i--;
                    continue;
                }
                squares.get(rand).setBomb(true);
            }
            round.setSquares(squares);
            round.setTime_begin(new Timestamp(System.currentTimeMillis()));
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
                timer = null;
            }
            Round round = myGame.getRounds().get(nextRound-1);
            List<Square> squares = round.getSquares();
            List<RoundResult> results = round.getResults();
            RoundResult nextPlayer = null;
            // if square is cliked , return
            if(squares.get(squareID-1).isClicked()){
                processingStat.set(false);
                return;
            }
            // get the player clicking
            for(RoundResult p : results){
                if(p.isNext()){
                    nextPlayer = p;
                    break;
                }
            }
            //if is bomb , set to clicked , else cal
            if(squares.get(squareID-1).isBomb()){
                squares.get(squareID-1).setClicked(true);
                squares.get(squareID -1).setColor(nextPlayer.getGamePlayer().getColor());
                sqLeft -= 1;
                // increase the player score
                nextPlayer.setScore(nextPlayer.getScore() +1);
            } else {            
                calSquareValue(squareID,nextPlayer.getGamePlayer().getColor());
                //set turn 
                for(RoundResult p : results){
                    p.setNext(!p.isNext());
                }
            }
            // dong bo hoa
            updateGameStat();
            processingStat.set(false);
        }
        
        private void calSquareValue(int squareID, String color){
            Round round = myGame.getRounds().get(nextRound-1);
            List<Square> squares = round.getSquares();
            // if square is cliked , return
            if(squares.get(squareID-1).isClicked())
                return;
            squares.get(squareID-1).setColor(color);
            // set the init total = 0
            int value = 0;
            // set clicked
            squares.get(squareID -1).setClicked(true);
            sqLeft -= 1;
            //find out the position of the square
            boolean isLeftEdge = (squareID % myGame.getWidth() == 1);
            boolean isRightEdge = (squareID % myGame.getWidth() == 0);
            boolean isTopEdge = (squareID <= myGame.getWidth());
            boolean isBottomEdge = (squareID > myGame.getWidth()*(myGame.getWidth()-1));
            //check the square nearby to cal value
            if(!isLeftEdge){
                value += squares.get(squareID -1 -1).isBomb() ? 1 : 0;
            }
            if(!isRightEdge){
                value += squares.get(squareID -1 +1).isBomb() ? 1 : 0;
            }
            if(!isTopEdge) {
                value += squares.get(squareID -1 - myGame.getWidth()).isBomb() ? 1 : 0;
            }
            if(!isBottomEdge) {
                value += squares.get(squareID -1 + myGame.getWidth()).isBomb() ? 1 : 0;
            }
            if(!isTopEdge && !isLeftEdge){
                // goc trai tren
                value += squares.get(squareID -2 - myGame.getWidth()).isBomb() ? 1 : 0;
            }
            if(!isTopEdge && !isRightEdge){
                // goc phai tren
                value += squares.get(squareID - myGame.getWidth()).isBomb() ? 1 : 0;
            }
            if(!isBottomEdge && !isLeftEdge) {
                // goc trai duoi
                value += squares.get(squareID -2 + myGame.getWidth()).isBomb() ? 1 : 0;
            }
            if(!isBottomEdge && !isRightEdge) {
                //goc phai duoi
                value += squares.get(squareID + myGame.getWidth()).isBomb() ? 1 : 0;
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
                timer = null;
            }
            int winScore = (int)(myGame.getBomb_number()/2);
            Round round = myGame.getRounds().get(nextRound-1);
            List<RoundResult> results = round.getResults();
            List<GamePlayer> players = myGame.getPlayers();
            // check round over
            boolean roundOver = false;
            for(RoundResult p : results)
                if(p.getScore() > winScore){
                    roundOver = true;
                    p.setWinner(true);
                    round.setTime_end(new Timestamp(System.currentTimeMillis()));
                    // check game over 
                    if(myGame.getRounds().size() == nextRound) 
                        gameOver = true;
                    else {
                        nextRound++;
                        initNewBoard();
                    }
                    break;
                }
            if(gameOver) {
                myGame.setTime_end(new Timestamp(System.currentTimeMillis()));
                for(Round r : myGame.getRounds()) {
                    for(RoundResult result : r.getResults()) {
                        GamePlayer p = result.getGamePlayer();
                        p.setPlus_score(p.getPlus_score() + (result.isWinner() ? 1 : 0));
                    }
                }
                for(GamePlayer p : players)
                    if(p.getPlus_score() > (int)(nextRound/2)){
                        p.setWinner(true);
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
                String result = remoteSaveGame(myGame);
                if(result.equals("ok")) {
                    listGameProcess.remove(this);
                // remove this from list game process
                // cap nhat bxh
                    for(ServerProcessing socket : myProcess)
                        if(socket.getStat() != null){
//                            sendUDPData(new ObjectWrapper(ObjectWrapper.GET_LIST_PLAYER_RANK,socket.getStat()));
                            //data = receiveUDPData();
                            List<PlayerRank> lr = remoteGetPlayerRank(socket.getStat().getID());
                            ObjectWrapper data = new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_PLAYER_RANK,lr);
                            socket.sendData(data);
                        }
                    
                    //cap nhat online
                    for(ServerProcessing socket : myProcess)
                        if(socket.getStat() != null)
                            publicInformUserIn(socket.getStat().getID());
                    // cap nhat tournament
                    if(myGame.getTournament() != null) {
                        for(ServerProcessing socket : myProcess)
                            if(socket.getStat() != null && 
                                    (socket.getStat().getID() == myGame.getPlayers().get(0).getPlayer().getID()
                                    || socket.getStat().getID() == myGame.getPlayers().get(1).getPlayer().getID())) {
                                List<TournamentPlayer> ltp = remoteGetTournamentPlayers(myGame.getTournament().getID(), socket.getStat().getID());
                                socket.sendData(new ObjectWrapper(ObjectWrapper.REPLY_GET_LIST_TOURNAMENT_PLAYER, ltp));
                            }
                    }
                } else {
                    // inform failure in saving game
                }
                return;
            }
            if(roundOver) {
                try {
                    //wait 5 second
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            //set new timer
            if(timer == null) {
                timer = new Timer();
                timer.schedule(new AutomaticClick(), 10000);
            }
        }
        
        private void clickARandomSquare(){
            //generate a random square position
            Round round = myGame.getRounds().get(nextRound-1);
            List<Square> squares = round.getSquares();
            int id = (int)(Math.random()*(squares.size())) + 1;
            if(squares.get(id-1).isClicked()) {
                int ord = id % sqLeft;
                int pos = 0;
                for(Square sq : squares) {
                    if(!sq.isClicked()){
                        if(pos == ord) {
                            id = sq.getID();
                            break;
                        }
                        pos += 1;
                    }
                }
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
            Round round = myGame.getRounds().get(nextRound-1);
            for(RoundResult r : round.getResults())
                if(r.getGamePlayer().getPlayer().getID() == playerID && r.isNext())
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
    
    
    public User remoteCheckLogin(User user){
        try {
            return userRO.checkLogin(user);
        } catch (RemoteException ex) {
           ex.printStackTrace();
        }
        return null;
    }
     
    public String remoteAddUser(User user) {
        try {
            if(userRO.addUser(user))
                return "ok";
            else
                return "failed";
        } catch (RemoteException ex) {
           ex.printStackTrace();
        }
        return "failed";
    }
    
    public Invitation remoteAddInvitation(Invitation inv) {
        try {
            return invRO.addInvitation(inv);
        } catch(RemoteException ex){
            ex.printStackTrace();
        }
        return null;
    }
    
    public List<Invitation> remoteGetListInvitation(int playerID) {
        try {
            return invRO.getListInvitation(playerID);
        } catch (RemoteException ex) {
           ex.printStackTrace();
        }
        return new ArrayList<Invitation>();
    }
    
    public String remoteSaveInvitation(Invitation inv) {
        try {
            if(invRO.saveInvitation(inv)){
                return "ok";
            } else
                return "fail";
        } catch (RemoteException ex) {
           ex.printStackTrace();
        }
        return "fail";
    }
    
    public List<PlayerRank> remoteGetPlayerRank(int playerID) {
        try {
            return playerRankRO.getPlayerRank(playerID);
        } catch (RemoteException ex) {
           ex.printStackTrace();
        }
        return new ArrayList<PlayerRank>();
    }
    
    public TournamentPlayer remoteGetTournamentPlayer(int playerID) {
        try {
            return trmPlayerRO.getTournamentPlayer(playerID);
        } catch (RemoteException ex) {
           ex.printStackTrace();
        }
        return null;
    }
    
    public TournamentPlayer remoteAddTournamentPlayer(TournamentPlayer tp) {
        try {
            return trmPlayerRO.addTournamentPlayer(tp);
        } catch (RemoteException ex) {
           ex.printStackTrace();
        }
        return null;
    }
    
    public List<TournamentPlayer> remoteGetTournamentPlayers(int trmID,int playerID) {
        try {
            return trmPlayerRO.getTournamentPlayers(trmID,playerID);
        } catch (RemoteException ex) {
           ex.printStackTrace();
        }
        return new ArrayList<TournamentPlayer>();
    }
    
    public List<TournamentPlayer> remoteGetTournamentPlayersOfPlayer(int playerID) {
        try {
            return trmPlayerRO.getTournamentPlayersOfPlayer(playerID);
        } catch (RemoteException ex) {
           ex.printStackTrace();
        }
        return new ArrayList<>();
    }
    
    public FriendRequest remoteAddFriendRequest(FriendRequest fr) {
        try {
            return friendRequestRO.addFriendRequest(fr);
        } catch (RemoteException ex) {
           ex.printStackTrace();
        }
        return null;
    }
    
    public String remoteSaveFriendRequest(FriendRequest fr) {
        try {
            return (friendRequestRO.saveFriendRequest(fr) ? "ok" : "fail");
        } catch (RemoteException ex) {
           ex.printStackTrace();
        }
        return "fail";
    }
    
    public String remoteDeleteFriendRequest(FriendRequest fr) {
        try {
            return (friendRequestRO.deleteFriendRequest(fr) ? "ok" : "fail");
        } catch (RemoteException ex) {
           ex.printStackTrace();
        }
        return "fail";
    }
    
    public List<FriendRequest> remoteGetPlayerFriendRequest(int playerID) {
        try {
            return friendRequestRO.getPlayerFriendRequest(playerID);
        } catch (RemoteException ex) {
           ex.printStackTrace();
        }
        return new ArrayList<>();
    }
    
    public Game remoteAddGame(Game g) {
        try {
            return gameRO.addGame(g);
        } catch (RemoteException ex) {
           ex.printStackTrace();
        }
        return null;
    }
    
    public String remoteSaveGame(Game g) {
        try {
            return gameRO.saveGame(g)? "ok" : "fail";
        } catch (RemoteException ex) {
           ex.printStackTrace();
        }
        return "fail";
    }
    
}