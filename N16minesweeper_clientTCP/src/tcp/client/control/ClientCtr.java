package tcp.client.control;
 
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.Challenge;
import model.FriendRequest;
import model.Game;
 
import model.IPAddress;
import model.ObjectWrapper;
import model.PlayerRank;
import model.User;
import tcp.client.view.ChallengeFrm;
import tcp.client.view.LoginFrm;
import tcp.client.view.ClientMainFrm;
import tcp.client.view.FriendRankFrm;
import tcp.client.view.GameFrm;
import tcp.client.view.PlayerRankFrm;
import tcp.client.view.PlayerDetailFrm;
import tcp.client.view.PlayerHomeFrm;
import tcp.client.view.PlayerTournamentFrm;
import tcp.client.view.RegisterFrm;
import tcp.client.view.TournamentFrm;
 
 
public class ClientCtr {
    private Socket mySocket;
    private ClientMainFrm view;
    private ClientListening myListening;                            // thread to listen the data from the server
    private ArrayList<ObjectWrapper> myFunction;                  // list of active client functions
    private IPAddress serverAddress = new IPAddress("localhost",8888);  // default server host and port
    private User user;
     
    public ClientCtr(ClientMainFrm view){
        super();
        this.view = view;
        myFunction = new ArrayList<ObjectWrapper>();  
    }
     
    public ClientCtr(ClientMainFrm view, IPAddress serverAddr) {
        super();
        this.view = view;
        this.serverAddress = serverAddr;
        myFunction = new ArrayList<ObjectWrapper>();
    }

    public ClientMainFrm getView() {
        return view;
    }

    public User getUser() {
        return user;
    }
 
    public boolean openConnection(){        
        try {
            mySocket = new Socket(serverAddress.getHost(), serverAddress.getPort());  
            myListening = new ClientListening();
            myListening.start();
            view.showMessage("Connected to the server at host: " + serverAddress.getHost() + ", port: " + serverAddress.getPort());
        } catch (Exception e) {
            //e.printStackTrace();
            view.showMessage("Error when connecting to the server!");
            return false;
        }
        return true;
    }
     
