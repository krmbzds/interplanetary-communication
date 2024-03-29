import java.util.Properties;

public class Config
{
    Properties configFile;
    public Config()
    {
        configFile = new java.util.Properties();
        try {
            configFile.load(this.getClass().getClassLoader().
                    getResourceAsStream("config/config.cfg"));
        }catch(Exception eta){
            eta.printStackTrace();
        }
    }

    public String getProperty(String key)
    {
        String value = this.configFile.getProperty(key);
        return value;
    }

    public void setProperty(String key, String value) {
        this.configFile.setProperty(key, value);
    }
}