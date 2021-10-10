/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;
import java.sql.Time;
import model.Tournament;
import model.Organization;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Administrator
 */
public class TournamentDAOTest {
    public TournamentDAOTest(){
        super();
    }
    @Test
    public void testAddTournament(){
        TournamentDAO td = new TournamentDAO();
        Session s = td.session;
        s.beginTransaction();
        Tournament t = new Tournament();
        java.util.Date utilDate = new java.util.Date();
        t.setTrm_name("new tournament 1");
        t.setTime_begin(new Time(utilDate.getTime()));
        t.setTime_end(new Time(utilDate.getTime()+(30*24*3600)));
        // test case: organization id = 3
        Organization o = new Organization();
        o.setID(3);
        t.setOrganization(o);
        td.addTournament(t);
        t = td.getTournamentByID(t.getID());
        Assert.assertNotNull(t);
        Assert.assertEquals("new tournament 1",t.getTrm_name());
        Assert.assertTrue(new Time(utilDate.getTime()).equals(t.getTime_begin()));
        Assert.assertTrue(new Time(utilDate.getTime()+(30*24*3600)).equals(t.getTime_end()));
        Assert.assertEquals(3, t.getOrganization().getID());
        s.getTransaction().rollback();
    }
}
