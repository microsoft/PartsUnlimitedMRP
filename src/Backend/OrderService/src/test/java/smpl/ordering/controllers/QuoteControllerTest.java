package smpl.ordering.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import smpl.ordering.TestPath;
import smpl.ordering.models.Quote;
import smpl.ordering.repositories.QuoteRepositoryTest;
import smpl.ordering.repositories.RepositoryFactory;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class QuoteControllerTest
{

    @Before
    public void setUp() throws Exception
    {
        // Make sure we're using memory-based repositories.
        RepositoryFactory.reset("memory");

        // Make sure the repositories are empty.
        ((TestPath) RepositoryFactory.getDealersRepository()).reset();
        ((TestPath) RepositoryFactory.getCatalogItemsRepository()).reset();
        ((TestPath) RepositoryFactory.getQuoteRepository()).reset();

        controller = new QuoteController();
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void testCreateQuote() throws Exception
    {
        ResponseEntity response = controller.createQuote(QuoteRepositoryTest.createQuote("quote-4711"));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Quote q = ((ResponseEntity<Quote>) response).getBody();
        assertNotNull(q);
        assertEquals("quote-4711", q.getQuoteId());
        HttpHeaders headers = response.getHeaders();
        assertEquals(new URI("/quotes/quote-4711"), headers.getLocation());

        response = controller.createQuote(QuoteRepositoryTest.createQuote(null));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        q = ((ResponseEntity<Quote>) response).getBody();
        assertNotNull(q);
        // The default quote id is a random integer. We'll get an exception if not.
        Integer.parseInt(q.getQuoteId());
        headers = response.getHeaders();
        assertEquals(new URI("/quotes/" + q.getQuoteId()), headers.getLocation());

        response = controller.createQuote(QuoteRepositoryTest.createQuote(""));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        q = ((ResponseEntity<Quote>) response).getBody();
        assertNotNull(q);
        // The default quote id is a random integer. We'll get an exception if not.
        Integer.parseInt(q.getQuoteId());
        headers = response.getHeaders();
        assertEquals(new URI("/quotes/" + q.getQuoteId()), headers.getLocation());

        response = controller.createQuote(QuoteRepositoryTest.createQuote("quote-4711"));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testUpdateQuote() throws Exception
    {
        Quote q = QuoteRepositoryTest.createQuote("quote-4711");

        ResponseEntity response = controller.updateQuote("quote-4711", q);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        response = controller.createQuote(q);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.updateQuote("quote-4711", q);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        response = controller.getQuoteById("quote-4711");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        q = ((ResponseEntity<Quote>) response).getBody();
        assertNotNull(q);
        assertEquals("quote-4711", q.getQuoteId());
    }

    @Test
    public void testGetQuoteById() throws Exception
    {
        ResponseEntity response = controller.getQuoteById("quote-4711");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        createNewQuote();
    }

    private void createNewQuote()
    {
        Quote q = QuoteRepositoryTest.createQuote("quote-4711");
        ResponseEntity response = controller.createQuote(q);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.getQuoteById("quote-4711");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testGetQuotesByCustomerName() throws Exception
    {
        ResponseEntity response = controller.getQuotesByCustomerName("cust-");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        Quote q = QuoteRepositoryTest.createQuote("quote-4711");
        response = controller.createQuote(q);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.getQuotesByCustomerName("cust-");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Quote> quotes = ((ResponseEntity<List<Quote>>) response).getBody();
        assertNotNull(quotes);
        assertEquals(1, quotes.size());

        controller.createQuote(QuoteRepositoryTest.createQuote("quote-4712"));

        response = controller.getQuotesByCustomerName("cust-");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        quotes = ((ResponseEntity<List<Quote>>) response).getBody();
        assertNotNull(quotes);
        assertEquals(2, quotes.size());
    }

    @Test
    public void testDeleteQuote() throws Exception
    {
        ResponseEntity response = controller.deleteQuote("quote-4711");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        createNewQuote();

        response = controller.deleteQuote("quote-4711");
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        response = controller.getQuoteById("quote-4711");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    private QuoteController controller;
}
