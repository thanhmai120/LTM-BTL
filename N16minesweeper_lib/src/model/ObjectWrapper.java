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
    public static final int GET_LIST_PLAYER_TOURNAMENT = 18;
    public static final int REPLY_GET_LIST_PLAYER_TOURNAMENT = 19;
    public static final int GET_LIST_PLAYER_RANK = 20;
    public static final int REPLY_GET_LIST_PLAYER_RANK = 21;
    public static final int SEND_FRIEND_REQUEST = 22;
    public static final int REPLY_FRIEND_REQUEST = 23;
    public static final int SERVER_INFORM_FRIEND_REQUEST = 24;
    public static final int ANSWER_FRIEND_REQUEST = 25;
    public static final int REPLY_ANSWER_FRIEND_REQUEST = 26;
    public static final int SERVER_INFORM_ANSWER_FRIEND_REQUEST = 27;
    public static final int CANCEL_FRIEND_REQUEST = 28;
    public static final int REPLY_CANCEL_FRIEND_REQUEST = 29;
    public static final int UNFRIEND = 30;
    public static final int REPLY_UNFRIEND = 31;
    public static final int SERVER_INFORM_UNFRIEND = 32;
    public static final int GET_LIST_FRIEND_REQUEST = 33;
    public static final int REPLY_GET_LIST_FRIEND_REQUEST = 34;
    public static final int ADD_GAME = 35;
    public static final int REPLY_ADD_GAME = 36;
    public static final int SAVE_GAME = 37;
    public static final int REPLY_SAVE_GAME = 38;
    public static final int SEND_INVITATION = 39;
    public static final int ANSWER_INVITATION = 40;
    public static final int SERVER_INFORM_INVITATION = 41;
    public static final int SERVER_INFORM_NEW_TOURNAMENT_PLAYER = 42;
    public static final int GET_LIST_TOURNAMENT_PLAYER = 43;
    public static final int REPLY_GET_LIST_TOURNAMENT_PLAYER = 44;
    public static final int REPLY_SEND_INVITATION = 45;
    public static final int GET_LIST_INVITATION = 46;
    public static final int REPLY_GET_LIST_INVITATION = 47;
    public static final int REPLY_ANSWER_INVITATION = 48;
    public static final int START_TRM_GAME = 49;
    public static final int REPLY_START_TRM_GAME = 50;
    public static final int SEE_GAME_RESULT = 51;
    public static final int REPLY_GAME_RESULT = 52;
    
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