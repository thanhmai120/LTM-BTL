/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.general;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import model.Invitation;

/**
 *
 * @author Administrator
 */
public interface InvitationInterface extends Remote{
    public Invitation addInvitation(Invitation inv) throws RemoteException;
    public List<Invitation> getListInvitation(int playerID) throws RemoteException;
    public boolean saveInvitation(Invitation inv) throws RemoteException;
}
