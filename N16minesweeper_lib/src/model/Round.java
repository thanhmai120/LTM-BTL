/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Administrator
 */
@Entity
@Table(name = "tblround")
public class Round  implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int ID;
    
    @Column(name = "time_begin")
    private Timestamp time_begin;
    
    @Column(name = "time_end")
    private Timestamp time_end;
    
    @OneToMany(mappedBy="round")
    private List<Square> squares;
    
    @ManyToOne
    @JoinColumn(name="gameid", nullable=false)
    private Game game;
    
    @OneToMany(mappedBy="round")
    private List<RoundResult> results;

    public Round() {
    }

    
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Timestamp getTime_begin() {
        return time_begin;
    }

    public void setTime_begin(Timestamp time_begin) {
        this.time_begin = time_begin;
    }

    public Timestamp getTime_end() {
        return time_end;
    }

    public void setTime_end(Timestamp time_end) {
        this.time_end = time_end;
    }

    public List<Square> getSquares() {
        return squares;
    }

    public void setSquares(List<Square> squares) {
        this.squares = squares;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public List<RoundResult> getResults() {
        return results;
    }

    public void setResults(List<RoundResult> results) {
        this.results = results;
    }
    
    
}
