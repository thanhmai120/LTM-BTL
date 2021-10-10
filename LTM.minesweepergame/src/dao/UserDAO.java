/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;
import java.util.List;
import model.User;
import org.hibernate.query.Query;
public class UserDAO extends DAO{
    public UserDAO(){
        super();
    }
    public boolean checkLogin(User user){
        Query q = session.createQuery("SELECT u FROM User u WHERE u.username = :username AND u.password = :password");
        q.setParameter("username", user.getUsername());
        q.setParameter("password", user.getPassword());
        List<User> checkUser = q.list();
        if(checkUser.size()>0){
           User u = checkUser.get(0);
           user.setType(u.getType());
           user.setID(u.getID());
           user.setFullname(u.getFullname());
           user.setActive(u.isActive());
           user.setEmail(u.getEmail());
           return true;
        }
        return false;
    }
}
