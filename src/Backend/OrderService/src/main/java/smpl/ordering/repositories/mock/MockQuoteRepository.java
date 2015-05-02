package smpl.ordering.repositories.mock;

import smpl.ordering.TestPath;
import smpl.ordering.models.*;
import smpl.ordering.BadRequestException;
import smpl.ordering.repositories.CatalogItemsRepository;
import smpl.ordering.repositories.DealersRepository;
import smpl.ordering.repositories.QuoteRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * An in-memory repository of quotes. Used for testing the API surface area.
 */
@SuppressWarnings("UnusedParameters")
public class MockQuoteRepository
        implements QuoteRepository, TestPath
{
    public MockQuoteRepository(CatalogItemsRepository catalog, DealersRepository dealers)
    {
        this.quotes = new ArrayList<>();
        this.dealers = dealers;
    }

    /**
     * Retrieves a specific quote from the repository.
     *
     * @param id The quote id.
     * @return A Quote object, if found.
     */
    @Override
    public Quote getQuote(String id)
    {
        for (Quote q : quotes)
        {
            if (q.getQuoteId().equals(id))
            {
                return q;
            }
        }
        return null;
    }

    /**
     * Retrieves a list of quotes where the customer name contains the string passed in.
     *
     * @param customerName A fragment of the customer name.
     * @return A list of quotes, possibly empty.
     */
    @Override
    public List<Quote> getQuotesByCustomerName(String customerName)
    {
        List<Quote> lst = new ArrayList<>();
        for (Quote q : quotes)
        {
            if (q.getCustomerName().toLowerCase().contains(customerName.toLowerCase()))
            {
                lst.add(q);
            }
        }
        return lst;
    }

    @Override
    public List<String> getQuoteIdsByDealerName(String dealerName)
    {
        List<String> lst = new ArrayList<>();
        for (Quote q : quotes)
        {
            if (q.getDealerName().compareToIgnoreCase(dealerName) == 0)
            {
                lst.add(q.getQuoteId());
            }
        }
        return lst;
    }

    /**
     * Creates a new quote from information edited by a client.
     *
     * @param quote The client quote information.
     * @return A Quote object.
     */
    @Override
    public Quote createQuote(Quote quote) throws BadRequestException
    {
        DealerInfo info = dealers.getDealer(quote.getDealerName());
        if (info == null)
        {
            dealers.upsertDealer(new DealerInfo(quote.getDealerName()), null);
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

        quotes.add(quote);

        return quote;
    }

    /**
     * Update an existing quote from client-edited information.
     *
     * @param id   The quote id.
     * @param from New client-edited information.
     * @param eTag An entity tag used for optimistic concurrency
     * @return true if the quote exists, false otherwise.
     */
    @Override
    public boolean updateQuote(String id, Quote from, String eTag)
    {
        Quote quote = getQuote(id);
        if (quote == null) return false;

        DealerInfo info = dealers.getDealer(from.getDealerName());
        if (info == null)
        {
            dealers.upsertDealer(new DealerInfo(from.getDealerName()), null);
        }

        from.setQuoteId(id);

        int idx = quotes.indexOf(quote);
        quotes.set(idx, from);

        return true;
    }

    /**
     * Remove a quote from the system.
     *
     * @param id   The quote id.
     * @param eTag An entity tag used for optimistic concurrency
     * @return true if the quote exists, false otherwise
     */
    @Override
    public boolean removeQuote(String id, String eTag)
    {
        Quote quote = getQuote(id);
        if (quote == null) return false;
        quotes.remove(quote);
        return true;
    }

    static
    {
        s_counter = new Random();
    }

    private final List<Quote> quotes;
    private final DealersRepository dealers;
    private static final Random s_counter;

    @Override
    public void reset()
    {
        quotes.clear();
    }
}
