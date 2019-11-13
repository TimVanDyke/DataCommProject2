import java.util.*;

public class HostConnection{
    public String ip;
    public String hostname;
    ArrayList<HostFile> fileList = new ArrayList<HostFile>();
    public String speed;
    public String username;

    public HostConnection(String hostname, String speed, String username){
        //this.ip = ip;
        this.hostname = hostname;
        this.speed = speed;
        this.username = username;
    }
}