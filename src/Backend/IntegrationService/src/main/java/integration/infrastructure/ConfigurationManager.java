package integration.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The ConfigurationManager class is responsible for wrapping calls to the application properties.
 */
public class ConfigurationManager {

    public ConfigurationManager()
    {
    }

    public static String getAzureStorageConnectionString() {
        return ConfigurationHelpers.getString("azure.storage.connectionstring");
    }

    public static String getMrpEndpoint() {
        return ConfigurationHelpers.getString("mrp.endpoint");
    }

    public static String getAzureOrderQueue() {
        return ConfigurationHelpers.getString("azure.storage.queue.orders");
    }

    public static String getAzureInventoryQueue(){
        return ConfigurationHelpers.getString("azure.storage.queue.inventory");
    }

    public static int getAzureQueueTimeout() {
        return ConfigurationHelpers.getInt("azure.storage.queue.message");
    }

}
