/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdbc.dao;

import java.util.List;
import model.Invitation;
import org.hibernate.query.Query;

/**
 *
 * @author Administrator
 */
public class InvitationDAO extends DAO{
    
    public boolean addInvitation(Invitation inv) {
        try{
            session.save(inv);
        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }
        session.evict(inv);
        return true;
    }
    public List<Invitation> getListInvitation(int playerID) {
        Query q = session.createQuery("SELECT i FROM Invitation i WHERE i.player.ID = :playerID AND i.accepted = null");
        q.setParameter("playerID", playerID);
        List<Invitation> result = q.list();
        for(Invitation inv : result) {
            session.evict(inv);
        }
        return result;
    }
    
    public boolean saveInvitation(Invitation inv) {
        try{
            if(!session.getTransaction().isActive()) session.beginTransaction();
            session.update(inv);
            session.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            session.getTransaction().rollback();
            return false;
        }
        return true;
    }
}
