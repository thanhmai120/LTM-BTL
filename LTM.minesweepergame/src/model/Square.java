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
@Table(name = "tblsquare")
public class Square implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int ID;
    
    @Column(name = "value")
    private int value;
    
    @Column(name = "is_bomb")
    private boolean is_bomb;
    
    @Column(name = "is_clicked")
    private boolean is_clicked;
    
    @ManyToOne
    @JoinColumn(name="gameid", nullable=false)
    private Game game;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isIs_bomb() {
        return is_bomb;
    }

    public void setIs_bomb(boolean is_bomb) {
        this.is_bomb = is_bomb;
    }

    public boolean isIs_clicked() {
        return is_clicked;
    }

    public void setIs_clicked(boolean is_clicked) {
        this.is_clicked = is_clicked;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
    
}
