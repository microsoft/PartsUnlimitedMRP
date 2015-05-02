package smpl.ordering.repositories;

import smpl.ordering.TestPath;
import smpl.ordering.models.CatalogItem;

import java.util.List;

import static org.junit.Assert.*;

public class CatalogItemsRepositoryTest
{
    protected void setUp() throws Exception
    {
        catalog = RepositoryFactory.getCatalogItemsRepository();
        ((TestPath) catalog).reset();

        catalog.upsertCatalogItem("ACC-0001", new CatalogItem("ACC-0001", "Shelving", 10.50, 4, 3), null);
        catalog.upsertCatalogItem("ACC-0002", new CatalogItem("ACC-0002", "Refrigeration Unit", 2500, 4, 3), null);
        catalog.upsertCatalogItem("ACC-0003", new CatalogItem("ACC-0003", "Freezer Unit", 4500, 4, 3), null);
    }

    protected void testGetCatalogItems()
    {
        List<CatalogItem> list = catalog.getCatalogItems();
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("ACC-0001", list.get(0).getSkuNumber());
        assertEquals("ACC-0002", list.get(1).getSkuNumber());
        assertEquals("ACC-0003", list.get(2).getSkuNumber());

        catalog.upsertCatalogItem("ACC-0004", new CatalogItem("ACC-0004", "Shelving", 13.50, 4, 3), null);
        list = catalog.getCatalogItems();
        assertEquals(4, list.size());

        assertEquals("ACC-0004", list.get(3).getSkuNumber());
    }

    protected void testGetCatalogItem()
    {
        assertEquals(10.50, catalog.getCatalogItem("ACC-0001").getPrice(), 0.01);
        assertEquals(2500, catalog.getCatalogItem("ACC-0002").getPrice(), 0.10);
        assertEquals(4500, catalog.getCatalogItem("ACC-0003").getPrice(), 0.10);
    }

    protected void testUpsertCatalogItem()
    {
        assertTrue(catalog.upsertCatalogItem("ACC-0001", new CatalogItem("ACC-0001", "Shelving", 11.50, 4, 3), null));
        assertFalse(catalog.upsertCatalogItem("ACC-0004", new CatalogItem("ACC-0004", "Shelving", 13.50, 4, 3), null));
        assertEquals(11.50, catalog.getCatalogItem("ACC-0001").getPrice(), 0.01);
    }

    protected void testRemoveCatalogItem()
    {
        assertTrue(catalog.removeCatalogItem("ACC-0001", null));
        assertFalse(catalog.removeCatalogItem("ACC-0004", null));
        List<CatalogItem> list = catalog.getCatalogItems();
        assertEquals(2, list.size());
        assertTrue(catalog.removeCatalogItem("ACC-0002", null));
        assertTrue(catalog.removeCatalogItem("ACC-0003", null));
        list = catalog.getCatalogItems();
        assertEquals(0, list.size());
    }

    private static CatalogItemsRepository catalog;
}
