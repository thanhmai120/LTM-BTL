/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import model.Game;
import model.GamePlayer;
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
public class GameDAOTest {
    public GameDAOTest(){
        super();
    }
    @Test
    public void testAddGame(){
        GameDAO gd = new GameDAO();
        Session s = gd.session;
        //test case: game has 2 players
        s.beginTransaction();
        Game g = new Game();
        java.util.Date utilDate = new java.util.Date();
        Player p1 = new Player();
        p1.setID(1);
        Player p2 = new Player();
        p2.setID(2);
        GamePlayer gp1 = new GamePlayer();
        gp1.setPlayer(p1);
        gp1.setIs_next(true);
        gp1.setColor("red");
        GamePlayer gp2 = new GamePlayer();
        gp2.setPlayer(p2);
        gp2.setColor("blue");
        List<GamePlayer> players = new ArrayList<>();
        players.add(gp1);
        players.add(gp2);
        g.setBomb_number(31);
        g.setTime_begin(new Time(utilDate.getTime()));
        g.setWidth(10);
        g.setPlayers(players);
        gd.addGame(g);
        g = gd.getGameByID(g.getID());
        Assert.assertNotNull(g);
        Assert.assertEquals(31, g.getBomb_number());
        Assert.assertEquals(10, g.getWidth());
        for(int i=0;i<players.size();i++){
            GamePlayer gp = players.get(i);
            Assert.assertEquals(gp.getID(), g.getPlayers().get(i).getID());
            Assert.assertEquals(gp.getColor(), g.getPlayers().get(i).getColor());
            Assert.assertEquals(gp.isIs_next(), g.getPlayers().get(i).isIs_next());
            Assert.assertEquals(gp.getPlayer().getID(), g.getPlayers().get(i).getPlayer().getID());
        }
        s.getTransaction().rollback();
        
        //test case: game has 1 player
        s.beginTransaction();
        g = new Game();
        players = new ArrayList<>();
        players.add(gp1);
        g.setBomb_number(31);
        g.setTime_begin(new Time(utilDate.getTime()));
        g.setWidth(10);
        g.setPlayers(players);
        gd.addGame(g);
        g = gd.getGameByID(g.getID());
        Assert.assertNull(g);
        s.getTransaction().rollback();
    }
}
