package smpl.ordering.controllers;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import smpl.ordering.BadRequestException;
import smpl.ordering.OrderingInitializer;
import smpl.ordering.Utility;
import smpl.ordering.models.Quote;
import smpl.ordering.repositories.QuoteRepository;
import smpl.ordering.repositories.RepositoryFactory;

import java.util.List;

@Controller
@RequestMapping("/quotes")
public class QuoteController
{
    /**
     * Gets a quote identified by its id.
     *
     * @param quoteId The quote id
     * @return An HttpResponse containing the quote, if found.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{quoteId}")
    public ResponseEntity getQuoteById(@PathVariable String quoteId)
    {
        try
        {
            Quote q = getRepository().getQuote(quoteId);
            if (q == null)
            {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            else
            {
                return new ResponseEntity<>(q, HttpStatus.OK);
            }
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity<>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets a list of quotes where the customer name contains the string 'name'
     *
     * @param name A fragment of the customer name field. Case is ignored.
     * @return An HttpResponse containing the quotes, if found.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getQuotesByCustomerName(@RequestParam(value = "name") String name)
    {
        try
        {
            List<Quote> q = getRepository().getQuotesByCustomerName(name);
            if (q == null || q.size() == 0)
            {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            else
            {
                return new ResponseEntity<>(q, HttpStatus.OK);
            }
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity<>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing quote with the data passed in.
     *
     * @param quoteId The quote id.
     * @param info    The client-generated quote information containing the new data.
     * @return An HttpResponse containing the quote, if found.
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{quoteId}")
    public ResponseEntity updateQuote(@PathVariable String quoteId, @RequestBody Quote info)
    {
        // Input data validation
        String errorMsg = info.validate();
        if (errorMsg != null)
        {
            return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
        }

        try
        {
            boolean ok = getRepository().updateQuote(quoteId, info, null);
            return new ResponseEntity(ok ? HttpStatus.OK : HttpStatus.NOT_FOUND);
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity<>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates a new quote from the data passed in.
     *
     * @param info The client-generated quote information containing the new quote data.
     * @return An HttpResponse containing the quote.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createQuote(@RequestBody Quote info)
    {
        String errorMsg = info.validate();
        if (errorMsg != null)
        {
            return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
        }

        try
        {
            Quote result = getRepository().createQuote(info);
            if (result != null)
            {
                String applicationPath = OrderingInitializer.getApplicationPath();
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("Location", applicationPath + "/quotes/" + result.getQuoteId());
                return new ResponseEntity<>(result, responseHeaders, HttpStatus.CREATED);
            }
            else
            {
                return new ResponseEntity(HttpStatus.CONFLICT);
            }
        }
        catch (BadRequestException bre)
        {
            return new ResponseEntity<>(bre.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity<>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Removes an existing quote from the system.
     *
     * @param quoteId The quote id.
     * @return An HTTP status code
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{quoteId}")
    public ResponseEntity deleteQuote(@PathVariable String quoteId)
    {
        try
        {
            boolean ok = getRepository().removeQuote(quoteId, null);
            return new ResponseEntity(ok ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity<>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private QuoteRepository getRepository()
    {
        return RepositoryFactory.getQuoteRepository();
    }
}
