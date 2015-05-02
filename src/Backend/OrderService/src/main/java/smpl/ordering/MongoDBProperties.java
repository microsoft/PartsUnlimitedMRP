package smpl.ordering;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Holds configuration properties pertaining to the MongoDB connection.
 */
@ConfigurationProperties(prefix = "mongodb")
public class MongoDBProperties
{
    private String host = "localhost";
    private String database = "ordering";

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getDatabase()
    {
        return database;
    }

    public void setDatabase(String database)
    {
        this.database = database;
    }
}
