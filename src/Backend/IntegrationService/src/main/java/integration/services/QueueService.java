package integration.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import integration.infrastructure.ConfigurationManager;
import integration.models.QueueResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * The queue service responsible for resolving all calls to azure queues.
 * @param <T> T is the message type is used for the serialization and de-serialization.
 */
public class QueueService <T> {

    private final static Logger log = LoggerFactory.getLogger(MrpConnectService.class);
    private final String queueName;
    private Class<T> valueType;

    public QueueService(String queueName, Class<T> valueType) {

        this.queueName = queueName;
        this.valueType = valueType;
    }

    /**
     * Retrieve a message off the configured queue.
     * @return A queue response object that host a reference to the queue message and the de-serialized content.
     * If not queue message is found or the message is malformed, null will be returned.
     * @throws StorageException
     * @throws URISyntaxException
     * @throws InvalidKeyException
     */
    public QueueResponse getQueueMessage() throws StorageException, URISyntaxException, InvalidKeyException {
        CloudQueue queue = QueueFactory.getQueue(queueName);
        CloudQueueMessage message = queue.retrieveMessage(ConfigurationManager.getAzureQueueTimeout(), null /*options*/, null /*opContext*/);

        if (message == null) {
            return null;
        }

        // If a queue message is successfully retrieved de-serialize it and return.
        String messageString = message.getMessageContentAsString();
        try {
            ObjectMapper mapper = new ObjectMapper();
            T messageContent = mapper.readValue(messageString, this.valueType);
            QueueResponse response = new QueueResponse(message, messageContent);
            return response;
        } catch (IOException ex) {
            log.error("An error occurred de-serializing queue message to OrderMessage. Serialized Object : " + messageString);
            deleteQueueMessage(message);
            return null;
        }
    }

    /**
     * Delete a processed message off the configured queue
     * @param message - The cloud queue message that is returned during retrieval.
     * @throws StorageException
     * @throws URISyntaxException
     * @throws InvalidKeyException
     */
    public void deleteQueueMessage(CloudQueueMessage message) throws StorageException, URISyntaxException, InvalidKeyException {
        CloudQueue queue = QueueFactory.getQueue(queueName);
        queue.deleteMessage(message);
    }

    /**
     * Serialize and add a object to the configured queue.
     * @param queueContent - the object to add to the queue.
     * @throws StorageException
     * @throws URISyntaxException
     * @throws InvalidKeyException
     * @throws JsonProcessingException
     */
    public void addQueueMessage(T queueContent) throws StorageException, URISyntaxException, InvalidKeyException, JsonProcessingException {
        CloudQueue queue = QueueFactory.getQueue(queueName);
        ObjectMapper mapper = new ObjectMapper();
        byte[] queueByteContent = mapper.writeValueAsBytes(queueContent);
        CloudQueueMessage message = new CloudQueueMessage(queueByteContent);
        queue.addMessage(message);
    }
}
