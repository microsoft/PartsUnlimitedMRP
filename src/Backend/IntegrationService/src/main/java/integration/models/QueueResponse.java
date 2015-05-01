package integration.models;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import java.io.IOException;

/**
 * This class is responsible for wrapping responses from the queue service.
 * @param <T>
 */
public class QueueResponse <T> {

    private final CloudQueueMessage queueMessage;
    private final T responseBody;

    public QueueResponse(CloudQueueMessage queueMessage, T responseBody){
        this.queueMessage = queueMessage;
        this.responseBody = responseBody;
    }

    public CloudQueueMessage getQueueMessage() {
        return queueMessage;
    }

    public T getResponseBody() throws StorageException, IOException {
        return responseBody;
    }
}
