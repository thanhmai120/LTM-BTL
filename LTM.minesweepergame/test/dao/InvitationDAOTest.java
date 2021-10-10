/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;
import java.sql.Date;
import java.sql.Time;
import model.Invitation;
import model.Organization;
import model.Player;
import model.Tournament;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;
/**
 *
 * @author Administrator
 */
public class InvitationDAOTest {
    public InvitationDAOTest(){
        super();
    }
    @Test
    public void testAddInvitation(){
        InvitationDAO id = new InvitationDAO();
        Session s = id.session;
        //test case
        s.beginTransaction();
        Invitation i = new Invitation();
        Organization o = new Organization();
        o.setID(3);
        Player p = new Player();
        p.setID(2);
        Tournament t = new Tournament();
        t.setID(13);
        java.util.Date utilDate = new java.util.Date();
        i.setExpired(new Date(utilDate.getTime()+24*3600));
        i.setOrgnization(o);
        i.setPlayer(p);
        i.setTime(new Time(utilDate.getTime()));
        i.setTournament(t);
        id.addInvitation(i);
        i = id.getInvitationByID(i.getID());
        Assert.assertNotNull(i);
        Assert.assertEquals(new Date(utilDate.getTime()+24*3600), i.getExpired());
        Assert.assertEquals(3, i.getOrgnization().getID());
        Assert.assertEquals(2, i.getPlayer().getID());
        Assert.assertEquals(13, i.getTournament().getID());
        Assert.assertEquals(new Time(utilDate.getTime()), i.getTime());
        s.getTransaction().commit();
    }
}
