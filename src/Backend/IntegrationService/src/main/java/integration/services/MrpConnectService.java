package integration.services;

import integration.models.website.OrderMessage;
import integration.models.mrp.CatalogItem;
import integration.models.mrp.Order;
import integration.models.mrp.Quote;
import integration.models.mrp.ShipmentRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This service is responsible for communication to the MRP system.
 */
public class MrpConnectService {

    private final static Logger log = LoggerFactory.getLogger(MrpConnectService.class);
    private String hostName;
    private RestTemplate restTemplate;

    public MrpConnectService(String hostName) {
        this.hostName = hostName;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Crates a new Quote, Order and Shipment in the MRP system
     * @param message the new order message from website
     * @throws MalformedURLException
     */
    public void createNewOrder(OrderMessage message) throws MalformedURLException {
        Quote quote = createQuote(message);
        log.info("Quote Created. Quote Id : " + quote.getQuoteId());
        Order order = createOrder(quote.getQuoteId());
        log.info("Order Created. Quote Id : " + quote.getQuoteId() + "  Order Id : " + order.getOrderId());
        createShipment(message, order.getOrderId());
        log.info("Shipment Created. Quote Id : " + quote.getQuoteId() + "  Order Id : " + order.getOrderId());
    }

    /**
     * Uses the customer address to build a shipment for the orderId.
     * @param message - Website Order
     * @param orderId - Id of order created in the MRP system
     * @throws MalformedURLException
     */
    private void createShipment(OrderMessage message, String orderId) throws MalformedURLException {
        String uri = UriComponentsBuilder.fromUriString(hostName).path("shipments").build().toUriString();
        ShipmentRecord newShipmentRecord = new ShipmentRecord(message, orderId);
        restTemplate.postForObject(uri, newShipmentRecord, ShipmentRecord.class);
    }

    /**
     * Posts the quote id to the MRP system to confirm the order.
     * @param quoteId - Id of the quote that you wish to fulfill.
     * @return Order - the newly created order including the id created in the MRP system.
     */
    private Order createOrder(String quoteId) {
        String uri = UriComponentsBuilder.fromUriString(hostName).path("orders").queryParam("fromQuote", quoteId).build().toUriString();
        Order createdOrder = restTemplate.postForObject(uri, null, Order.class);
        return createdOrder;
    }

    /**
     * Takes all of the information about the order to create the inital quote.
     * @param message - Website Order
     * @return Quote - the newly created quote including the id created in the MRP system.
     */
    private Quote createQuote(OrderMessage message) {
        String uri = UriComponentsBuilder.fromUriString(hostName).path("quotes").build().toUriString();
        Quote newQuote = new Quote(message);
        Quote createdQuote = restTemplate.postForObject(uri, newQuote, Quote.class);
        return createdQuote;
    }

    /**
     * Queries the MRP system for the catalog.
     * @return List<CatalogItem> - list of all catalog items in the MRP system.
     */
    public List<CatalogItem> getCatalogItems() {
        String uri = UriComponentsBuilder.fromUriString(hostName).pathSegment("catalog").build().toUriString();

        ArrayList<CatalogItem> CatalogList = new ArrayList<CatalogItem>();
        for (CatalogItem catalog : restTemplate.getForObject(uri, CatalogItem[].class)){
            CatalogList.add(catalog);
        }
        return CatalogList;
    }
}