/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;
import java.util.ArrayList;
import java.util.List;
import model.PlayerRank;
import org.hibernate.query.NativeQuery;
public class PlayerRankDAO extends DAO{
    public PlayerRankDAO(){
        super();
    }
    public List<PlayerRank> getPlayerRanks(){
        NativeQuery q = session.createNativeQuery("SELECT u.*,\n" +
        "(SELECT COUNT(g.id) FROM tblgameplayer g WHERE g.playeruserid = p.id ) AS total_game,\n" +
        "(SELECT COUNT(g.id) FROM tblgameplayer g WHERE g.playeruserid = p.id AND g.is_winner = true) AS n_win,\n" +
        "(SELECT IFNULL(n_win/total_game,0)) AS win_rate,\n" +

        "(SELECT IFNULL(SUM(g.plus_score),0) FROM tblgameplayer g WHERE g.playeruserid = p.id AND\n" +
        "g.tournamentplayerid IS NOT NULL) AS trm_score,\n" +

        "(SELECT IFNULL(AVG((SELECT COUNT(gp.id) FROM tblgameplayer gp WHERE gp.playeruserid = g.playeruserid \n" +
        "AND gp.is_winner=true) / (SELECT COUNT(gp.id) FROM tblgameplayer gp \n" +
        "WHERE gp.playeruserid = g.playeruserid)),0) FROM tblgameplayer g WHERE g.playeruserid!=p.id AND g.gameid IN\n" +
        "(SELECT g.gameid FROM tblgameplayer g WHERE g.playeruserid = p.id)) as avg_op_win_rate\n" +

        "FROM tblplayer p, tbluser u WHERE p.id = u.id ORDER BY n_win, win_rate, avg_op_win_rate, trm_score ;" );
        List<PlayerRank> result = new ArrayList<PlayerRank>();
        List<Object[]> rows = (List<Object[]>)q.list();
        for(int i=0; i<rows.size(); i++){
            Object[] row = rows.get(i);
            PlayerRank r = new PlayerRank();
            r.setID(Integer.parseInt(row[0].toString()));
            r.setUsername(row[1].toString());
            r.setFullname(row[2].toString());
            r.setWin_number(Integer.parseInt(row[4].toString()));
            r.setWin_rate(Float.parseFloat(row[5].toString()));
            r.setTrm_score(Integer.parseInt(row[6].toString()));
            r.setRank(i);
            result.add(r);
        }
        return result;
    }
    
}
