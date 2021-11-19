/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdbc.dao;

import java.util.List;
import model.FriendRequest;
import org.hibernate.query.Query;

/**
 *
 * @author Administrator
 */
public class FriendRequestDAO extends DAO{
    public boolean addFriendRequest(FriendRequest fr){
        if(!session.getTransaction().isActive()) session.beginTransaction();
        Query q1 = session.createQuery("SELECT r FROM FriendRequest r WHERE r.toPlayer.id = :fromPlayerID AND"
                + " r.fromPlayer.id = :toPlayerID AND (r.accepted = null OR r.accepted = 1)");
        Query q2 = session.createQuery("SELECT r FROM FriendRequest r WHERE r.toPlayer.id = :toPlayerID AND"
                + " r.fromPlayer.id = :fromPlayerID AND (r.accepted = null OR r.accepted = 1)");
        q1.setParameter("fromPlayerID", fr.getFromPlayer().getID());
        q1.setParameter("toPlayerID", fr.getToPlayer().getID());
        q2.setParameter("fromPlayerID", fr.getFromPlayer().getID());
        q2.setParameter("toPlayerID", fr.getToPlayer().getID());
        List<FriendRequest> l1 = q1.list();
        List<FriendRequest> l2 = q2.list();
        if(l1.size()>0 || l2.size()>0){
            session.getTransaction().rollback();
            return false;
        }
        session.save(fr);
        session.getTransaction().commit();
        // evict
        session.evict(fr);
        return true;
    }
    
    public boolean saveFriendRequest(FriendRequest fr){
        try{
            if(!session.getTransaction().isActive()) session.beginTransaction();
            session.update(fr);
            session.getTransaction().commit();
        }catch(Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return false;
        }
        return true;
    }
    
    public boolean deleteFriendRequest (FriendRequest fr) {
        try{
            if(!session.getTransaction().isActive()) session.beginTransaction();
            Query q1 = session.createQuery("SELECT fr FROM FriendRequest fr WHERE fr.fromPlayer.ID = :fromPlayerID"
                    + " AND fr.toPlayer.ID = :toPlayerID AND fr.accepted = true");
            Query q2 = session.createQuery("SELECT fr FROM FriendRequest fr WHERE fr.fromPlayer.ID = :toPlayerID"
                    + " AND fr.toPlayer.ID = :fromPlayerID AND fr.accepted = true");
            q1.setParameter("fromPlayerID", fr.getFromPlayer().getID());
            q1.setParameter("toPlayerID", fr.getToPlayer().getID());
            q2.setParameter("fromPlayerID", fr.getFromPlayer().getID());
            q2.setParameter("toPlayerID", fr.getToPlayer().getID());
            List<FriendRequest> l1 = q1.list();
            List<FriendRequest> l2 = q2.list();
            if(l1.size() == 0 && l2.size() == 0) {
                session.getTransaction().rollback();
                return false;
            }
            if(l1.size() > 0) 
                fr = l1.get(0);
            else
                fr = l2.get(0);
            session.delete(fr);
            session.getTransaction().commit();
        }catch(Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
            return false;
        }
        return true;
    }
    
    public List<FriendRequest> getPlayerFriendRequest(int playerID) {
        Query q = session.createQuery("SELECT r FROM FriendRequest r WHERE r.toPlayer.id = :playerID AND r.accepted = null");
        q.setParameter("playerID", playerID);
        List<FriendRequest> result = q.list();
        //evict
        for(FriendRequest fr : result) {
            session.evict(fr);
        }
        return result;
    }
}
