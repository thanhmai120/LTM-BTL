/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.Serializable;
import java.sql.Time;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "tbltournament")
public class Tournament implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int ID;
    @Column(name = "trm_name")
    private String trm_name;
    
    @Column(name = "time_begin")
    private Time time_begin;
    
    @Column(name = "time_end")
    private Time time_end;
    
    @Column(name = "description")
    private String description;
    
    @ManyToOne
    @JoinColumn(name="organizationuserid", nullable=false)
    private Organization organization;
    
    @OneToMany(mappedBy="tournament")
    private List<Game> games;
    
    @OneToMany(mappedBy="tournament")
    private List<TournamentPlayer> players;
    
    @OneToMany(mappedBy="tournament")
    private List<Invitation> invitations;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTrm_name() {
        return trm_name;
    }

    public void setTrm_name(String trm_name) {
        this.trm_name = trm_name;
    }

    public Time getTime_begin() {
        return time_begin;
    }

    public void setTime_begin(Time time_begin) {
        this.time_begin = time_begin;
    }

    public Time getTime_end() {
        return time_end;
    }

    public void setTime_end(Time time_end) {
        this.time_end = time_end;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public List<TournamentPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<TournamentPlayer> players) {
        this.players = players;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<Invitation> invitations) {
        this.invitations = invitations;
    }
    
}
