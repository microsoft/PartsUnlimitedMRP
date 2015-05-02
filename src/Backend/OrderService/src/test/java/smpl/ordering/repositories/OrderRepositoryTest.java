package smpl.ordering.repositories;

import smpl.ordering.ConflictingRequestException;
import smpl.ordering.TestPath;
import smpl.ordering.models.*;

import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("EmptyCatchBlock")
public class OrderRepositoryTest
{
    public void setUp() throws Exception
    {
        // Set up some data for the order tests to access.

        DealersRepository dealers = RepositoryFactory.getDealersRepository();
        ((TestPath)dealers).reset();

        dealers.upsertDealer(DealersRepositoryTest.createDealer("DLR-1"), null);
        dealers.upsertDealer(DealersRepositoryTest.createDealer("DLR-2"), null);
        dealers.upsertDealer(DealersRepositoryTest.createDealer("DLR-3"), null);
        dealers.upsertDealer(DealersRepositoryTest.createDealer("DLR-4"), null);

        CatalogItemsRepository catalog = RepositoryFactory.getCatalogItemsRepository();
        ((TestPath)catalog).reset();

        catalog.upsertCatalogItem("ACC-0001", new CatalogItem("ACC-0001", "Shelving", 10.50, 4, 3), null);
        catalog.upsertCatalogItem("ACC-0002", new CatalogItem("ACC-0002", "Refrigeration Unit", 2500, 2, 7), null);
        catalog.upsertCatalogItem("ACC-0003", new CatalogItem("ACC-0003", "Freezer Unit", 4500, 4, 5), null);

        QuoteRepository quotes = RepositoryFactory.getQuoteRepository();
        ((TestPath)quotes).reset();
        repository = RepositoryFactory.getOrderRepository();
        ((TestPath)repository).reset();

        for (int i = 0; i < 10; ++i)
        {
            Quote template = quotes.createQuote(QuoteRepositoryTest.createQuote(String.format("quote-%s", i)));
            repository.createOrder(template.getQuoteId());
        }
    }

    public void testHasOrder()
    {
        assertTrue(repository.hasOrder("order-quote-0"));
        assertFalse(repository.hasOrder("order-not-there"));
    }

    public void testGetOrder()
    {
        Order o1 = repository.getOrder("order-quote-0");
        assertNotNull(o1);
        assertNull(repository.getOrder("order-not-there"));

        String qid = o1.getQuoteId();
        assertEquals("quote-0", qid);
    }

    public void testGetOrdersByQuoteId()
    {
        Order order = repository.getOrderByQuoteId("quote-100");
        assertNull(order);

        order = repository.getOrderByQuoteId("quote-1");
        assertNotNull(order);
        assertEquals("quote-1", order.getQuoteId());
    }

    public void testGetOrdersByStatus()
    {
        List<Order> orders = repository.getOrdersByStatus(OrderStatus.Delivered);
        assertNotNull(orders);
        assertEquals(0, orders.size());

        orders = repository.getOrdersByStatus(OrderStatus.Created);
        assertNotNull(orders);
        assertEquals(10, orders.size());
    }


    public void testGetOrdersByDealerName()
    {
        List<Order> orders = repository.getOrdersByDealerName("DLR-1", OrderStatus.Created);
        assertNotNull(orders);
        assertEquals(10, orders.size());

        orders = repository.getOrdersByDealerName("DLR-100", OrderStatus.Created);
        assertNotNull(orders);
        assertEquals(0, orders.size());

        orders = repository.getOrdersByDealerName("DLR-1", OrderStatus.Confirmed);
        assertNotNull(orders);
        assertEquals(0, orders.size());
    }

    public void testCreateOrder() throws Exception
    {
        QuoteRepository quotes = RepositoryFactory.getQuoteRepository();

        Quote template = quotes.createQuote(QuoteRepositoryTest.createQuote("quote-4711"));
        Order o1 = repository.createOrder(template.getQuoteId());

        assertNotNull(o1);
        assertEquals(o1.getQuoteId(), "quote-4711");
        assertEquals(0, o1.getEvents().size());
        assertEquals(OrderStatus.Created, o1.getStatus());

        List<Order> orders = repository.getOrdersByDealerName("DLR-1", OrderStatus.Created);
        assertNotNull(orders);
        assertEquals(11, orders.size());

        try {
            // Duplicate orders...
            assertNull(repository.createOrder(template.getQuoteId()));
        } catch (ConflictingRequestException bre) { }
    }

    public void testUpdateOrder() throws Exception
    {
        QuoteRepository quotes = RepositoryFactory.getQuoteRepository();

        Quote template = quotes.createQuote(QuoteRepositoryTest.createQuote("quote-4712"));
        Order o1 = repository.createOrder(template.getQuoteId());
        assertNotNull(o1);
        assertEquals(o1.getQuoteId(), "quote-4712");
        assertEquals(0, o1.getEvents().size());
        assertEquals(OrderStatus.Created, o1.getStatus());

        String orderId = o1.getOrderId();

        OrderEventInfo info = new OrderEventInfo();
        info.setDate("1/1/2001");
        info.setComments("This is a unit test");
        o1.addEvent(info);
        repository.updateOrder(o1.getOrderId(), o1, null);

        Order o2 = repository.getOrder(orderId);
        assertNotNull(o2);
        assertEquals(o2.getQuoteId(), "quote-4712");
        assertEquals(1, o2.getEvents().size());

        info = o2.getEvents().get(0);
        assertEquals("This is a unit test", info.getComments());
        assertEquals("1/1/2001", info.getDate());
    }

    public void testUpdateOrder1() throws Exception
    {
        QuoteRepository quotes = RepositoryFactory.getQuoteRepository();

        Quote template = quotes.createQuote(QuoteRepositoryTest.createQuote("quote-4713"));
        Order o1 = repository.createOrder(template.getQuoteId());
        assertNotNull(o1);
        assertEquals(o1.getQuoteId(), "quote-4713");
        assertEquals(0, o1.getEvents().size());
        assertEquals(OrderStatus.Created, o1.getStatus());

        String orderId = o1.getOrderId();

        OrderEventInfo info = new OrderEventInfo();
        info.setDate("1/2/2001");
        info.setComments("This is a unit test");
        OrderUpdateInfo update = new OrderUpdateInfo();
        update.setStatus(OrderStatus.Confirmed);
        update.setEventInfo(info);
        repository.updateOrder(o1.getOrderId(), update, null);

        Order o2 = repository.getOrder(orderId);
        assertNotNull(o2);
        assertEquals(o2.getQuoteId(), "quote-4713");
        assertEquals(1, o2.getEvents().size());
        assertEquals(OrderStatus.Confirmed, o2.getStatus());

        info = o2.getEvents().get(0);
        assertEquals("This is a unit test", info.getComments());
        assertEquals("1/2/2001", info.getDate());
    }

    private OrderRepository repository;
}
