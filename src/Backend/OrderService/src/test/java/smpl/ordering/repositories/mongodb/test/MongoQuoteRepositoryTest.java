package smpl.ordering.repositories.mongodb.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import smpl.ordering.ConfigurationRule;
import smpl.ordering.repositories.QuoteRepositoryTest;
import smpl.ordering.repositories.RepositoryFactory;

@SuppressWarnings("EmptyMethod")
public class MongoQuoteRepositoryTest
        extends QuoteRepositoryTest
{
    @Rule
    public ConfigurationRule rule = new ConfigurationRule();

    @Before
    @Override
    public void setUp() throws Exception
    {
        // Make sure we're using a clean repository.
        RepositoryFactory.reset("mongodb");
        super.setUp();
    }

    @Test
    @Override
    public void testGetQuote()
    {
        super.testGetQuote();
    }

    @Test
    @Override
    public void testGetQuotesByCustomerName()
    {
        super.testGetQuotesByCustomerName();
    }

    @Test
    @Override
    public void testCreateQuote() throws Exception
    {
        super.testCreateQuote();
    }

    @Test
    @Override
    public void testUpdateQuote()
    {
        super.testUpdateQuote();
    }

    @Test
    @Override
    public void testRemoveQuote()
    {
        super.testRemoveQuote();
    }
}
