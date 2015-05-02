package smpl.ordering.repositories.mongodb.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import smpl.ordering.ConfigurationRule;
import smpl.ordering.repositories.DealersRepositoryTest;
import smpl.ordering.repositories.RepositoryFactory;


@SuppressWarnings("EmptyMethod")
public class MongoDealersRepositoryTest
        extends DealersRepositoryTest
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
