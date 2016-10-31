import java.util.Dictionary;
import java.util.Hashtable;

/**
 * Created by lifeng on 10/21/16.
 * User dictionary, managing all the user passwords also.
 */
public class UserDB {
    private Dictionary<String, String> userDic;

    public UserDB() {
        userDic = new Hashtable<String,String>();
    }

    public boolean checkUserName(String username){
        if(userDic.get(username) == null){
            return false;
        }
        else{
            return true;
        }
    }
    public boolean insertNewUser(String username, String passwd){
        if(checkUserName(username) == true){
            //System.out.println("User "+ username + " not created");
            return false;
        }
        System.out.println("User "+ username + " created");
        userDic.put(username, passwd);
        return true;
    }
    public boolean checkPasswd(String username, String passwd){
        if(userDic.get(username).equals(passwd)){
            //System.out.println("user " + username + " exists");
            return true;

        }
        else
            return false;
    }
}
