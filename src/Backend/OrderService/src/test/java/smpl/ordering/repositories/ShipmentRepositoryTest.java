package smpl.ordering.repositories;

import smpl.ordering.BadRequestException;
import smpl.ordering.TestPath;
import smpl.ordering.models.*;

import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("EmptyCatchBlock")
public class ShipmentRepositoryTest
{
    public void setUp() throws Exception
    {
        // Set up some data for the order tests to access.

        DealersRepository dealers = RepositoryFactory.getDealersRepository();
        ((TestPath) dealers).reset();

        dealers.upsertDealer(DealersRepositoryTest.createDealer("DLR-1"), null);
        dealers.upsertDealer(DealersRepositoryTest.createDealer("DLR-2"), null);
        dealers.upsertDealer(DealersRepositoryTest.createDealer("DLR-3"), null);
        dealers.upsertDealer(DealersRepositoryTest.createDealer("DLR-4"), null);

        CatalogItemsRepository catalog = RepositoryFactory.getCatalogItemsRepository();
        ((TestPath) catalog).reset();

        catalog.upsertCatalogItem("ACC-0001", new CatalogItem("ACC-0001", "Shelving", 10.50, 4, 3), null);
        catalog.upsertCatalogItem("ACC-0002", new CatalogItem("ACC-0002", "Refrigeration Unit", 2500, 2, 7), null);
        catalog.upsertCatalogItem("ACC-0003", new CatalogItem("ACC-0003", "Freezer Unit", 4500, 4, 5), null);

        QuoteRepository quotes = RepositoryFactory.getQuoteRepository();
        ((TestPath) quotes).reset();
        orders = RepositoryFactory.getOrderRepository();
        ((TestPath) orders).reset();

        shipments = RepositoryFactory.getShipmentRepository();
        ((TestPath) shipments).reset();

        for (int i = 0; i < 10; ++i)
        {
            Quote template = quotes.createQuote(QuoteRepositoryTest.createQuote(String.format("quote-%s", i)));
            Order order = orders.createOrder(template.getQuoteId());
            if (i < 5)
            {
                shipments.createShipment(createShipmentRecord(order.getOrderId()));
                order.setStatus(OrderStatus.Shipped);
                orders.updateOrder(order.getOrderId(), order, null);
            }
        }
    }

    public void testGetShipments()
    {
        List<ShipmentRecord> records = shipments.getShipments(OrderStatus.None);
        assertNotNull(records);
        assertEquals(5, records.size());

        records = shipments.getShipments(OrderStatus.Delivered);
        assertNotNull(records);
        assertEquals(0, records.size());

        records = shipments.getShipments(OrderStatus.Shipped);
        assertNotNull(records);
        assertEquals(5, records.size());
    }

    public void testGetShipmentById()
    {
        ShipmentRecord record = shipments.getShipmentById("order-quote-0");
        assertNotNull(record);
        assertNotNull(record.getEvents());
        assertEquals(0, record.getEvents().size());
    }

    public void testCreateShipment() throws BadRequestException
    {
        List<ShipmentRecord> records = shipments.getShipments(OrderStatus.None);
        assertNotNull(records);
        assertEquals(5, records.size());

        try
        {
            // Duplicate shipment record
            Order o = orders.getOrder("order-quote-0");
            shipments.createShipment(createShipmentRecord(o.getOrderId()));
            fail("Should have seen an exception");
        }
        catch (BadRequestException bre)
        {
        }

        Order o = orders.getOrder("order-quote-5");
        ShipmentRecord newShipment = shipments.createShipment(createShipmentRecord(o.getOrderId()));
        assertNotNull(newShipment);
        assertEquals(o.getOrderId(), newShipment.getOrderId());

        records = shipments.getShipments(OrderStatus.None);
        assertNotNull(records);
        assertEquals(6, records.size());
    }

    public void testUpdateShipment()
    {
        ShipmentRecord record = shipments.getShipmentById("order-quote-0");
        assertNotNull(record);
        assertNotNull(record.getEvents());
        assertEquals(0, record.getEvents().size());

        record.addEvent("12/1/2014", "This is just a test.");

        shipments.updateShipment(record);

        getShipment("order-quote-0");
    }

    private void getShipment(String id)
    {
        ShipmentRecord record;
        record = shipments.getShipmentById(id);
        assertNotNull(record);
        assertNotNull(record.getEvents());
        assertEquals(1, record.getEvents().size());

        assertEquals("12/1/2014", record.getEvents().get(0).getDate());
        assertEquals("This is just a test.", record.getEvents().get(0).getComments());
    }

    public void testAddEvent()
    {
        ShipmentRecord record = shipments.getShipmentById("order-quote-0");
        assertNotNull(record);
        assertNotNull(record.getEvents());
        assertEquals(0, record.getEvents().size());

        shipments.addEvent("order-quote-0", new ShipmentEventInfo("12/1/2014", "This is just a test."));

        getShipment("order-quote-0");
    }

    public static ShipmentRecord createShipmentRecord(String orderId)
    {
        ShipmentRecord result = new ShipmentRecord();
        result.setOrderId(orderId);
        result.setDeliveryDate("02/02/2015");
        result.setContactName("Jane Smith");
        result.setPrimaryContactPhone(new PhoneInfo("206-555-1212", "Mobile"));
        result.setAlternateContactPhone(new PhoneInfo("206-555-1111", "Work"));
        result.setDeliveryAddress(new DeliveryAddress("123 Main Street", "Bellevue", "WA", "98006", "Near KFC"));
        return result;
    }

    private OrderRepository orders;
    private ShipmentRepository shipments;
}
