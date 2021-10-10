/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jdbc.dao;
import java.util.ArrayList;
import java.util.List;
import model.PlayerRank;
import org.hibernate.query.NativeQuery;
public class PlayerRankDAO extends DAO{
    public PlayerRankDAO(){
        super();
    }
    public List<PlayerRank> getPlayerRanks(){
        List<PlayerRank> result = new ArrayList<PlayerRank>();
        NativeQuery q = session.createNativeQuery("SELECT u.*,\n" +
        "(SELECT COUNT(g.id) FROM tblgameplayer g WHERE g.playeruserid = p.id ) AS total_game,\n" +
        "(SELECT COUNT(g.id) FROM tblgameplayer g WHERE g.playeruserid = p.id AND g.is_winner = 1) AS n_win,\n" +
        "(SELECT IFNULL(n_win/total_game,0)) AS win_rate,\n" +

        "(SELECT IFNULL(AVG((SELECT COUNT(gp.id) FROM tblgameplayer gp WHERE gp.playeruserid = g.playeruserid \n" +
        "AND gp.is_winner=true) / (SELECT COUNT(gp.id) FROM tblgameplayer gp \n" +
        "WHERE gp.playeruserid = g.playeruserid)),0) FROM tblgameplayer g WHERE g.playeruserid!=p.id AND g.gameid IN\n" +
        "(SELECT g.gameid FROM tblgameplayer g WHERE g.playeruserid = p.id)) as avg_op_win_rate,\n" +

        "(SELECT IFNULL(SUM(g.plus_score),0) FROM tblgameplayer g WHERE g.playeruserid = p.id AND\n" +
        "g.tournamentplayerid IS NOT NULL) AS trm_score\n" +

        "FROM tblplayer p, tbluser u WHERE p.id = u.id ORDER BY n_win DESC, win_rate DESC, avg_op_win_rate DESC, trm_score DESC;" );
        List<Object[]> rows = (List<Object[]>)q.list();
        for(int i=0; i<rows.size(); i++){
            Object[] row = rows.get(i);
            PlayerRank r = new PlayerRank();
            r.setID(Integer.parseInt(row[0].toString()));
            r.setUsername(row[1].toString());
            r.setFullname(row[3].toString());
            r.setEmail(row[4].toString());
            r.setActive(Boolean.parseBoolean(row[5].toString()));
            r.setType(row[6].toString());
            r.setWin_number(Integer.parseInt(row[8].toString()));
            r.setWin_rate(Float.parseFloat(row[9].toString()));
            r.setAvg_opponent_win_rate(Float.parseFloat(row[10].toString()));
            r.setTrm_score(Integer.parseInt(row[11].toString()));
            r.setRank(i+1);
            result.add(r);
        }
        return result;
    }
    
    public List<PlayerRank> getPlayerRanks(int playerID){
        NativeQuery q = session.createNativeQuery("SELECT u.*,\n" +
        "(SELECT COUNT(g.id) FROM tblgameplayer g WHERE g.playeruserid = p.id ) AS total_game,\n" +
        "(SELECT COUNT(g.id) FROM tblgameplayer g WHERE g.playeruserid = p.id AND g.is_winner = 1) AS n_win,\n" +
        "(SELECT IFNULL(n_win/total_game,0)) AS win_rate,\n" +

        "(SELECT IFNULL(AVG((SELECT COUNT(gp.id) FROM tblgameplayer gp WHERE gp.playeruserid = g.playeruserid \n" +
        "AND gp.is_winner=true) / (SELECT COUNT(gp.id) FROM tblgameplayer gp \n" +
        "WHERE gp.playeruserid = g.playeruserid)),0) FROM tblgameplayer g WHERE g.playeruserid!=p.id AND g.gameid IN\n" +
        "(SELECT g.gameid FROM tblgameplayer g WHERE g.playeruserid = p.id)) as avg_op_win_rate,\n" +

        "(SELECT IFNULL(SUM(g.plus_score),0) FROM tblgameplayer g WHERE g.playeruserid = p.id AND\n" +
        "g.tournamentplayerid IS NOT NULL) AS trm_score,\n" +
                
        "(SELECT CASE r.accepted WHEN 1 THEN 'true' ELSE 'false' END FROM tblfriendrequest r\n" +
        "WHERE (r.fromplayeruserid = :playerID AND r.toplayeruserid = p.id) \n" +
        "OR (r.fromplayeruserid = p.id AND r.toplayeruserid = :playerID)) AS befriend\n"+

        "FROM tblplayer p, tbluser u WHERE p.id = u.id ORDER BY n_win DESC, win_rate DESC, avg_op_win_rate DESC, trm_score DESC;" );
        List<PlayerRank> result = new ArrayList<PlayerRank>();
        q.setParameter("playerID", playerID);
        List<Object[]> rows = (List<Object[]>)q.list();
        for(int i=0; i<rows.size(); i++){
            Object[] row = rows.get(i);
            PlayerRank r = new PlayerRank();
            r.setID(Integer.parseInt(row[0].toString()));
            r.setUsername(row[1].toString());
            r.setFullname(row[3].toString());
            r.setEmail(row[4].toString());
            r.setActive(Boolean.parseBoolean(row[5].toString()));
            r.setType(row[6].toString());
            r.setWin_number(Integer.parseInt(row[8].toString()));
            r.setWin_rate(Float.parseFloat(row[9].toString()));
            r.setAvg_opponent_win_rate(Float.parseFloat(row[10].toString()));
            r.setTrm_score(Integer.parseInt(row[11].toString()));
            r.setFriend_stat(Boolean.parseBoolean(row[12] == null ? "false" : row[12].toString()));
            r.setRank(i+1);
            result.add(r);
        }
        return result;
    }
    
    public List<PlayerRank> getFriendRanks(int playerID){
        List<PlayerRank> friendRanks = new ArrayList<PlayerRank>();
        List<PlayerRank> ranks = getPlayerRanks(playerID);
        for(PlayerRank r: ranks){
            if(r.isFriend_stat())
                friendRanks.add(r);
        }
        return friendRanks;
    }
    
}
