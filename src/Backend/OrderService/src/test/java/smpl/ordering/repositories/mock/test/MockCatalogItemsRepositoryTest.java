package smpl.fabrikant.ordering.repositories.mock.test;

import org.junit.Before;
import org.junit.Test;
import smpl.fabrikant.ordering.repositories.CatalogItemsRepositoryTest;
import smpl.fabrikant.ordering.repositories.RepositoryFactory;

public class MockCatalogItemsRepositoryTest extends CatalogItemsRepositoryTest
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
