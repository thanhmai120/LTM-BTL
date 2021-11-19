/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.general;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Tournament;
import model.TournamentPlayer;

/**
 *
 * @author Administrator
 */
public interface TournamentPlayerInterface extends Remote{
    public TournamentPlayer getTournamentPlayer(int playerID) throws RemoteException;
    public TournamentPlayer addTournamentPlayer(TournamentPlayer tp) throws RemoteException;
    public List<TournamentPlayer> getTournamentPlayers(int trmID,int playerID) throws RemoteException;
    public List<TournamentPlayer> getTournamentPlayersOfPlayer(int playerID) throws RemoteException;
}
