package smpl.ordering.repositories.mongodb;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import smpl.ordering.TestPath;
import smpl.ordering.repositories.CatalogItemsRepository;
import smpl.ordering.repositories.mongodb.models.CatalogItem;
import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB-based catalog items repository implementation class
 */
public class MongoCatalogItemsRepository
        implements CatalogItemsRepository, TestPath
{
    @Override
    public List<smpl.ordering.models.CatalogItem> getCatalogItems()
    {
        List<CatalogItem> found = operations.findAll(CatalogItem.class);

        List<smpl.ordering.models.CatalogItem> result = new ArrayList<>();

        for (CatalogItem catalogItem : found)
        {
            result.add(catalogItem.toCatalogItem());
        }

        return result;
    }

    @Override
    public smpl.ordering.models.CatalogItem getCatalogItem(String sku)
    {
        CatalogItem existing = findExistingCatalogItem(sku);

        if (existing != null)
        {
            return existing.toCatalogItem();
        }

        return null;
    }

    private CatalogItem findExistingCatalogItem(String sku)
    {
        Query findExisting = new Query(Criteria.where("skuNumber").is(sku));
        return operations.findOne(findExisting, CatalogItem.class);
    }

    @Override
    public boolean upsertCatalogItem(String sku, smpl.ordering.models.CatalogItem catalogItem, String eTag)
    {
        CatalogItem existing = findExistingCatalogItem(sku);
        CatalogItem mongoCatalogItem = new smpl.ordering.repositories.mongodb.models.CatalogItem(catalogItem);

        if (existing != null)
        {
            mongoCatalogItem.setId(existing.getId());
        }

        operations.save(mongoCatalogItem);

        return existing != null;
    }

    @Override
    public boolean removeCatalogItem(String sku, String eTag)
    {
        Query findExisting = new Query(Criteria.where("skuNumber").is(sku));
        CatalogItem existing = operations.findAndRemove(findExisting, CatalogItem.class);
        return existing != null;
    }

    public MongoCatalogItemsRepository(MongoTemplate template)
    {
        operations = new MongoOperationsWithRetry(template);
    }

    private final MongoOperations operations;

    @Override
    public void reset()
    {
        operations.dropCollection(CatalogItem.class);
    }
}
