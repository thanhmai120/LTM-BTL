/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.Serializable;
import java.util.List;
import java.util.Date;
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
@Table(name = "tbltournamentplayer")
public class TournamentPlayer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int ID;
    
    @Column(name = "day_join")
    private Date day_join;
    
    @ManyToOne
    @JoinColumn(name="tournamentid", nullable=false)
    private Tournament tournament;
    
    @ManyToOne
    @JoinColumn(name="playeruserid", nullable=false)
    private Player player;
    
    @OneToOne
    @JoinColumn(name="invitationid", nullable=false)
    private Invitation invitation;
    
    @OneToMany(mappedBy="tournamentPlayer")
    private List<GamePlayer> gamePlayers;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Date getDay_join() {
        return day_join;
    }

    public void setDay_join(Date day_join) {
        this.day_join = day_join;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Invitation getInvitation() {
        return invitation;
    }

    public void setInvitation(Invitation invitation) {
        this.invitation = invitation;
    }

    public List<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setGamePlayers(List<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }
    
}
