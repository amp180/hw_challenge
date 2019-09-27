package gitTester;
import java.util.*;
import java.io.*;

public class GithubConfig {
    final static String propsfile = "githubConfig.properties";
    String username;
    String token;

    public GithubConfig(){
        Properties props = new Properties();
        InputStream inputStream = null;
        
        try{
            inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream(propsfile); 
            props.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        this.username = props.getProperty("username");
        this.token = props.getProperty("token");
    }
}