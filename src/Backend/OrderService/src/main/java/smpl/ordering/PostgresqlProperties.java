package smpl.ordering;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Holds configuration properties pertaining to the PostgreSQL connection.
 */
@ConfigurationProperties(prefix = "postgresql")
public class PostgresqlProperties
{
    private String username;
    private String password;
    private String driverClass;
    private String url;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getDriverClass()
    {
        return driverClass;
    }

    public void setDriverClass(String driverClass)
    {
        this.driverClass = driverClass;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
}
