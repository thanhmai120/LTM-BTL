/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.general;

import java.rmi.Remote;
import java.rmi.RemoteException;
import model.User;

/**
 *
 * @author Administrator
 */
public interface UserInterface extends Remote{
    public User checkLogin(User user) throws RemoteException;
    public boolean addUser(User user) throws RemoteException;
}
