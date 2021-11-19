/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.general;

import java.rmi.Remote;
import java.rmi.RemoteException;
import model.Game;

/**
 *
 * @author Administrator
 */
public interface GameInterface extends Remote{
    public Game addGame(Game game) throws RemoteException;
    public boolean saveGame(Game game) throws RemoteException;
    
}
