package integration.services;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import integration.infrastructure.ConfigurationManager;

import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The queue factory is responsible for initializing and locating the queues a thread safe manner.
 */
public class QueueFactory {

    private static Map<String, CloudQueue> s_queueDictionary;

    /**
     * get queue will return the named queue, initializing a new queue in the case it does not already exist.
     * @param queueKey - The name of the cloud queue to search for.
     * @return com.microsoft.azure.storage.queue.CloudQueue
     * @throws StorageException
     * @throws URISyntaxException
     * @throws InvalidKeyException
     */
    public static CloudQueue getQueue(String queueKey) throws StorageException, URISyntaxException, InvalidKeyException {
        if (s_queueDictionary == null) {
            // http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ConcurrentHashMap.html
            // Ensuring if we are thread safe for setting and getting cloud queues.
            s_queueDictionary = new ConcurrentHashMap();
        }

        if (s_queueDictionary.containsKey(queueKey)) {
            return s_queueDictionary.get(queueKey);
        } else {
            // Retrieve storage account from connection-string.
            CloudStorageAccount storageAccount = CloudStorageAccount.parse(ConfigurationManager.getAzureStorageConnectionString());
            // Create the queue client.
            CloudQueueClient queueClient = storageAccount.createCloudQueueClient();
            // Retrieve a reference to a queue.
            CloudQueue queue = queueClient.getQueueReference(queueKey);
            // Create the queue if it doesn't already exist.
            queue.createIfNotExists();

            s_queueDictionary.put(queueKey, queue);
            return queue;
        }
    }
}
