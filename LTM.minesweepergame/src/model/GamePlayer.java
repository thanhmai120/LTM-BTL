/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Administrator
 */

@Entity
@Table(name = "tblgameplayer")
public class GamePlayer implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int ID;
    
    @Column(name = "score")
    private int score;
    
    @Column(name = "color")
    private String color;
    
    @Column(name = "is_next")
    private boolean is_next;
    
    @Column(name = "is_winner")
    private boolean is_winner;
    
    @Column(name = "plus_score")
    private int plus_score;
    
    @ManyToOne
    @JoinColumn(name="gameid", nullable=false)
    private Game game;
    
    @ManyToOne
    @JoinColumn(name="playeruserid", nullable=false)
    private Player player;
    
    @ManyToOne
    @JoinColumn(name="tournamentplayerid", nullable=true)
    private TournamentPlayer tournamentPlayer;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isIs_next() {
        return is_next;
    }

    public void setIs_next(boolean is_next) {
        this.is_next = is_next;
    }

    public boolean isIs_winner() {
        return is_winner;
    }

    public void setIs_winner(boolean is_winner) {
        this.is_winner = is_winner;
    }

    public int getPlus_score() {
        return plus_score;
    }

    public void setPlus_score(int plus_score) {
        this.plus_score = plus_score;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public TournamentPlayer getTournamentPlayer() {
        return tournamentPlayer;
    }

    public void setTournamentPlayer(TournamentPlayer tournamentPlayer) {
        this.tournamentPlayer = tournamentPlayer;
    }
    
}
