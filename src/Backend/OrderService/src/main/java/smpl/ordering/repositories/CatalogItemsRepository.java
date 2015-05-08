package smpl.ordering.repositories;

import smpl.ordering.models.CatalogItem;

import java.util.List;

/**
 * Interface for repositories holding catalog item data.
 */
@SuppressWarnings({"SameParameterValue", "UnusedParameters"})
public interface CatalogItemsRepository
{
    List<CatalogItem> getCatalogItems();

    CatalogItem getCatalogItem(String sku);

    boolean upsertCatalogItem(String sku, CatalogItem catalogItem, String eTag);

    boolean removeCatalogItem(String sku, String eTag);
}
