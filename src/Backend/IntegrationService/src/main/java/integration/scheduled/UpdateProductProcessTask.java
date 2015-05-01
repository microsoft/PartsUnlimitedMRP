package integration.scheduled;

import integration.Constants;
import integration.infrastructure.ConfigurationManager;
import integration.models.mrp.CatalogItem;
import integration.models.website.ProductMessage;
import integration.services.MrpConnectService;
import integration.services.QueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Scheduled tasks for querying the MRP system for catalog items and sending an update to the website.
 */
@EnableScheduling
public class UpdateProductProcessTask {
    private final static Logger log = LoggerFactory.getLogger(UpdateProductProcessTask.class);

    @Scheduled(fixedDelay = Constants.SCHEDULED_INTERVAL)
    public void scheduledTask() {
        try{
            MrpConnectService mrpService = new MrpConnectService(ConfigurationManager.getMrpEndpoint());
            List<CatalogItem> catalogItems = mrpService.getCatalogItems();

            if (catalogItems != null && !catalogItems.isEmpty()){
                ProductMessage message = new ProductMessage(catalogItems);
                QueueService queueService = new QueueService(ConfigurationManager.getAzureInventoryQueue(), ProductMessage.class);
                queueService.addQueueMessage(message);
            }
        }
        catch (Exception ex)
        {
            log.error("Exception thrown while processing catalog item inventory :" + ex.toString());
        }
    }
}