    public boolean sendData(Object obj){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(mySocket.getOutputStream());
            oos.writeObject(obj);           
             
        } catch (Exception e) {
            //e.printStackTrace();
            view.showMessage("Error when sending data to the server!");
            return false;
        }
        return true;
    }
     
    
    public Object receiveData(){
        Object result = null;
        try {
            ObjectInputStream ois = new ObjectInputStream(mySocket.getInputStream());
            result = ois.readObject();
        } catch (Exception e) {
            //e.printStackTrace();
            view.showMessage("Error when receiving data from the server!");
            return null;
        }
        return result;
    }
     
    public boolean closeConnection(){
         try {
             
            for(ObjectWrapper fto : myFunction)
                ((JFrame)fto.getData()).setVisible(false);
             if(myListening != null)
                 myListening.stop();
             if(mySocket !=null) {
                 mySocket.close();
                 view.showMessage("Disconnected from the server!");
             }
            myFunction.clear();             
         } catch (Exception e) {
             //e.printStackTrace();
             view.showMessage("Error when disconnecting from the server!");
             return false;
         }
         return true;
    }
     
     
     
    public ArrayList<ObjectWrapper> getActiveFunction() {
        return myFunction;
    }
    
    public void switchLoginState(User user){
        this.user = user;
        this.view.onLoginSuccessfully();
        this.view.showMessage("User Login: "+user.getUsername()+"\nHello "+user.getFullname());
        myFunction.clear();
        ChallengeFrm cv = new ChallengeFrm(this);
        PlayerHomeFrm hv = new PlayerHomeFrm(this);
    }
 
 
    class ClientListening extends Thread{
         
        public ClientListening() {
            super();
        }
         
        public void run() {
            try {
                while(true) {
                ObjectInputStream ois = new ObjectInputStream(mySocket.getInputStream());
                Object obj = ois.readObject();
                if(obj instanceof ObjectWrapper) {
                    
                    ObjectWrapper data = (ObjectWrapper)obj;
                    for(ObjectWrapper func: myFunction)
                        if(func.getData() instanceof PlayerHomeFrm) {
                            ((PlayerHomeFrm)func.getData()).receivedDataProcessing(data);
                            break;
                        }
                    if(data.getPerformative() == ObjectWrapper.SERVER_INFORM_CLIENT_NUMBER)
                        view.showMessage("Number of client connecting to the server: " + data.getData());
                    else if(data.getPerformative() == ObjectWrapper.SERVER_UPDATE_GAME_STAT) {
                        ObjectWrapper existed = null;
                        for(ObjectWrapper fto : myFunction)
                            if(fto.getPerformative() == ObjectWrapper.SERVER_UPDATE_GAME_STAT) {
                                existed = fto;
                                break;
                            }
                        if(existed == null){
                           GameFrm gv = new GameFrm(view.getMyControl());
                           Game g = (Game)data.getData();
                           int nextRound= g.getRounds().size();
                           for(int i=0; i<g.getRounds().size(); i++){
                                if(g.getRounds().get(i).getTime_end() == null) {
                                    nextRound = i+1;
                                    break;
                                }
                           }
                           gv.setNextRound(nextRound);
                           gv.setVisible(true);
                           gv.receivedDataProcessing(data);
                        } else {
                            GameFrm gv = (GameFrm)(existed.getData());
                            gv.setVisible(true);
                            gv.receivedDataProcessing(data);
                        }
                        
                    }
//                    else if(data.getPerformative() == ObjectWrapper.SERVER_INFORM_CHALLENGE){
////                        ObjectWrapper existed = null;
////                        for(ObjectWrapper fto: myFunction)
////                            if(fto.getPerformative() == ObjectWrapper.SERVER_INFORM_CHALLENGE) {
////                                existed = fto;
////                                ChallengeFrm cv = (ChallengeFrm)fto.getData();
////                                cv.receivedDataProcessing(data);
////                                cv.setVisible(true);
////                                break;
////                            }
////                        if(existed == null){
////                            ChallengeFrm cv = new ChallengeFrm(view.getMyControl());
////                            cv.receivedDataProcessing(data);
////                        }
//                        Challenge challenge = (Challenge)data.getData();
//                        String[] options = {"accept","refuse"};
//                        int result = JOptionPane.showOptionDialog(view
//                                , "Challenge from "+challenge.getFromPlayer().getUsername()
//                                , "New Challenge", JOptionPane.YES_NO_OPTION
//                                , JOptionPane.QUESTION_MESSAGE, null,options, null);
//                        if(result == JOptionPane.YES_OPTION) {
//                            challenge.setAccepted(true);
//                            sendData(new ObjectWrapper(ObjectWrapper.ANSWER_CHALLENGE, challenge));
//                        }else{
//                            challenge.setAccepted(false);
//                            sendData(new ObjectWrapper(ObjectWrapper.ANSWER_CHALLENGE, challenge));
//                        }
//                    }
//                    else if(data.getPerformative() == ObjectWrapper.SERVER_REPLY_ANSWER){
//                        if(data.getData() instanceof String) {
//                            if(data.getData().equals("expired")){
//                                JOptionPane.showMessageDialog(view, "Challenge is expired !");
//                            }
//                            if(data.getData().equals("busy")){
//                                JOptionPane.showMessageDialog(view, "Player challenging is busy !");
//                            }
//                            if(data.getData().equals("offline")){
//                                JOptionPane.showMessageDialog(view, "Player challenging is offline !");
//                            }
//                            if(data.getData().equals("game start")){
//                                // ksdjfiObjectWrapper existed = null;
//                                
//                                JOptionPane.showMessageDialog(view, "Game start !");
//                                new GameFrm(view.getMyControl()).setVisible(true);
//                            }
//                        }
//                    }
//                    else if(data.getPerformative() == ObjectWrapper.SERVER_INFORM_USER_IN){
//                        for(ObjectWrapper fto: myFunction){
//                            if(fto.getPerformative() == ObjectWrapper.REPLY_GET_LIST_FRIEND || 
//                                    fto.getPerformative() == ObjectWrapper.REPLY_GET_LIST_PLAYER_RANK){
//                                if(fto.getData() instanceof FriendRankFrm){
//                                    ((FriendRankFrm)fto.getData()).informPlayerOnline(data);
//                                }
//                                if(fto.getData() instanceof PlayerRankFrm){
//                                    ((PlayerRankFrm)fto.getData()).informPlayerOnline(data);
//                                }
//                            }
//                        }
//                    }
//                    else if(data.getPerformative() == ObjectWrapper.SERVER_INFORM_USER_OUT){
//                        for(ObjectWrapper fto: myFunction){
//                            if(fto.getPerformative() == ObjectWrapper.REPLY_GET_LIST_FRIEND || 
//                                    fto.getPerformative() == ObjectWrapper.REPLY_GET_LIST_PLAYER_RANK){
//                                if(fto.getData() instanceof FriendRankFrm){
//                                    ((FriendRankFrm)fto.getData()).informPlayerOffline(data);
//                                }
//                                if(fto.getData() instanceof PlayerRankFrm){
//                                    ((PlayerRankFrm)fto.getData()).informPlayerOffline(data);
//                                }
//                            }
//                        }
//                    }
                    else if(data.getPerformative() == ObjectWrapper.SERVER_INFORM_USER_IN){
                        for(ObjectWrapper fto: myFunction){
                            if(fto.getData() instanceof GameFrm){
                                ((GameFrm)fto.getData()).informPlayerIn(data);
                                break;
                            }
                        }
                    }
                    else if(data.getPerformative() == ObjectWrapper.SERVER_INFORM_USER_OUT){
                        for(ObjectWrapper fto: myFunction){
                            if(fto.getData() instanceof GameFrm){
                                ((GameFrm)fto.getData()).informPlayerOut(data);
                                break;
                            }
                        }
                    }
                    else if(data.getPerformative() == ObjectWrapper.SERVER_INFORM_NEW_TOURNAMENT_PLAYER){
                        for(ObjectWrapper fto: myFunction){
                            if(fto.getData() instanceof TournamentFrm){
                                ((TournamentFrm)fto.getData()).receivedDataProcessing(data);
                                break;
                            }
                        }
                    }
                    else {
                        for(ObjectWrapper fto: myFunction)
                            if(fto.getPerformative() == data.getPerformative()) {
                                switch(data.getPerformative()) {
                                case ObjectWrapper.REPLY_LOGIN_USER:
                                    LoginFrm loginView = (LoginFrm)fto.getData();
                                    loginView.receivedDataProcessing(data); 
                                    break;
                                case ObjectWrapper.REPLY_REGISTER_USER:
                                    RegisterFrm rv = (RegisterFrm)fto.getData();
                                    rv.receivedDataProcessing(data);
                                    break;
//                                case ObjectWrapper.REPLY_GET_LIST_PLAYER_RANK:
//                                    PlayerRankFrm prv = (PlayerRankFrm)fto.getData();
//                                    prv.receivedDataProcessing(data);
//                                    break;
                                case ObjectWrapper.REPLY_CHALLENGE_PLAYER:
                                    PlayerDetailFrm pdv = (PlayerDetailFrm)fto.getData();
                                    pdv.receivedDataProcessing(data);
                                    break;
//                                case ObjectWrapper.REPLY_GET_LIST_FRIEND:
//                                    FriendRankFrm frv = (FriendRankFrm)fto.getData();
//                                    frv.receivedDataProcessing(data);
//                                    break;
//                                case ObjectWrapper.REPLY_GET_LIST_PLAYER_TOURNAMENT:
//                                    PlayerTournamentFrm ptv = (PlayerTournamentFrm)fto.getData();
//                                    ptv.receivedDataProcessing(data);
//                                    break;
//                                case ObjectWrapper.SERVER_INFORM_CHALLENGE:
//                                    ChallengeFrm cv = (ChallengeFrm)fto.getData();
//                                    cv.receivedDataProcessing(data);
//                                    break;
                                case ObjectWrapper.REPLY_FRIEND_REQUEST:
                                    pdv = (PlayerDetailFrm)fto.getData();
                                    pdv.receivedDataProcessing(data);
                                    break;
                                case ObjectWrapper.REPLY_UNFRIEND:
                                    pdv = (PlayerDetailFrm)fto.getData();
                                    pdv.receivedDataProcessing(data);
                                    break;
                                case ObjectWrapper.REPLY_GET_LIST_TOURNAMENT_PLAYER:
                                    TournamentFrm tv = (TournamentFrm) fto.getData();
                                    tv.receivedDataProcessing(data);
                                    break;
                                case ObjectWrapper.REPLY_SEND_INVITATION:
                                    tv = (TournamentFrm) fto.getData();
                                    tv.receivedDataProcessing(data);
                                    break;
                                case ObjectWrapper.SERVER_INFORM_NEW_TOURNAMENT_PLAYER:
                                    tv = (TournamentFrm) fto.getData();
                                    tv.receivedDataProcessing(data);
                                    break;
                                case ObjectWrapper.REPLY_START_TRM_GAME:
                                    tv = (TournamentFrm) fto.getData();
                                    tv.receivedDataProcessing(data);
                                    break;
                                }
                                break;
                            }
//                        //view.showMessage("Received an object: " + data.getPerformative());
                    }
                }
                }
            } catch (Exception e) {
                e.printStackTrace();
                view.showMessage("Error when receiving data from the server!");
                view.resetClient();
            }
        }
    }
}