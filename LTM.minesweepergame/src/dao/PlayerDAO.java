/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;
import java.util.List;
import model.Player;
import org.hibernate.query.Query;
public class PlayerDAO extends DAO{
    public PlayerDAO(){
        super();
    }
    public List<Player> searchPlayer(String key){
        Query q = session.createQuery("SELECT p FROM Player p WHERE p.fullname LIKE '%"+key+"%'");
        List<Player> result = q.list();
        return result;
    }
}
