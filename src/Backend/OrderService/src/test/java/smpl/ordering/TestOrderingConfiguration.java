package smpl.ordering;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.TelemetryConfiguration;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import smpl.ordering.repositories.RepositoryFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
@ComponentScan
@EnableAutoConfiguration
public class TestOrderingConfiguration
        implements ApplicationContextAware
{
    public
    @Bean
    MongoTemplate mongoTemplate() throws Exception
    {
        Properties props = PropertyHelper.getProperties();

        MongoClientOptions.Builder options = MongoClientOptions.builder();
        options.socketKeepAlive(false);

        // Compared to the product configuration, this is a bit primitive, but trying to autowire the unit test
        // configuration turned out to be rather complicated, so getting properties manually is a better
        // way to go.
        String mongoHost = props.getProperty("mongodb.host");
        String mongoPort = System.getenv("MONGO_PORT"); // Anticipating use within a docker container.

        if (!Utility.isNullOrEmpty(mongoPort))
        {
            URL portUrl = new URL(mongoPort.replace("tcp:", "http:"));
            mongoHost = portUrl.getHost();
        }

        String mongoDB = props.getProperty("mongodb.database");

        if (s_mongoClient == null)
        {
            if (mongoDB != null && !mongoDB.isEmpty() && mongoHost != null && !mongoHost.isEmpty())
            {
                List<ServerAddress> hosts = new ArrayList<>();
                for (String host : mongoHost.split(","))
                {
                    hosts.add(new ServerAddress(host));
                }
                s_mongoClient = new MongoClient(hosts, options.build());

            }
            else
            {
                s_mongoClient = new MongoClient();
            }
        }

        return new MongoTemplate(s_mongoClient, mongoDB);
    }

    public
    @Bean
    TelemetryClient getTelemetryClient()
    {
        if (TelemetryConfiguration.getActive() == null)
        {
            return null;
        }

        TelemetryClient client = new TelemetryClient(TelemetryConfiguration.getActive());
        return client;
    }

    public
    @Bean
    RepositoryFactory repositoryFactory()
    {
        // See comments about manually getting properties earlier in the file.
        Properties props = PropertyHelper.getProperties();
        String storage = props.getProperty("ordering.storage");
        RepositoryFactory.reset(storage);
        return RepositoryFactory.getFactory();
    }

    private static ApplicationContext applicationContext;

    private static MongoClient s_mongoClient;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException
    {
        applicationContext = context;
    }

    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
}
