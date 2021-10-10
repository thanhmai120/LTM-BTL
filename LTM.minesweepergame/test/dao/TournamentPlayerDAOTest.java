/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;
import java.sql.Date;
import model.Invitation;
import model.Tournament;
import model.Organization;
import model.Player;
import model.TournamentPlayer;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;
/**
 *
 * @author Administrator
 */
public class TournamentPlayerDAOTest {
    public TournamentPlayerDAOTest(){
        super();
    }
    @Test
    public void testAddTournamentPlayer(){
        TournamentPlayerDAO tpd = new TournamentPlayerDAO();
        Session s = tpd.session;
        // test case 1: người chơi chưa tham gia giải đấu
        s.beginTransaction();
        TournamentPlayer tp = new TournamentPlayer();
        Player p = new Player();
        p.setID(2);
        Invitation i = new Invitation();
        i.setID(4);
        Tournament t = new Tournament();
        t.setID(14);
        java.util.Date utilDate = new java.util.Date();
        tp.setDay_join(new Date(utilDate.getTime()));
        tp.setInvitation(i);
        tp.setTournament(t);
        tp.setPlayer(p);
        tpd.addTournamentPlayer(tp);
        tp = tpd.getTournamentPlayerByID(tp.getID());
        Assert.assertNotNull(tp);
        Assert.assertEquals(new Date(utilDate.getTime()), tp.getDay_join());
        Assert.assertEquals(4, tp.getInvitation().getID());
        Assert.assertEquals(14, tp.getTournament().getID());
        Assert.assertEquals(2, tp.getPlayer().getID());
        s.getTransaction().rollback();
    }
}
