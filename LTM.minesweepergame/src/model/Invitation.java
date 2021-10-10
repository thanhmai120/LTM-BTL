/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "tblinvitation")
public class Invitation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int ID;
    
    @Column(name = "time")
    private Time time;
    
    @Column(name = "accepted")
    private Boolean accepted;
    
    @Column(name = "expired")
    private Date expired;
    
    @ManyToOne
    @JoinColumn(name="organizationuserid", nullable=false)
    private Organization organization;
    
    @ManyToOne
    @JoinColumn(name="playeruserid", nullable=false)
    private Player player;
    
    @ManyToOne
    @JoinColumn(name="tournamentid", nullable=false)
    private Tournament tournament;
    
    @OneToOne(mappedBy="invitation")
    private TournamentPlayer tournamentPlayer;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    public Organization getOrgnization() {
        return organization;
    }

    public void setOrgnization(Organization organization) {
        this.organization = organization;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public TournamentPlayer getTournamentPlayer() {
        return tournamentPlayer;
    }

    public void setTournamentPlayer(TournamentPlayer tournamentPlayer) {
        this.tournamentPlayer = tournamentPlayer;
    }
    
}
