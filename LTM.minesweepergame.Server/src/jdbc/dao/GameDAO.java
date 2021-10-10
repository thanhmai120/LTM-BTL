/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jdbc.dao;
import java.util.List;
import model.Game;
import model.GamePlayer;
import org.hibernate.query.Query;
public class GameDAO extends DAO{
    public GameDAO(){
        super();
    }
    public void addGame(Game game){
        if(!session.getTransaction().isActive()) session.beginTransaction();
        session.save(game);
        List<GamePlayer> players = game.getPlayers();
        if(players.size()!=2){
            session.getTransaction().rollback();
            return;
        }
        for(GamePlayer player : players ){
            player.setGame(game);
            session.save(player);
        }
        session.getTransaction().commit();
    }
    public void saveGame(Game game){
        session.beginTransaction();
        session.update(game);
        List<GamePlayer> players = game.getPlayers();
        for(GamePlayer player : players){
            player.setGame(game);
            session.update(player);
        }
        session.getTransaction().commit();
    }
    public List<Game> getTournamentPlayerActiveGame(int playerID, int tournamentID){
        Query q = session.createQuery("SELECT g FROM Game g WHERE g.tournament.ID = :tournamentID AND"
                + " g.ID IN (SELECT p.game.ID FROM GamePlayer p WHERE p.player.ID = :playerID) AND"
                + " g.time_end IS NULL");
        q.setParameter("tournamentID", tournamentID);
        q.setParameter("playerID", playerID);
        List<Game> result = q.list();
        return result;
    }
    public Game getGameByID(int gameID){
        Query getGame = session.createQuery("SELECT g FROM Game g WHERE g.ID = :gameID");
        Query getGamePlayers = session.createQuery("SELECT p FROM GamePlayer p WHERE p.game.ID = :gameID");
        getGame.setParameter("gameID", gameID);
        getGamePlayers.setParameter("gameID", gameID);
        List<Game> games = getGame.list();
        List<GamePlayer> players = getGamePlayers.list();
        if(games.size()>0){
            games.get(0).setPlayers(players);
            return games.get(0);
        }
        return null;
    }
}
