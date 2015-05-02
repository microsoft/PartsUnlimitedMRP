package smpl.ordering.repositories.mongodb;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import smpl.ordering.BadRequestException;
import smpl.ordering.TestPath;
import smpl.ordering.models.*;
import smpl.ordering.repositories.DealersRepository;
import smpl.ordering.repositories.QuoteRepository;
import smpl.ordering.repositories.mongodb.models.QuoteDetails;

import java.util.*;

public class MongoQuoteRepository
        implements QuoteRepository, TestPath
{
    @Override
    public Quote getQuote(String id)
    {
        QuoteDetails existing = findExistingQuote(id);
        return (existing != null) ? existing.toQuote() : null;
    }

    private QuoteDetails findExistingQuote(String id)
    {
        Query findExisting = new Query(Criteria.where("quoteId").is(id));
        return operations.findOne(findExisting, QuoteDetails.class);
    }

    @Override
    public List<Quote> getQuotesByCustomerName(String customerName)
    {
        // TODO: figure out a way to not bring all quotes into memory just to
        //       do the filtering. The database should filter for us.

        //Query findExisting = new Query(Criteria.where("customerName").is(customerName));
        List<QuoteDetails> found = operations.findAll(QuoteDetails.class);

        List<Quote> result = new ArrayList<>();

        if (found != null)
        {
            for (QuoteDetails q : found)
            {
                String cName = q.getCustomerName();
                if (cName != null && cName.toLowerCase().contains(customerName.toLowerCase()))
                {
                    result.add(q.toQuote());
                }
            }
        }
        return result;
    }

    @Override
    public List<String> getQuoteIdsByDealerName(String dealerName)
    {
        List<QuoteDetails> foundQueries = operations.find(new Query(Criteria.where("dealerName").is(dealerName)), QuoteDetails.class);
        List<String> quotesIds = new ArrayList<>();
        for (QuoteDetails q : foundQueries)
        {
            quotesIds.add(q.getQuoteId());
        }

        return quotesIds;
    }

    @Override
    public Quote createQuote(Quote from) throws BadRequestException
    {
        Quote quote = new Quote(from);

        DealerInfo info = dealers.getDealer(from.getDealerName());
        if (info == null)
        {
            dealers.upsertDealer(new DealerInfo(from.getDealerName()), null);
        }

        String id = quote.getQuoteId();

        if (id == null || id.isEmpty())
        {
            quote.setQuoteId(String.format("%d", s_counter.nextInt() & 0x7FFFFFFF));
        }
        else
        {
            if (getQuote(id) != null)
            {
                throw new BadRequestException(String.format("Duplicate: the quote '%s' already exists", id));
            }
        }

        operations.insert(new QuoteDetails(quote));

        return quote;
    }

    @Override
    public boolean updateQuote(String id, Quote from, String eTag)
    {
        QuoteDetails existing = findExistingQuote(id);
        if (existing == null) return false;

        from.setQuoteId(id); // Just to make sure...

        DealerInfo info = dealers.getDealer(from.getDealerName());
        if (info == null)
        {
            dealers.upsertDealer(new DealerInfo(from.getDealerName()), null);
        }

        QuoteDetails details = new QuoteDetails(from);
        details.setId(existing.getId());

        operations.save(details);

        return true;
    }

    @Override
    public boolean removeQuote(String id, String eTag)
    {
        Query findExisting = new Query(Criteria.where("quoteId").is(id));
        QuoteDetails existing = operations.findAndRemove(findExisting, QuoteDetails.class);
        return existing != null;
    }

    public MongoQuoteRepository(MongoTemplate template, DealersRepository dealers)
    {
        this.operations = new MongoOperationsWithRetry(template);
        this.dealers = dealers;
    }

    static
    {
        s_counter = new Random();
    }

    private final DealersRepository dealers;

    private static final Random s_counter;
    private final MongoOperations operations;

    @Override
    public void reset()
    {
        operations.dropCollection("quotes");
    }
}
