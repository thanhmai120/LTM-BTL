/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "tblplayer")
public class Player extends User implements Serializable{
    
    @OneToMany(mappedBy="fromPlayer")
    private List<Challenge> sendChallenges;
    
    @OneToMany(mappedBy="toPlayer")
    private List<Challenge> receiveChallenges;
    
    @OneToMany(mappedBy="player")
    private List<GamePlayer> gamePlayers;
    
    @OneToMany(mappedBy="player")
    private List<TournamentPlayer> trmPlayers;
    
    @OneToMany(mappedBy="player")
    private List<Invitation> invitations;
    
    @OneToMany(mappedBy="fromPlayer")
    private List<FriendRequest> sendFriendRequests;
    
    @OneToMany(mappedBy="toPlayer")
    private List<FriendRequest> receiveFriendRequests;

    public Player() {
    }

    public Player(User user) {
        this.setUsername(user.getUsername());
        this.setEmail(user.getEmail());
        this.setFullname(user.getFullname());
        this.setID(user.getID());
        this.setPassword(user.getPassword());
        this.setActive(user.isActive());
        this.setType("player");
    }
    
    

    public List<Challenge> getSendChallenges() {
        return sendChallenges;
    }

    public void setSendChallenges(List<Challenge> sendChallenges) {
        this.sendChallenges = sendChallenges;
    }

    public List<Challenge> getReceiveChallenges() {
        return receiveChallenges;
    }

    public void setReceiveChallenges(List<Challenge> receiveChallenges) {
        this.receiveChallenges = receiveChallenges;
    }

    public List<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(List<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }

    public List<TournamentPlayer> getTrmPlayers() {
        return trmPlayers;
    }

    public void setTrmPlayers(List<TournamentPlayer> trmPlayers) {
        this.trmPlayers = trmPlayers;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<Invitation> invitations) {
        this.invitations = invitations;
    }
    
}
