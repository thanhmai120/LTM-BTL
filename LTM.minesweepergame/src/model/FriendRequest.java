/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.sql.Time;
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
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "tblfriendrequest")
public class FriendRequest implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int ID;
    
    @Column(name = "time_request")
    private Time time_request;
    
    @Column(name = "time_reply")
    private Time time_reply;
    
    @Column(name = "accepted")
    private boolean accepted;
    
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

    public Time getTime_request() {
        return time_request;
    }

    public void setTime_request(Time time_request) {
        this.time_request = time_request;
    }

    public Time getTime_reply() {
        return time_reply;
    }

    public void setTime_reply(Time time_reply) {
        this.time_reply = time_reply;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
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
