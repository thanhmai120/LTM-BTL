/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;
import java.sql.Date;
import java.util.List;
import model.Tournament;
import org.hibernate.query.Query;
public class TournamentDAO extends DAO{
    public TournamentDAO(){
        super();
    }
    
    public Tournament getTournamentByID(int tournamentID){
        Tournament t = null;
        Query q = session.createQuery("SELECT t FROM Tournament t WHERE t.ID = ?");
        q.setParameter(0, tournamentID);
        List<Tournament> result = q.list();
        if(result.size()>0){
            t = result.get(0);
        }
        return t;
    }
    public void addTournament(Tournament tournament){
        session.save(tournament);
        
    }
    public List<Tournament> getPlayerActiveTournaments(int playerID){
        Query q = session.createQuery("SELECT t FROM Tournament t WHERE t.ID IN"
                + " (SELECT tp.tournament.ID FROM TournamentPlayer tp WHERE tp.player.ID = :playerID)"
                + " AND t.time_end < :today");
        q.setParameter("playerID", playerID);
        java.util.Date utilDate = new java.util.Date();
        q.setParameter("today", new Date(utilDate.getTime()));
        List<Tournament> result = q.list();
        return result;
    }
    public List<Tournament> getOrganizationTournaments(int orgID){
        Query q = session.createQuery("SELECT t FROM Tournament t WHERE t.organization.ID = :orgID AND"
                + " t.time_end < :today");
        q.setParameter("orgID", orgID);
        java.util.Date utilDate = new java.util.Date();
        q.setParameter("today", new Date(utilDate.getTime()));
        List<Tournament> result = q.list();
        return result;
    }
}
