package smpl.ordering.controllers;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import smpl.ordering.BadRequestException;
import smpl.ordering.ConflictingRequestException;
import smpl.ordering.OrderingInitializer;
import smpl.ordering.Utility;
import smpl.ordering.models.*;
import smpl.ordering.repositories.OrderRepository;
import smpl.ordering.repositories.QuoteRepository;
import smpl.ordering.repositories.RepositoryFactory;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController
{
    /**
     * Gets an order identified by its id.
     *
     * @param orderId The order id
     * @return An HttpResponse containing the quote, if found.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{orderId}")
    public ResponseEntity getOrderById(@PathVariable String orderId)
    {
        try
        {
            Order o = getOrders().getOrder(orderId);
            if (o == null)
            {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            else
            {
                return new ResponseEntity<>(o, HttpStatus.OK);
            }
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets a list of orders for a given dealer.
     *
     * @param dealer The dealer name.
     * @return An HttpResponse containing the quotes, if found.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getOrdersByDealerName(
            @RequestParam(value = "dealer", required = false, defaultValue = "") String dealer,
            @RequestParam(value = "status", required = false, defaultValue = "None") OrderStatus status)
    {
        try
        {
            List<Order> o;
            if (dealer.length() == 0) {
                o = getOrders().getOrdersByStatus(status);
            }
            else {
                o = getOrders().getOrdersByDealerName(dealer, status);
            }
            if (o == null || o.size() == 0)
            {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            else
            {
                return new ResponseEntity<>(o, HttpStatus.OK);
            }
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a new order.
     *
     * @param from The id of the quote from which this order will be created.
     * @return An HttpResponse containing the quote.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createOrder(@RequestParam(value = "fromQuote") String from)
    {
        try
        {
            Quote quote = getQuotes().getQuote(from);

            if (quote != null)
            {
                Order order = getOrders().createOrder(from);
                String applicationPath = OrderingInitializer.getApplicationPath();
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("Location", applicationPath + "/orders/" + order.getOrderId());
                return new ResponseEntity<>(order, responseHeaders, HttpStatus.CREATED);
            }
            else
            {
                return new ResponseEntity<>("There is no such quote", HttpStatus.BAD_REQUEST);
            }
        }
        catch (ConflictingRequestException bre)
        {
            return new ResponseEntity<>(bre.getMessage(), HttpStatus.CONFLICT);
        }
        catch (BadRequestException bre)
        {
            return new ResponseEntity<>(bre.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a new order.
     *
     * @return An HttpResponse containing the quote.
     */
    @RequestMapping(method = RequestMethod.POST, value = "/{orderId}/events")
    public ResponseEntity addEvent(@PathVariable String orderId,
                                   @RequestBody OrderEventInfo info)
    {
        try
        {
            Order order = getOrders().getOrder(orderId);
            if (order != null)
            {
                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                info.setDate(df.format(new Date()));
                order.addEvent(info);
                getOrders().updateOrder(order.getOrderId(), order, null);
                return new ResponseEntity(HttpStatus.CREATED);
            }
            else
            {
                return new ResponseEntity<>("There is no such order", HttpStatus.BAD_REQUEST);
            }
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an order.
     *
     * @return An HttpResponse containing the quote.
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{orderId}")
    public ResponseEntity updateOrder(@PathVariable String orderId,
                                   @RequestBody Order order)
    {
        // Input data validation
        String errorMsg = order.validate();
        if (errorMsg != null)
        {
            return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
        }

        try
        {
            boolean ok = getOrders().updateOrder(orderId, order, null);
            return new ResponseEntity(ok ? HttpStatus.OK : HttpStatus.NOT_FOUND);
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
     * Creates a new order.
     *
     * @return An HttpResponse containing the quote.
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{orderId}/status")
    public ResponseEntity updateStatus(@PathVariable String orderId,
                                       @RequestBody OrderUpdateInfo info)
    {
        try
        {
            if (getOrders().hasOrder(orderId))
            {
                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                info.getEventInfo().setDate(df.format(new Date()));
                getOrders().updateOrder(orderId, info, null);
                return new ResponseEntity(HttpStatus.OK);
            }
            else
            {
                return new ResponseEntity<>("There is no such order", HttpStatus.BAD_REQUEST);
            }
        }
        catch (BadRequestException bre)
        {
            return new ResponseEntity<>(bre.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Removes an existing order from the system.
     *
     * @param orderId The order id.
     * @return An HTTP status code
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{orderId}")
    public ResponseEntity deleteOrder(@PathVariable String orderId)
    {
        try
        {
            boolean ok = getOrders().removeOrder(orderId, null);
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

    private QuoteRepository getQuotes()
    {
        return RepositoryFactory.getQuoteRepository();
    }

    private OrderRepository getOrders()
    {
        return RepositoryFactory.getOrderRepository();
    }
}
