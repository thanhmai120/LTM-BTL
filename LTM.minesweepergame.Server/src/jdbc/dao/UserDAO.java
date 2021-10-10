/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jdbc.dao;
import java.util.List;
import model.Player;
import model.User;
import org.hibernate.query.Query;
public class UserDAO extends DAO{
    public UserDAO(){
        super();
    }
    public User checkLogin(User user){
        Query q = session.createQuery("SELECT u FROM User u WHERE u.username = :username AND u.password = :password");
        q.setParameter("username", user.getUsername());
        q.setParameter("password", user.getPassword());
        List<User> checkUser = q.list();
        if(checkUser.size()>0){
           User u = checkUser.get(0);
           return u;
        }
        return null;
    }
    
    public boolean addUser(User user){
        try{
            session.beginTransaction();
            if(user.getType().equals("player")) {
                Player p = new Player();
                p.setActive(user.isActive());
                p.setEmail(user.getEmail());
                p.setPassword(user.getPassword());
                p.setType(user.getType());
                p.setUsername(user.getUsername());
                p.setFullname(user.getFullname());
                session.save("User",p);
            }
            session.getTransaction().commit();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
