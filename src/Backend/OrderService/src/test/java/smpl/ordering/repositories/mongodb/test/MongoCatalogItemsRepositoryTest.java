package smpl.ordering.repositories.mongodb.test;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import smpl.ordering.ConfigurationRule;
import smpl.ordering.repositories.CatalogItemsRepositoryTest;
import smpl.ordering.repositories.RepositoryFactory;


public class MongoCatalogItemsRepositoryTest extends CatalogItemsRepositoryTest
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
    public void testGetCatalogItems()
    {
        super.testGetCatalogItems();
    }

    @Test
    @Override
    public void testGetCatalogItem()
    {
        super.testGetCatalogItem();
    }

    @Test
    @Override
    public void testUpsertCatalogItem()
    {
        super.testUpsertCatalogItem();
    }

    @Test
    @Override
    public void testRemoveCatalogItem()
    {
        super.testRemoveCatalogItem();
    }
}
