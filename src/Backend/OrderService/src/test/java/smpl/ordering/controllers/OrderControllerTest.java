package smpl.ordering.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import smpl.ordering.TestPath;
import smpl.ordering.models.*;
import smpl.ordering.repositories.QuoteRepositoryTest;
import smpl.ordering.repositories.RepositoryFactory;

import java.net.URI;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class OrderControllerTest
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

        quotes = new QuoteController();
        controller = new OrderController();
    }

    @Test
    public void testCreateOrder() throws Exception
    {
        ResponseEntity response = quotes.createQuote(QuoteRepositoryTest.createQuote("quote-4711"));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.createOrder("quote-4711");
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Order order = ((ResponseEntity<Order>) response).getBody();
        assertEquals("quote-4711", order.getQuoteId());
        HttpHeaders headers = response.getHeaders();
        assertEquals(new URI("/orders/" + order.getOrderId()), headers.getLocation());

        response = controller.getOrderById(order.getOrderId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("quote-4711", order.getQuoteId());
    }

    @Test
    public void testGetOrderById() throws Exception
    {
        ResponseEntity response = controller.getOrderById("order-not-there");
        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        response = quotes.createQuote(QuoteRepositoryTest.createQuote("quote-4711"));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.createOrder("quote-4711");
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Order refOrder = ((ResponseEntity<Order>) response).getBody();
        assertNotNull(refOrder);

        response = controller.getOrderById(refOrder.getOrderId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Order order = ((ResponseEntity<Order>) response).getBody();
        assertNotNull(order);

        assertEquals(refOrder, order);
    }

    @Test
    public void testGetQuotesByDealerName() throws Exception
    {
        ResponseEntity response = controller.getOrdersByDealerName("DLR-1", OrderStatus.None);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        List<Order> orders = ((ResponseEntity<List<Order>>) response).getBody();
        assertNull(orders);

        response = quotes.createQuote(QuoteRepositoryTest.createQuote("quote-4711"));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.createOrder("quote-4711");
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Order refOrder = ((ResponseEntity<Order>) response).getBody();
        assertNotNull(refOrder);

        response = controller.getOrdersByDealerName("DLR-1", OrderStatus.Created);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        orders = ((ResponseEntity<List<Order>>) response).getBody();
        assertNotNull(orders);
        assertEquals(1, orders.size());

        assertEquals(refOrder, orders.get(0));

        // Case insensitive.

        response = controller.getOrdersByDealerName("dlr-1", OrderStatus.Created);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // optional dealer
        response = controller.getOrdersByDealerName("", OrderStatus.Created);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // optional dealer, status
        response = controller.getOrdersByDealerName("", OrderStatus.None);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddEvent() throws Exception
    {
        ResponseEntity response = quotes.createQuote(QuoteRepositoryTest.createQuote("quote-4711"));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.createOrder("quote-4711");
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Order refOrder = ((ResponseEntity<Order>) response).getBody();
        assertNotNull(refOrder);

        response = controller.addEvent(refOrder.getOrderId(), new OrderEventInfo("testAddEvent-1"));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.addEvent(refOrder.getOrderId(), new OrderEventInfo("testAddEvent-2"));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.addEvent(refOrder.getOrderId(), new OrderEventInfo("1/1/2000", "testAddEvent-3"));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.getOrderById(refOrder.getOrderId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Order order = ((ResponseEntity<Order>) response).getBody();
        assertNotNull(order);
        assertNotNull(order.getEvents());
        assertEquals(3, order.getEvents().size());

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        String today = df.format(new Date());

        for (int i = 0; i < order.getEvents().size(); ++i)
        {
            assertEquals(today, order.getEvents().get(i).getDate());
            assertTrue(order.getEvents().get(i).getComments().startsWith("testAddEvent"));
        }
    }

    @Test
    public void testUpdate() throws Exception
    {
        ResponseEntity response = quotes.createQuote(QuoteRepositoryTest.createQuote("quote-4711"));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.createOrder("quote-4711");
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Order refOrder = ((ResponseEntity<Order>) response).getBody();
        assertNotNull(refOrder);

        response = controller.updateStatus(refOrder.getOrderId(), new OrderUpdateInfo(OrderStatus.Confirmed, "testUpdate-1"));
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = controller.updateStatus(refOrder.getOrderId(), new OrderUpdateInfo(OrderStatus.Started, "testUpdate-2"));
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = controller.updateStatus(refOrder.getOrderId(), new OrderUpdateInfo(OrderStatus.Built, "testUpdate-3"));
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = controller.updateStatus(refOrder.getOrderId(), new OrderUpdateInfo(OrderStatus.Shipped, "testUpdate-4"));
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = controller.getOrderById(refOrder.getOrderId());
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Order order = ((ResponseEntity<Order>) response).getBody();
        assertNotNull(order);
        assertNotNull(order.getEvents());
        assertEquals(4, order.getEvents().size());

        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        String today = df.format(new Date());

        for (int i = 0; i < order.getEvents().size(); ++i)
        {
            assertEquals(today, order.getEvents().get(i).getDate());
            assertTrue(order.getEvents().get(i).getComments().startsWith("testUpdate"));
        }
    }

    private QuoteController quotes;
    private OrderController controller;
}
