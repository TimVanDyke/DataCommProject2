import java.util.*;

public class HostFile {
    public String name;
    public String user;
    ArrayList<String> keywords;

    public HostFile(String name, String user, ArrayList<String> keywords){
        this.name = name;
        this.user = user;
        this.keywords = keywords;
    }

    public HostFile(){

    }
}