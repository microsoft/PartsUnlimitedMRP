package smpl.ordering.repositories;

import smpl.ordering.BadRequestException;
import smpl.ordering.models.Quote;

import java.util.List;

/**
 * Interface for repositories holding quote data.
 */
@SuppressWarnings({"SameParameterValue", "UnusedParameters"})
public interface QuoteRepository
{
    Quote getQuote(String id);

    List<Quote> getQuotesByCustomerName(String customerName);

    List<String> getQuoteIdsByDealerName(String dealerName);

    Quote createQuote(Quote from) throws BadRequestException;

    boolean updateQuote(String id, Quote quote, String eTag);

    boolean removeQuote(String id, String eTag);
}
