package smpl.ordering.controllers;


import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import smpl.ordering.BadRequestException;
import smpl.ordering.OrderingInitializer;
import smpl.ordering.Utility;
import smpl.ordering.models.CatalogItem;
import smpl.ordering.models.Delivery;
import smpl.ordering.models.Order;
import smpl.ordering.models.Quote;
import smpl.ordering.models.OrderStatus;
import smpl.ordering.models.ShipmentEventInfo;
import smpl.ordering.models.ShipmentRecord;
import smpl.ordering.repositories.RepositoryFactory;
import smpl.ordering.repositories.ShipmentRepository;
import smpl.ordering.repositories.OrderRepository;
import smpl.ordering.repositories.QuoteRepository;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/shipments")
public class ShipmentController
{
    /**
     * Gets a list of existing shipments, regardless of status
     *
     * @return An HttpResponse containing a list of shipments.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getShipments(
            @RequestParam(value = "status", required = false, defaultValue = "None")
            OrderStatus status)
    {
        try
        {
            List<ShipmentRecord> shipments = getShipmentRepository().getShipments(status);
            if (shipments == null || shipments.size() == 0)
            {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            else
            {
                return new ResponseEntity<List<ShipmentRecord>>(shipments, HttpStatus.OK);
            }
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity<String>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/deliveries", method = RequestMethod.GET)
    public ResponseEntity getDeliveries()
    {
        try
        {
            List<ShipmentRecord> shipments = getShipmentRepository().getShipments(OrderStatus.DeliveryConfirmed);

            if (shipments == null || shipments.size() == 0)
            {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            else
            {
                List<Delivery> deliveries = new ArrayList<Delivery>();
                OrderRepository orderRepository = getOrderRepository();
                QuoteRepository quoteRepository = getQuoteRepository();

                for (int n = 0; n < shipments.size(); n++) {
                    Delivery delivery = new Delivery();

                    ShipmentRecord shipment = shipments.get(n);
                    delivery.setShipmentRecord(shipment);

                    Order order = orderRepository.getOrder(shipment.getOrderId());
                    delivery.setOrder(order);

                    Quote quote = quoteRepository.getQuote(order.getQuoteId());
                    delivery.setQuote(quote);

                    deliveries.add(delivery);
                }

                return new ResponseEntity<List<Delivery>>(deliveries, HttpStatus.OK);
            }
        }
        catch (Exception exc)
        {
            return new ResponseEntity<String>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets a specific shipment by its corresponding order id.
     *
     * @param id The order id
     * @return An HttpResponse containing a shipment record.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity getShipment(@PathVariable String id)
    {

        try
        {
            ShipmentRecord sr = getShipmentRepository().getShipmentById(id);
            if (sr == null)
            {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            else
            {
                return new ResponseEntity<ShipmentRecord>(sr, HttpStatus.OK);
            }
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity<String>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Creates a shipment record
     *
     * @param info Information about the SKU
     * @return An HTTP status code.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createShipmentRecord(@RequestBody ShipmentRecord info)
    {
        String errorMsg = info.validate();
        if (errorMsg != null)
        {
            return new ResponseEntity<String>(errorMsg, HttpStatus.BAD_REQUEST);
        }

        try
        {
            ShipmentRecord sr = getShipmentRepository().getShipmentById(info.getOrderId());
            if (sr != null)
            {
                return new ResponseEntity<String>("A shipment record already exists", HttpStatus.CONFLICT);
            }

            boolean result = getShipmentRepository().createShipment(info) != null;
            String applicationPath = OrderingInitializer.getApplicationPath();
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", applicationPath + "/shipments/" + info.getOrderId());
            return new ResponseEntity(responseHeaders, result ? HttpStatus.CREATED : HttpStatus.NOT_FOUND);
        }
        catch (BadRequestException bre)
        {
            return new ResponseEntity<String>(bre.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity<String>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates a shipment record
     *
     * @param id  The order id
     * @param record A shipment record
     * @return An HTTP status code.
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public ResponseEntity updateShipment(@PathVariable String id, @RequestBody ShipmentRecord record)
    {
        String errorMsg = record.validate();
        if (errorMsg != null)
        {
            return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
        }

        if (!id.equals(record.getOrderId()))
        {
            return new ResponseEntity<>("mismatched ids", HttpStatus.BAD_REQUEST);
        }

        try
        {
            ShipmentRecord sr = getShipmentRepository().getShipmentById(id);
            if (sr == null)
            {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

            getShipmentRepository().updateShipment(record);
            return new ResponseEntity(HttpStatus.OK);
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity<>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates a shipment record with a new event.
     *
     * @param id  The order id
     * @param event A shipment event record
     * @return An HTTP status code.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/events")
    public ResponseEntity addEvent(@PathVariable String id, @RequestBody ShipmentEventInfo event)
    {
        String errorMsg = event.validate();
        if (errorMsg != null)
        {
            return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
        }

        try
        {
            ShipmentRecord sr = getShipmentRepository().getShipmentById(id);
            if (sr == null)
            {
                return new ResponseEntity<ShipmentRecord>(HttpStatus.NOT_FOUND);
            }

            event.setDate(DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()));

            boolean result = getShipmentRepository().addEvent(id, event);
            return new ResponseEntity(result ? HttpStatus.OK : HttpStatus.CREATED);
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity<>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Removes an existing shipment from the system.
     *
     * @param orderId The order id.
     * @return An HTTP status code
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{orderId}")
    public ResponseEntity deleteShipment(@PathVariable String orderId)
    {
        try
        {
            boolean ok = getShipmentRepository().removeShipment(orderId, null);
            return new ResponseEntity(ok ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity<>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ShipmentRepository getShipmentRepository()
    {
        return RepositoryFactory.getShipmentRepository();
    }

    private OrderRepository getOrderRepository()
    {
        return RepositoryFactory.getOrderRepository();
    }

    private QuoteRepository getQuoteRepository()
    {
        return RepositoryFactory.getQuoteRepository();
    }
}
