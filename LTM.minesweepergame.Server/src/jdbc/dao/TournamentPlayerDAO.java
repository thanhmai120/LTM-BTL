/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jdbc.dao;
import java.util.List;
import model.TournamentPlayer;
import org.hibernate.query.Query;
public class TournamentPlayerDAO extends DAO{
    public TournamentPlayerDAO(){
        super();
    }
    public TournamentPlayer getTournamentPlayerByID(int tpID){
        Query q = session.createQuery("SELECT p FROM TournamentPlayer p WHERE p.ID = :tpID");
        q.setParameter("tpID", tpID);
        List<TournamentPlayer> result = q.list();
        if(result.size()>0){
            return result.get(0);
        }
        return null;
    }
    public void addTournamentPlayer(TournamentPlayer trmPlayer){
        session.save(trmPlayer);
    }
    public List<TournamentPlayer> getTournamentPlayers(int trmID){
        Query q = session.createQuery("SELECT p FROM TournamentPlayer p WHERE p.tournament.ID = :trmID");
        q.setParameter("trmID", trmID);
        List<TournamentPlayer> result = q.list();
        return result;
    }
    public List<TournamentPlayer> getTournamentPlayersOfPlayer(int playerID){
        Query q = session.createQuery("SELECT p FROM TournamentPlayer p WHERE p.player.ID = :playerID");
        q.setParameter("playerID", playerID);
        List<TournamentPlayer> result = q.list();
        return result;
    }
}
