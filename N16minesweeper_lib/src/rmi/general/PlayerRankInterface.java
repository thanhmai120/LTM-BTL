/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.general;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.PlayerRank;

/**
 *
 * @author Administrator
 */
public interface PlayerRankInterface extends Remote{
    public List<PlayerRank> getPlayerRank(int playerID) throws RemoteException;
    
}
