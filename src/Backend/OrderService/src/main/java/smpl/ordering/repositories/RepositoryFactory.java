package smpl.ordering.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import smpl.ordering.OrderingConfiguration;
import smpl.ordering.repositories.mock.*;
import smpl.ordering.repositories.mongodb.*;

@SuppressWarnings("EmptyCatchBlock")
public class RepositoryFactory
{

    public static CatalogItemsRepository getCatalogItemsRepository()
    {
        switch (s_factory.storageKind)
        {
        case RepositoryFactory.MEMORY:
            return s_factory.mockRepos.catalogItems;
        case RepositoryFactory.MONGODB:
            return s_factory.mongodbRepos.catalogItems;
        default:
            return null;
        }
    }

    public static DealersRepository getDealersRepository()
    {
        switch (s_factory.storageKind)
        {
        case RepositoryFactory.MEMORY:
            return s_factory.mockRepos.dealers;
        case RepositoryFactory.MONGODB:
            return s_factory.mongodbRepos.dealers;
        default:
            return null;
        }
    }

    public static OrderRepository getOrderRepository()
    {
        switch (s_factory.storageKind)
        {
        case RepositoryFactory.MEMORY:
            return s_factory.mockRepos.orders;
        case RepositoryFactory.MONGODB:
            return s_factory.mongodbRepos.orders;
        default:
            return null;
        }
    }

    public static QuoteRepository getQuoteRepository()
    {
        switch (s_factory.storageKind)
        {
        case RepositoryFactory.MEMORY:
            return s_factory.mockRepos.quotes;
        case RepositoryFactory.MONGODB:
            return s_factory.mongodbRepos.quotes;
        default:
            return null;
        }
    }

    public static ShipmentRepository getShipmentRepository()
    {
        switch (s_factory.storageKind)
        {
        case RepositoryFactory.MEMORY:
            return s_factory.mockRepos.shipments;
        case RepositoryFactory.MONGODB:
            return s_factory.mongodbRepos.shipments;
        default:
            return null;
        }
    }

    private void init(String storage)
    {
        if (mongoTemplate == null)
        {
            try
            {
                mongoTemplate = OrderingConfiguration.getApplicationContext().getBean(MongoTemplate.class);
            }
            catch (Exception exc)
            {
            }
        }

        this.storageKind = storage;
        this.mockRepos = new Repositories();
        this.mongodbRepos = new Repositories();

        this.mockRepos.catalogItems = new MockCatalogItemsRepository();
        this.mockRepos.dealers = new MockDealersRepository();
        this.mockRepos.quotes = new MockQuoteRepository(this.mockRepos.catalogItems, this.mockRepos.dealers);
        this.mockRepos.orders = new MockOrderRepository(this.mockRepos.quotes);
        this.mockRepos.shipments = new MockShipmentRepository(this.mockRepos.orders);

        this.mongodbRepos.catalogItems = new MongoCatalogItemsRepository(mongoTemplate);
        this.mongodbRepos.dealers = new MongoDealersRepository(mongoTemplate);
        this.mongodbRepos.quotes = new MongoQuoteRepository(mongoTemplate, this.mongodbRepos.dealers);
        this.mongodbRepos.orders = new MongoOrderRepository(mongoTemplate, this.mongodbRepos.quotes);
        this.mongodbRepos.shipments = new MongoShipmentRepository(mongoTemplate, this.mongodbRepos.orders);
    }

    private RepositoryFactory(String storage)
    {
        init(storage);
    }

    public static synchronized RepositoryFactory getFactory()
    {
        return s_factory;
    }

    static public void reset(String storage)
    {
        if (s_factory != null)
        {
            s_factory.mongoTemplate = null;
        }
        s_factory = new RepositoryFactory(storage);
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    private class Repositories
    {
        CatalogItemsRepository catalogItems;
        DealersRepository dealers;
        QuoteRepository quotes;
        OrderRepository orders;
        ShipmentRepository shipments;
    }

    private Repositories mockRepos;
    private Repositories mongodbRepos;

    private String storageKind;
    private static RepositoryFactory s_factory;

    public static final String MEMORY = "memory";
    public static final String MONGODB = "mongodb";
}
