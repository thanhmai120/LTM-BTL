/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jdbc.dao;
import java.util.ArrayList;
import java.util.List;
import model.GamePlayer;
import model.TournamentPlayer;
import org.hibernate.query.Query;
public class TournamentPlayerDAO extends DAO{
    public TournamentPlayerDAO(){
        super();
    }
    public TournamentPlayer getTournamentPlayerByID(int playerID){
        Query q = session.createQuery("SELECT p FROM TournamentPlayer p WHERE p.ID = :playerID");
        q.setParameter("playerID", playerID);
        List<TournamentPlayer> result = q.list();
        if(result.size()>0){
            return result.get(0);
        }
        return null;
    }
    public boolean addTournamentPlayer(TournamentPlayer trmPlayer){
        try{
            session.beginTransaction();
            Query q = session.createQuery("SELECT tp FROM TournamentPlayer tp WHERE tp.player.ID = :playerID AND "
                    + "tp.tournament.ID = :tournamentID");
            q.setParameter("playerID",trmPlayer.getPlayer().getID());
            q.setParameter("tournamentID", trmPlayer.getTournament().getID());
            List<TournamentPlayer> l = q.list();
            if(l.size()>0){
                session.getTransaction().rollback();
                return true;
            }
            session.save(trmPlayer);
        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
        session.getTransaction().commit();
        return true;
    }
    public List<TournamentPlayer> getTournamentPlayers(int trmID,int playerID){
        Query q = session.createQuery("SELECT p FROM TournamentPlayer p WHERE p.tournament.ID = :trmID");
        Query qGamePlayer = session.createQuery("SELECT gp FROM GamePlayer gp WHERE gp.tournamentPlayer.ID"
                + " = :trmPlayerID AND gp.game.ID IN"
                + " (SELECT p.game.ID FROM GamePlayer p WHERE p.player.ID = :playerID)");
        q.setParameter("trmID", trmID);
        List<TournamentPlayer> result = q.list();
        for(TournamentPlayer tp : result) {
            qGamePlayer.setParameter("trmPlayerID", tp.getID());
            qGamePlayer.setParameter("playerID", playerID);
            List<GamePlayer> gps = qGamePlayer.list();
            tp.setGamePlayers(gps);
        }
        return result;
    }
    public List<TournamentPlayer> getTournamentPlayersOfPlayer(int playerID){
        Query q = session.createQuery("SELECT p FROM TournamentPlayer p WHERE p.player.ID = :playerID");
        q.setParameter("playerID", playerID);
        List<TournamentPlayer> result = new ArrayList<>();
        try{
        result = q.list();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
