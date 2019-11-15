import java.util.*;

public class HostFile {
    public String name;
    public String user;
    public String port;
    public String speed;
    public String hostname;
    ArrayList<String> keywords;

    public HostFile(String name, String user, String speed, String hostname, String port, ArrayList<String> keywords){
        this.name = name;
        this.user = user;
        this.speed = speed;
        this.hostname = hostname;
        this.port = port;
        this.keywords = keywords;
    }

    public HostFile(){

    }
    public String getName(){
        return name;
    }
    public String getUser(){
        return user;
    }
    public ArrayList<String> getKeywords(){
        return keywords;
    }
}