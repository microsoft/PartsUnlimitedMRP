package smpl.ordering.repositories.mongodb.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import smpl.ordering.ConfigurationRule;
import smpl.ordering.repositories.OrderRepositoryTest;
import smpl.ordering.repositories.RepositoryFactory;

@SuppressWarnings("EmptyMethod")
public class MongoOrderRepositoryTest
        extends OrderRepositoryTest
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
    public void testHasOrder()
    {
        super.testHasOrder();
    }

    @Test
    @Override
    public void testGetOrder()
    {
        super.testGetOrder();
    }

    @Test
    @Override
    public void testGetOrdersByQuoteId()
    {
        super.testGetOrdersByQuoteId();
    }

    @Test
    @Override
    public void testGetOrdersByStatus()
    {
        super.testGetOrdersByStatus();
    }

    @Test
    @Override
    public void testGetOrdersByDealerName()
    {
        super.testGetOrdersByDealerName();
    }

    @Test
    @Override
    public void testCreateOrder() throws Exception
    {
        super.testCreateOrder();
    }

    @Test
    @Override
    public void testUpdateOrder() throws Exception
    {
        super.testUpdateOrder();
    }

    @Test
    @Override
    public void testUpdateOrder1() throws Exception
    {
        super.testUpdateOrder1();
    }
}
