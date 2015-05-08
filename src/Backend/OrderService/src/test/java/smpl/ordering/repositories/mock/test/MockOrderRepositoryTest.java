package smpl.ordering.repositories.mock.test;

import org.junit.Before;
import org.junit.Test;

import smpl.ordering.repositories.*;

@SuppressWarnings("EmptyMethod")
public class MockOrderRepositoryTest
        extends OrderRepositoryTest
{

    @Before
    @Override
    public void setUp() throws Exception
    {
        // Make sure we're using memory-based repositories.
        RepositoryFactory.reset("memory");
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

    private OrderRepository repository;
}
