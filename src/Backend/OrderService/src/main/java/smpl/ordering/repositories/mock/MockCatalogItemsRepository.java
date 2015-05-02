package smpl.ordering.repositories.mock;

import smpl.ordering.TestPath;
import smpl.ordering.models.CatalogItem;
import smpl.ordering.repositories.CatalogItemsRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * An in-memory repository of catalog items. Used for testing the API surface area.
 */
public class MockCatalogItemsRepository
        implements CatalogItemsRepository, TestPath
{
    public MockCatalogItemsRepository()
    {
        catalog.add(new CatalogItem("MRP-0001", "Brake Pads", 26.99,  10,  3));
        catalog.add(new CatalogItem("MRP-0002", "Brake Calipers", 33.99, 10, 3));
        catalog.add(new CatalogItem("MRP-0003", "Brake Calipers Guide Pin", 2.99, 10, 3));
    }

    /**
     * Retrieves a list of the items in the catalog.
     *
     * @return An catalog item list
     */
    @Override
    public List<CatalogItem> getCatalogItems()
    {

        List<CatalogItem> result = new ArrayList<>();
        for (CatalogItem catalogItem : catalog)
        {
            result.add(new CatalogItem(catalogItem));
        }
        return result;
    }

    /**
     * Retrieves information on a specific product
     *
     * @param sku The SKU number
     * @return The catalogItem, null if not found.
     */
    @Override
    public CatalogItem getCatalogItem(String sku)
    {
        for (CatalogItem catalogItem : catalog)
        {
            if (compareSkuNumbers(sku, catalogItem))
            {
                return new CatalogItem(catalogItem);
            }
        }
        return null;
    }

    private boolean compareSkuNumbers(String sku, CatalogItem catalogItem)
    {
        return catalogItem.getSkuNumber().toLowerCase().equals(sku.toLowerCase());
    }

    /**
     * Insert or update an catalog item product information record in the catalog.
     *
     * @param sku       The SKU number
     * @param catalogItem The catalog item information record.
     * @param eTag      An entity tag used for optimistic concurrency
     * @return true if update, false if insert.
     */
    @Override
    public boolean upsertCatalogItem(String sku, CatalogItem catalogItem, String eTag)
    {
        for (int i = 0; i < catalog.size(); ++i)
        {
            CatalogItem ci = catalog.get(i);
            if (compareSkuNumbers(sku, ci))
            {
                catalog.set(i, catalogItem);
                return true;
            }
        }
        catalog.add(catalogItem);
        return false;
    }

    /**
     * Remove an catalog item information record from the catalog.
     *
     * @param sku The SKU number
     * @return true if found, false otherwise.
     */
    @Override
    public boolean removeCatalogItem(String sku, String eTag)
    {
        for (int i = 0; i < catalog.size(); ++i)
        {
            CatalogItem catalogItem = catalog.get(i);
            if (compareSkuNumbers(sku, catalogItem))
            {
                catalog.remove(i);
                return true;
            }
        }
        return false;
    }

    private final List<CatalogItem> catalog = new ArrayList<>();

    @Override
    public void reset()
    {
        catalog.clear();
    }
}
