package smpl.ordering.repositories.mongodb;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import smpl.ordering.TestPath;
import smpl.ordering.models.DealerInfo;
import smpl.ordering.repositories.DealersRepository;
import smpl.ordering.repositories.mongodb.models.Dealer;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB-based dealers repository implementation class
 */
public class MongoDealersRepository
        implements DealersRepository, TestPath
{

    @Override
    public List<DealerInfo> getDealers()
    {
        List<DealerInfo> result = new ArrayList<>();
        List<Dealer> found = operations.findAll(Dealer.class);

        for (Dealer dealer : found)
        {
            result.add(dealer.toDealerInfo());
        }
        return result;
    }

    @Override
    public DealerInfo getDealer(String name)
    {
        Query findExisting = new Query(Criteria.where("name").is(name));
        Dealer existing = operations.findOne(findExisting, Dealer.class);

        if (existing != null)
        {
            return existing.toDealerInfo();
        }
        return null;
    }

    @Override
    public boolean upsertDealer(DealerInfo dealer, String eTag)
    {
        Query findExisting = new Query(Criteria.where("name").is(dealer.getName()));
        Dealer existing = operations.findOne(findExisting, Dealer.class);
        Dealer mongoDealer = new Dealer(dealer);

        if (existing != null)
        {
            mongoDealer.setId(existing.getId());
        }

        operations.save(mongoDealer);

        return existing != null;
    }

    @Override
    public boolean removeDealer(String name, String eTag)
    {
        Query findExisting = new Query(Criteria.where("name").is(name));
        Dealer existing = operations.findAndRemove(findExisting, Dealer.class);
        return existing != null;
    }

    public MongoDealersRepository(MongoTemplate template)
    {
        operations = new MongoOperationsWithRetry(template);
    }

    private final MongoOperations operations;

    @Override
    public void reset()
    {
        operations.dropCollection("dealers");
    }
}
