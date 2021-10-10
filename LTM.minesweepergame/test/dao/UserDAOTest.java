/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;
import model.User;
import org.junit.Assert;
import org.junit.Test;
/**
 *
 * @author Administrator
 */
public class UserDAOTest {
    public UserDAOTest(){
        super();
    }
    
    @Test
    public void testCheckLogin(){
        UserDAO ud = new UserDAO();
        // test case 1 : username = "rrr" password = "fsdf";
        User u = new User();
        u.setUsername("rrr");
        u.setPassword("fsdf");
        Assert.assertFalse(ud.checkLogin(u));
        // test case 2: username = "abc" password = "def";
        u.setUsername("abc");
        u.setPassword("def");
        Assert.assertTrue(ud.checkLogin(u));
        Assert.assertEquals("Đặng Thanh Mai", u.getFullname());
        Assert.assertEquals("abc", u.getUsername());
        Assert.assertEquals(1, u.getID());
        Assert.assertEquals("player", u.getType());
        Assert.assertEquals("", u.getEmail());
        Assert.assertEquals(true, u.isActive());
    }
    
}
