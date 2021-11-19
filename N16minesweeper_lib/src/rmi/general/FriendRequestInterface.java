/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.general;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.FriendRequest;

/**
 *
 * @author Administrator
 */
public interface FriendRequestInterface extends Remote{
    public FriendRequest addFriendRequest(FriendRequest fr) throws RemoteException;
    public boolean saveFriendRequest(FriendRequest fr) throws RemoteException;
    public boolean deleteFriendRequest (FriendRequest fr) throws RemoteException;
    public List<FriendRequest> getPlayerFriendRequest(int playerID) throws RemoteException;
}
