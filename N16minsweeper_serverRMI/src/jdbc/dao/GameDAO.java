/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jdbc.dao;
import java.util.List;
import model.Game;
import model.GamePlayer;
import model.Round;
import model.RoundResult;
import org.hibernate.query.Query;
public class GameDAO extends DAO{
    public GameDAO(){
        super();
    }
    public boolean addGame(Game game){
        try{
            if(!session.getTransaction().isActive()) session.beginTransaction();
            session.save(game);
            // save player
            for(GamePlayer player : game.getPlayers()){
                player.setGame(game);
                session.save(player);
            }
            // save round
            for(Round round :  game.getRounds()) {
                round.setGame(game);
                session.save(round);
                // save round results 
                for ( RoundResult r : round.getResults()) {
                    r.setRound(round);
                    session.save(r);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        session.getTransaction().commit();
        //evict
        session.evict(game);
        // evict players
        for(GamePlayer player : game.getPlayers() )
            session.evict(player);
        // evict rounds
        for(Round round :  game.getRounds()) {
            session.evict(round);
            for(RoundResult r : round.getResults()) {
                session.evict(r);
            }
        }
        return true;
    }
    public boolean saveGame(Game game){
        try {
            session.beginTransaction();
            session.update(game);
            List<GamePlayer> players = game.getPlayers();
            for(GamePlayer player : players){
                player.setGame(game);
                session.update(player);
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
        session.getTransaction().commit();
        session.evict(game);
        return true;
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
