package rmi.server.control;
 
 
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import jdbc.dao.FriendRequestDAO;
import jdbc.dao.GameDAO;
import jdbc.dao.InvitationDAO;
import jdbc.dao.PlayerRankDAO;
import jdbc.dao.TournamentPlayerDAO;
 
import jdbc.dao.UserDAO;
import model.FriendRequest;
import model.Game;
import model.User;
import model.IPAddress;
import model.Invitation;
import model.PlayerRank;
import model.TournamentPlayer;
import model.User;
import rmi.general.InvitationInterface;
import rmi.general.PlayerRankInterface;
import rmi.general.TournamentPlayerInterface;
import rmi.general.UserInterface;
import rmi.general.FriendRequestInterface;
import rmi.general.GameInterface;
import rmi.server.view.ServerMainFrm;
 
 
public class ServerCtr extends UnicastRemoteObject implements UserInterface, InvitationInterface,PlayerRankInterface
        ,TournamentPlayerInterface,FriendRequestInterface, GameInterface{
    private IPAddress myAddress = new IPAddress("localhost", 9999);     // default server host/port
    private Registry registry;
    private ServerMainFrm view;
    private String rmiService = "rmiServer";    // default rmi service key
     
    public ServerCtr(ServerMainFrm view) throws RemoteException{
        this.view = view;   
    }
     
    public ServerCtr(ServerMainFrm view, int port, String service) throws RemoteException{
        this.view = view;   
        myAddress.setPort(port);
        this.rmiService = service;
    }
     
    public void start() throws RemoteException{
        // registry this to the localhost
        try{
            try {
                //create new one
                registry = LocateRegistry.createRegistry(myAddress.getPort());
            }catch(ExportException e) {//the Registry exists, get it
                registry = LocateRegistry.getRegistry(myAddress.getPort());
            }
            registry.rebind(rmiService, this);
            myAddress.setHost(InetAddress.getLocalHost().getHostAddress());
            view.showServerInfo(myAddress, rmiService);
            view.showMessage("The RIM has registered the service key: " + rmiService + ", at the port: " + myAddress.getPort());
        }catch(RemoteException e){
            throw e;
        }catch(Exception e) {
            e.printStackTrace();
        } 
    }
     
    public void stop() throws RemoteException{
        // unbind the service
        try{
            if(registry != null) {
                registry.unbind(rmiService);
                UnicastRemoteObject.unexportObject(this,true);
            }
            view.showMessage("The RIM has unbinded the service key: " + rmiService + ", at the port: " + myAddress.getPort());
        }catch(RemoteException e){
            throw e;
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
     
     
    @Override
    public User checkLogin(User user){
        User u = (new UserDAO()).checkLogin(user);
        return u;
    }
    
    @Override
    public boolean addUser(User user){
        boolean ok = (new UserDAO()).addUser(user);
        return ok;
    }

    @Override
    public Invitation addInvitation(Invitation inv) throws RemoteException {
        boolean ok = (new InvitationDAO()).addInvitation(inv);
        return inv;
    }

    @Override
    public List<Invitation> getListInvitation(int playerID) throws RemoteException {
        List<Invitation> result = (new InvitationDAO()).getListInvitation(playerID);
        return result;
    }

    @Override
    public boolean saveInvitation(Invitation inv) throws RemoteException {
        boolean ok = (new InvitationDAO()).saveInvitation(inv);
        return ok;
    }

    @Override
    public List<PlayerRank> getPlayerRank(int playerID) throws RemoteException {
        List<PlayerRank> result = (new PlayerRankDAO()).getPlayerRanks(playerID);
        return result;
    }

    @Override
    public TournamentPlayer getTournamentPlayer(int playerID) throws RemoteException {
        TournamentPlayer result = (new TournamentPlayerDAO()).getTournamentPlayerByID(playerID);
        return result;
    }

    @Override
    public TournamentPlayer addTournamentPlayer(TournamentPlayer tp) throws RemoteException {
        boolean ok = (new TournamentPlayerDAO()).addTournamentPlayer(tp);
        return tp;
    }

    @Override
    public List<TournamentPlayer> getTournamentPlayers(int trmID,int playerID) throws RemoteException {
        List<TournamentPlayer> result = (new TournamentPlayerDAO()).getTournamentPlayers(trmID,playerID);
        return result;
    }

    @Override
    public List<TournamentPlayer> getTournamentPlayersOfPlayer(int playerID) throws RemoteException {
        List<TournamentPlayer> result = (new TournamentPlayerDAO()).getTournamentPlayersOfPlayer(playerID);
        return result;
    }

    @Override
    public FriendRequest addFriendRequest(FriendRequest fr) throws RemoteException {
        boolean ok = (new FriendRequestDAO()).addFriendRequest(fr);
        return fr;
    }

    @Override
    public boolean saveFriendRequest(FriendRequest fr) throws RemoteException {
        boolean ok = (new FriendRequestDAO()).saveFriendRequest(fr);
        return ok;
    }

    @Override
    public boolean deleteFriendRequest(FriendRequest fr) throws RemoteException {
        boolean ok = (new FriendRequestDAO()).deleteFriendRequest(fr);
        return ok;
    }

    @Override
    public List<FriendRequest> getPlayerFriendRequest(int playerID) throws RemoteException {
        List<FriendRequest> result = (new FriendRequestDAO()).getPlayerFriendRequest(playerID);
        return result;
    }

    @Override
    public Game addGame(Game game) throws RemoteException {
        boolean ok = (new GameDAO()).addGame(game);
        return game;
    }

    @Override
    public boolean saveGame(Game game) throws RemoteException {
        boolean ok = (new GameDAO()).saveGame(game);
        return ok;
    }
}