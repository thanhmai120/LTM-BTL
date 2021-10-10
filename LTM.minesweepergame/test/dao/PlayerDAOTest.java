/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;
import java.util.List;
import model.Player;
import org.junit.Assert;
import org.junit.Test;
/**
 *
 * @author Administrator
 */
public class PlayerDAOTest {
    public PlayerDAOTest(){
        super();
    }
    
    @Test
    public void testsearchPlayer(){
        PlayerDAO pd = new PlayerDAO();
        // test case 1: key = "xxxx"
        String key = "xxxx";
        List<Player> l = pd.searchPlayer(key);
        Assert.assertNotNull(l);
        Assert.assertEquals(0, l.size());
        
        // test case 2: key = "m"
        key = "m";
        l = pd.searchPlayer(key);
        Assert.assertNotNull(l);
        Assert.assertEquals(1, l.size());
        Player p = l.get(0);
        Assert.assertNotNull(p);
        Assert.assertEquals("Đặng Thanh Mai", p.getFullname());
        Assert.assertEquals("abc", p.getUsername());
        Assert.assertEquals(1, p.getID());
        Assert.assertEquals("player", p.getType());
        Assert.assertEquals("", p.getEmail());
        Assert.assertEquals(true, p.isActive());
    }
}
