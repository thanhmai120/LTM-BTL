package model;
 
import java.io.Serializable;
 
public class ObjectWrapper  implements Serializable{
    private static final long serialVersionUID = 20210811011L;
    public static final int SERVER_INFORM_CLIENT_NUMBER = 1;
    public static final int SERVER_INFORM_USER_IN = 2;
    public static final int SERVER_INFORM_USER_OUT = 3;
    public static final int LOGIN_USER = 4;
    public static final int REPLY_LOGIN_USER = 5;
    public static final int REGISTER_USER = 6;
    public static final int REPLY_REGISTER_USER = 7;
    public static final int CHALLENGE_PLAYER = 8;
    public static final int REPLY_CHALLENGE_PLAYER = 9;
    public static final int SERVER_INFORM_CHALLENGE = 10;
    public static final int CANCEL_CHALLENGE = 11;
    public static final int ANSWER_CHALLENGE = 12;
    public static final int SERVER_REPLY_ANSWER = 13;
    public static final int MAKE_A_MOVE = 14;
    public static final int SERVER_UPDATE_GAME_STAT = 15;
    public static final int GET_LIST_FRIEND = 16;
    public static final int REPLY_GET_LIST_FRIEND = 17;
    public static final int GET_LIST_TOURNAMENT = 18;
    public static final int REPLY_GET_LIST_TOURNAMENT = 19;
    public static final int GET_LIST_PLAYER_RANK = 20;
    public static final int REPLY_GET_LIST_PLAYER_RANK = 21;
    
    private int performative;
    private Object data;
    public ObjectWrapper() {
        super();
    }
    public ObjectWrapper(int performative, Object data) {
        super();
        this.performative = performative;
        this.data = data;
    }
    public int getPerformative() {
        return performative;
    }
    public void setPerformative(int performative) {
        this.performative = performative;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }   
}