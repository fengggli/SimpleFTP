
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;
/**
 * created by lifeng on 10/21/16
 * this defines how clients and server communicate with each other
 * the programming model comes from the KnockKnock example here : http://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 * More detailed information about all the States, please refer to the assignment report
 */

import static java.nio.file.Files.exists;

public class FtpProtocol {
    private static final int WAITING = -1;
    private static final int Welcomed = 0;
    private static final int RegWaitName = 1;
    private static final int RegWaitPwd = 2;
    private static final int LoginWaitName = 3;
    private static final int LoginWaitPwd = 4;
    private static final int LoggedIn = 5;
    private static final int FileSent = 6;
    private static final int FileAck = 7;


    private static UserDB userDic = new UserDB();
    private int state = WAITING;

    public  String username;
    public  String passwd;


    public String processInput(String theInput) {
        String theOutput;

        if (state == WAITING) {
            theOutput = "welcome!" +
                    "1: Login   2: Register 3: exit";
            state = Welcomed;
        } else if (state == Welcomed) {
            if (theInput.equalsIgnoreCase("2")) {
                theOutput = "name to register?";
                state = RegWaitName;
            } else if(theInput.equalsIgnoreCase("1")) {
                theOutput = "name to login?";
                state = LoginWaitName;
            } else if(theInput.equalsIgnoreCase("3")){
                theOutput = "Bye.";
                state = WAITING;
            } else {
                // wrong input here
                theOutput=" wrong input, try again"+
                        "1. login 2. register 3.exit";
            }

        } else if (state == RegWaitName) {
            if (userDic.checkUserName(theInput) == false) {
                username = theInput;
                theOutput = "password?";
                state = RegWaitPwd;
            } else {
                theOutput = "user name unavailable, try another one";
            }
        } else if (state == RegWaitPwd) {
            // we can accept all not-null string as passwd
            if (theInput != null) {
                passwd = theInput;
                userDic.insertNewUser(username, passwd);
                theOutput = "You are now registered! 1. login 2. register 3. exit";
                state = Welcomed;
            } else {
                theOutput = "passwd should not be empty";
            }
        } else if(state == LoginWaitName){
            if(userDic.checkUserName(theInput) == true){
                // if user exsit
                username = theInput;
                theOutput = "password?";
                state = LoginWaitPwd;
            } else {
                theOutput = "username not exit 1.Login, 2.register 3. exit";
                state = Welcomed;
            }
        } else if(state == LoginWaitPwd){
            passwd = theInput;
            if(userDic.checkPasswd(username, passwd) == true){
                theOutput = "Login succeed, filepath to download?";
                state = LoggedIn;
            }
            else {
                theOutput = "Password not correct, 1.Login, 2.register 3. exit";
                state = Welcomed;
            }
        } else if(state == LoggedIn){
            // support multiple path
            // verify all paths, if alStringTokenizer tokenizer = new StringTokenizer(fromServer);
            StringTokenizer tokenizer = new StringTokenizer(theInput);

            String allPaths = "";
            String wrongPath = "";
            int numFiles = 0;
            boolean allFileExist = true;

            while (tokenizer.hasMoreTokens()) {
                Path filePath = Paths.get(tokenizer.nextToken());
                if (exists(filePath)) {
                    allPaths = allPaths + " " + theInput;
                    numFiles += 1;
                }
                else {
                    allFileExist = false;
                    numFiles = -1;
                    wrongPath += "file " + filePath + " not exit! ";
                    break;
                }
            }
            if(allFileExist){
                // message will be "#send #offiles path1 path2 path3"
                theOutput = "#send " + numFiles + allPaths;
                state = FileSent;
            }else {
                theOutput  = wrongPath + ", input paths again!";
            }
        }
        else if(state == FileSent){
            if(theInput.startsWith("#ack")){

                theOutput = "file transferred, 1.another transfer 2. exist";
                System.out.println("get ack from client");
                state = FileAck;
            }
            else {
                theOutput = "send failure, now retry. path?";
                state = LoggedIn;
            }
        }
        else if(state == FileAck) {
            if (theInput.equalsIgnoreCase("1")) {
                theOutput = "path?";
                state = LoggedIn;}
            else {
                theOutput = "Bye.";
                state = WAITING;
            }
        } else {
            theOutput = "server internal error. now exist. Bye.";
            state = WAITING;
        }

        return theOutput;
    }
}
