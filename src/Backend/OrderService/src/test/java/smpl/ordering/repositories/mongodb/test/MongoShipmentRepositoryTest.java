package smpl.ordering.repositories.mongodb.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import smpl.ordering.BadRequestException;
import smpl.ordering.ConfigurationRule;
import smpl.ordering.repositories.RepositoryFactory;
import smpl.ordering.repositories.ShipmentRepositoryTest;

@SuppressWarnings("EmptyMethod")
public class MongoShipmentRepositoryTest extends ShipmentRepositoryTest
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

    @Override
    @Test
    public void testGetShipmentById()
    {
        super.testGetShipmentById();
    }

    @Override
    @Test
    public void testGetShipments()
    {
        super.testGetShipments();
    }

    @Override
    @Test
    public void testCreateShipment() throws BadRequestException
    {
        super.testCreateShipment();
    }

    @Override
    @Test
    public void testUpdateShipment()
    {
        super.testUpdateShipment();
    }

    @Override
    @Test
    public void testAddEvent()
    {
        super.testAddEvent();
    }

}
