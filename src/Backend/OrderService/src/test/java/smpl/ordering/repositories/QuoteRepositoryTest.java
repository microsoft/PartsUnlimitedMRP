package smpl.ordering.repositories;

import smpl.ordering.BadRequestException;
import smpl.ordering.TestPath;
import smpl.ordering.models.CatalogItem;
import smpl.ordering.models.QuoteItemInfo;
import smpl.ordering.models.Quote;
import smpl.ordering.repositories.mock.test.MockDealersRepositoryTest;

import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("EmptyCatchBlock")
public class QuoteRepositoryTest
{
    public void setUp() throws Exception
    {
        // Set up some data for the quote tests to access.

        repository = RepositoryFactory.getQuoteRepository();
        ((TestPath) repository).reset();

        DealersRepository dealers = RepositoryFactory.getDealersRepository();
        ((TestPath) dealers).reset();

        dealers.upsertDealer(MockDealersRepositoryTest.createDealer("DLR-1"), null);
        dealers.upsertDealer(MockDealersRepositoryTest.createDealer("DLR-2"), null);
        dealers.upsertDealer(MockDealersRepositoryTest.createDealer("DLR-3"), null);
        dealers.upsertDealer(MockDealersRepositoryTest.createDealer("DLR-4"), null);

        CatalogItemsRepository catalog = RepositoryFactory.getCatalogItemsRepository();
        ((TestPath) catalog).reset();

        catalog.upsertCatalogItem("ACC-0001", new CatalogItem("ACC-0001", "Shelving", 10.50, 4, 3), null);
        catalog.upsertCatalogItem("ACC-0002", new CatalogItem("ACC-0002", "Refrigeration Unit", 2500, 2, 7), null);
        catalog.upsertCatalogItem("ACC-0003", new CatalogItem("ACC-0003", "Freezer Unit", 4500, 4, 5), null);

        for (int i = 0; i < 10; ++i)
        {
            repository.createQuote(createQuote(String.format("quote-%s", i)));
        }
    }

    public void testGetQuote()
    {
        Quote q1 = repository.getQuote("quote-0");
        assertNotNull(q1);
        assertNull(repository.getQuote("quote-100"));

        assertEquals("quote-0", q1.getQuoteId());
        assertEquals("cust-quote-0", q1.getCustomerName());
        assertEquals(2, q1.getQuoteItems().size());

        QuoteItemInfo a0 = q1.getQuoteItems().get(0);
        QuoteItemInfo a1 = q1.getQuoteItems().get(1);

        assertTrue(
            ("ACC-0001".equals(a0.getSkuNumber()) && "ACC-0003".equals(a1.getSkuNumber())) ||
            ("ACC-0001".equals(a1.getSkuNumber()) && "ACC-0003".equals(a0.getSkuNumber())));
        assertTrue(
            (15 == a0.getAmount() && 1 == a1.getAmount()) ||
            (15 == a1.getAmount() && 1 == a0.getAmount()));
    }

    public void testGetQuotesByCustomerName()
    {
        List<Quote> q1 = repository.getQuotesByCustomerName("cust-quote-7");
        assertNotNull(q1);
        assertEquals(1, q1.size());
        List<Quote> q3 = repository.getQuotesByCustomerName("non-existent");
        assertNotNull(q3);
        assertTrue(q3.isEmpty());

        List<Quote> q2 = repository.getQuotesByCustomerName("cust-quote");
        assertNotNull(q2);
        assertEquals(10, q2.size());
    }

    public void testCreateQuote() throws Exception
    {
        Quote template = createQuote("New-Quote-1");

        Quote q1 = repository.createQuote(template);

        assertEquals(template, q1);

        try
        {
            // Duplicate
            repository.createQuote(template);
            fail("Duplicate quote not caught.");
        }
        catch (BadRequestException bre)
        {
        }
    }

    public void testUpdateQuote()
    {
        Quote q1 = repository.getQuote("quote-5");
        assertNotNull(q1);

        q1.setState("WA");
        assertTrue(repository.updateQuote("quote-5", q1, null));

        Quote q2 = repository.getQuote("quote-5");
        assertEquals(q1, q2);

        assertFalse(repository.updateQuote("quote-16", q1, null));
    }

    public void testRemoveQuote()
    {
        assertTrue(repository.removeQuote("quote-5", null));
        assertFalse(repository.removeQuote("quote-16", null));
        List<Quote> q2 = repository.getQuotesByCustomerName("cust-quote");
        assertNotNull(q2);
        assertEquals(9, q2.size());
    }

    public static Quote createQuote(String quoteId)
    {
        Quote quote = new Quote();
        quote.setQuoteId(quoteId);
        quote.setDealerName("DLR-1");
        quote.setCustomerName("cust-" + quoteId);
        quote.addQuoteItem("ACC-0001", 15);
        quote.addQuoteItem("ACC-0003", 1);
        return quote;
    }

    private QuoteRepository repository;
}
