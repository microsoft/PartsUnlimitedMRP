package smpl.ordering.repositories.mock.test;

import org.junit.Before;
import org.junit.Test;
import smpl.ordering.repositories.DealersRepositoryTest;
import smpl.ordering.repositories.RepositoryFactory;

@SuppressWarnings("EmptyMethod")
public class MockDealersRepositoryTest
        extends DealersRepositoryTest
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
    public void testGetDealers()
    {
        super.testGetDealers();
    }

    @Test
    @Override
    public void testGetDealer()
    {
        super.testGetDealer();
    }

    @Test
    @Override
    public void testUpsertDealer()
    {
        super.testUpsertDealer();
    }

    @Test
    @Override
    public void testRemoveDealer()
    {
        super.testRemoveDealer();
    }
}
