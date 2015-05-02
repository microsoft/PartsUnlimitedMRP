package smpl.ordering.repositories.mock.test;

import org.junit.Before;
import org.junit.Test;
import smpl.ordering.repositories.*;

@SuppressWarnings("EmptyMethod")
public class MockQuoteRepositoryTest
        extends QuoteRepositoryTest
{
    @Before
    public void setUp() throws Exception
    {
        // Make sure we're using memory-based repositories.
        RepositoryFactory.reset("memory");
        super.setUp();
    }

    @Test
    public void testGetQuote()
    {
        super.testGetQuote();
    }

    @Test
    public void testGetQuotesByCustomerName()
    {
        super.testGetQuotesByCustomerName();
    }

    @Test
    public void testCreateQuote() throws Exception
    {
        super.testCreateQuote();
    }

    @Test
    public void testUpdateQuote()
    {
        super.testUpdateQuote();
    }

    @Test
    public void testRemoveQuote()
    {
        super.testRemoveQuote();
    }
}
