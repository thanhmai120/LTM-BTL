/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "tblfriendrequest")
public class FriendRequest implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int ID;
    
    @Column(name = "time_request")
    private Timestamp time_request;
    
    @Column(name = "time_reply")
    private Timestamp time_reply;
    
    @Column(name = "accepted")
    private Boolean accepted;
    
    @ManyToOne
    @JoinColumn(name="fromplayeruserid", nullable=false)
    private Player fromPlayer;
    
    @ManyToOne
    @JoinColumn(name="toplayeruserid", nullable=false)
    private Player toPlayer;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Timestamp getTime_request() {
        return time_request;
    }

    public void setTime_request(Timestamp time_request) {
        this.time_request = time_request;
    }

    public Timestamp getTime_reply() {
        return time_reply;
    }

    public void setTime_reply(Timestamp time_reply) {
        this.time_reply = time_reply;
    }

    public Boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Player getFromPlayer() {
        return fromPlayer;
    }

    public void setFromPlayer(Player fromPlayer) {
        this.fromPlayer = fromPlayer;
    }

    public Player getToPlayer() {
        return toPlayer;
    }

    public void setToPlayer(Player toPlayer) {
        this.toPlayer = toPlayer;
    }
    
    
}
