package smpl.ordering.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import smpl.ordering.TestPath;
import smpl.ordering.models.*;
import smpl.ordering.repositories.QuoteRepositoryTest;
import smpl.ordering.repositories.RepositoryFactory;
import smpl.ordering.repositories.ShipmentRepositoryTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("unchecked")
public class ShipmentControllerTest
{
    @Before
    public void setUp() throws Exception
    {
        // Make sure we're using memory-based repositories.
        RepositoryFactory.reset("memory");

        // Make sure the repositories are empty.
        ((TestPath) RepositoryFactory.getDealersRepository()).reset();
        ((TestPath) RepositoryFactory.getCatalogItemsRepository()).reset();
        ((TestPath) RepositoryFactory.getQuoteRepository()).reset();
        ((TestPath) RepositoryFactory.getOrderRepository()).reset();
        ((TestPath) RepositoryFactory.getShipmentRepository()).reset();

        quotes = new QuoteController();
        orders = new OrderController();
        controller = new ShipmentController();

    }

    @Test
    public void testCreateShipment()
    {
        ShipmentRecord info = ShipmentRepositoryTest.createShipmentRecord("order-quote-4711");

        ResponseEntity response = controller.createShipmentRecord(info);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        createQuoteAndOrder("quote-4711");

        response = controller.createShipmentRecord(info);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void testGetAllShipments()
    {
        Order order = createQuoteAndOrder("quote-4711");

        ResponseEntity response = controller.createShipmentRecord(ShipmentRepositoryTest.createShipmentRecord(order.getOrderId()));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.getShipments(OrderStatus.None);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<ShipmentRecord> list = ((ResponseEntity<List<ShipmentRecord>>) response).getBody();
        assertNotNull(list);
        assertEquals(1, list.size());
        assertNotNull(list.get(0).getEvents());
        assertEquals(order.getOrderId(), list.get(0).getOrderId());
    }

    @Test
    public void testGetShipmentsByStatus()
    {
        Order order = createQuoteAndOrder("quote-4711");

        ResponseEntity response = controller.createShipmentRecord(ShipmentRepositoryTest.createShipmentRecord(order.getOrderId()));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.getShipments(OrderStatus.Created);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<ShipmentRecord> list = ((ResponseEntity<List<ShipmentRecord>>) response).getBody();
        assertNotNull(list);
        assertEquals(1, list.size());
        assertNotNull(list.get(0).getEvents());
        assertEquals(order.getOrderId(), list.get(0).getOrderId());

        response = orders.updateStatus(order.getOrderId(), new OrderUpdateInfo(OrderStatus.Shipped, "The thing has shipped"));
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = controller.getShipments(OrderStatus.Shipped);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        list = ((ResponseEntity<List<ShipmentRecord>>) response).getBody();
        assertNotNull(list);
        assertEquals(1, list.size());
        assertNotNull(list.get(0).getEvents());
        assertEquals(order.getOrderId(), list.get(0).getOrderId());

        response = controller.getShipments(OrderStatus.Delivered);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateShipment()
    {
        Order order = createQuoteAndOrder("quote-4711");

        ShipmentRecord record = ShipmentRepositoryTest.createShipmentRecord(order.getOrderId());

        ResponseEntity response = controller.createShipmentRecord(record);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.getShipment(order.getOrderId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ShipmentRecord rec = ((ResponseEntity<ShipmentRecord>) response).getBody();
        assertNotNull(rec);
        assertNotNull(rec.getEvents());
        assertEquals(0, rec.getEvents().size());

        record.setContactName("John Jones");

        controller.updateShipment(record.getOrderId(), record);

        response = controller.getShipment(record.getOrderId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        rec = ((ResponseEntity<ShipmentRecord>) response).getBody();
        assertNotNull(rec);
        assertEquals(rec.getContactName(), "John Jones");
        assertNotNull(rec.getEvents());
        assertEquals(0, rec.getEvents().size());
    }

    @Test
    public void testAddEventToShipment()
    {
        Order order = createQuoteAndOrder("quote-4711");

        ShipmentRecord record = ShipmentRepositoryTest.createShipmentRecord(order.getOrderId());

        ResponseEntity response = controller.createShipmentRecord(record);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.getShipment(order.getOrderId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ShipmentRecord rec = ((ResponseEntity<ShipmentRecord>) response).getBody();
        assertNotNull(rec);
        assertNotNull(rec.getEvents());
        assertEquals(0, rec.getEvents().size());

        controller.addEvent(record.getOrderId(), new ShipmentEventInfo("12/13/2014", "The truck is on its way..."));

        response = controller.getShipment(record.getOrderId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        rec = ((ResponseEntity<ShipmentRecord>) response).getBody();
        assertNotNull(rec);
        assertNotNull(rec.getEvents());
        assertEquals(1, rec.getEvents().size());
        assertEquals("The truck is on its way...", rec.getEvents().get(0).getComments());
    }

    @SuppressWarnings("SameParameterValue")
    private Order createQuoteAndOrder(String id)
    {
        ResponseEntity response = quotes.createQuote(QuoteRepositoryTest.createQuote(id));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = orders.createOrder(id);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        return ((ResponseEntity<Order>) response).getBody();
    }


    private QuoteController quotes;
    private OrderController orders;
    private ShipmentController controller;
}
