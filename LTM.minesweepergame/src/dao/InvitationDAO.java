/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;
import java.sql.Date;
import java.util.List;
import model.Invitation;
import org.hibernate.query.Query;
public class InvitationDAO extends DAO{
    public InvitationDAO(){
        super();
    }
    public Invitation getInvitationByID(int invitationID){
        Invitation i = null;
        Query q = session.createQuery("SELECT i FROM Invitation i WHERE i.ID = ?");
        q.setParameter(0, invitationID);
        List<Invitation> result = q.list();
        if(result.size()>0){
            i = result.get(0);
        }
        return i;
    }
    public void addInvitation(Invitation invitation){
        session.save(invitation);
    }
    public void saveInvitation(Invitation invitation){
        session.update(invitation);
    }
    public List<Invitation> getPlayerInvitations(int playerID){
        Query q = session.createQuery("SELECT i FROM Invitation i WHERE i.player.ID = :playerID AND"
                + " i.expired < :today AND i.accepted IS NULL");
        q.setParameter("playerID", playerID);
        java.util.Date utilDate = new java.util.Date();
        q.setParameter("today", new Date(utilDate.getTime()));
        List<Invitation> result = q.list();
        return result;
    }
}
