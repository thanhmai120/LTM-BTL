/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.Serializable;

/**
 *
 * @author Administrator
 */
public class PlayerRank extends PlayerStat implements Serializable{
    private float win_rate;
    private float avg_opponent_win_rate;
    private int win_number;
    private int trm_score;
    private int rank;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
    
    

    public float getWin_rate() {
        return win_rate;
    }

    public void setWin_rate(float win_rate) {
        this.win_rate = win_rate;
    }

    public float getAvg_opponent_win_rate() {
        return avg_opponent_win_rate;
    }

    public void setAvg_opponent_win_rate(float avg_opponent_win_rate) {
        this.avg_opponent_win_rate = avg_opponent_win_rate;
    }

    public int getWin_number() {
        return win_number;
    }

    public void setWin_number(int win_number) {
        this.win_number = win_number;
    }

    public int getTrm_score() {
        return trm_score;
    }

    public void setTrm_score(int trm_score) {
        this.trm_score = trm_score;
    }
    
}
