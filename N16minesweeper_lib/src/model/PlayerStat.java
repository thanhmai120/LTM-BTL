/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class PlayerStat extends Player implements Serializable{
    public static final int REQUESTING = 1;
    public static final int REQUESTED = 2;
    public static final int BE_FRIEND =3;
    private boolean online;
    private boolean free;
    private int friend_stat;

    public PlayerStat() {
    }
    
    public PlayerStat(User user) {
        this.setUsername(user.getUsername());
        this.setEmail(user.getEmail());
        this.setFullname(user.getFullname());
        this.setID(user.getID());
        this.setPassword(user.getPassword());
        this.setActive(user.isActive());
        this.setType("player");
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public int getFriend_stat() {
        return friend_stat;
    }

    public void setFriend_stat(int friend_stat) {
        this.friend_stat = friend_stat;
    }

    
    
}
