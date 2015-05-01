package integration.scheduled;

import integration.Constants;
import integration.infrastructure.ConfigurationManager;
import integration.models.QueueResponse;
import integration.models.website.OrderMessage;
import integration.services.MrpConnectService;
import integration.services.QueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Scheduled task for retrieving any new orders from the queue and integrating them into the MRP system.
 */
@EnableScheduling
public class CreateOrderProcessTask {

    private final static Logger log = LoggerFactory.getLogger(CreateOrderProcessTask.class);

    @Scheduled(fixedDelay = Constants.SCHEDULED_INTERVAL)
    public void scheduledTask() {
        try {
            String endpoint = ConfigurationManager.getMrpEndpoint();
            MrpConnectService mrpService = new MrpConnectService(endpoint);
            QueueService queueService = new QueueService(ConfigurationManager.getAzureOrderQueue(), OrderMessage.class);
            QueueResponse<OrderMessage> response;

            while ((response = queueService.getQueueMessage()) != null) {
                log.info("Found queue message. MessageId :" + response.getQueueMessage().getMessageId());
                mrpService.createNewOrder(response.getResponseBody());
                log.info("Created new order in MRP system. MessageId :" + response.getQueueMessage().getMessageId());
                queueService.deleteQueueMessage(response.getQueueMessage());
                log.info("Message removed from queue. MessageId :" + response.getQueueMessage().getMessageId());
            }
        } catch (Exception ex) {
            log.error("Exception thrown while retrieving queue message :" + ex.toString());
        }
    }
}
