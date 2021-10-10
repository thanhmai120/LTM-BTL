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
@Table(name = "tblgame")
public class Game  implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int ID;
    
    @Column(name = "width")
    private int width;
    
    @Column(name = "time_begin")
    private Time time_begin;
    
    @Column(name = "time_end")
    private Time time_end;
    
    @Column(name = "bomb_number")
    private int bomb_number;
    
    @OneToMany(mappedBy="game")
    private List<GamePlayer> players;
    
    @ManyToOne
    @JoinColumn(name="tournamentid", nullable=true)
    private Tournament tournament;
    
    @OneToMany(mappedBy="game")
    private List<Square> squares;
    
    @OneToOne(mappedBy="game")
    private Challenge challenge;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
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

    public int getBomb_number() {
        return bomb_number;
    }

    public void setBomb_number(int bomb_number) {
        this.bomb_number = bomb_number;
    }

    public List<GamePlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<GamePlayer> players) {
        this.players = players;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public List<Square> getSquares() {
        return squares;
    }

    public void setSquares(List<Square> squares) {
        this.squares = squares;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }
    
}
