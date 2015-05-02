package smpl.ordering.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import smpl.ordering.TestPath;
import smpl.ordering.models.DealerInfo;
import smpl.ordering.repositories.DealersRepository;
import smpl.ordering.repositories.DealersRepositoryTest;
import smpl.ordering.repositories.RepositoryFactory;

import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class DealerControllerTest
{

    @Before
    public void setUp() throws Exception
    {
        // Make sure we're using memory-based repositories.
        RepositoryFactory.reset("memory");

        // Make sure the repository is empty.
        DealersRepository dealers = RepositoryFactory.getDealersRepository();
        ((TestPath) dealers).reset();

        controller = new DealerController();
    }

    @Test
    public void testAddDealer() throws Exception
    {
        ResponseEntity response = controller.addDealer(DealersRepositoryTest.createDealer("DLR-1"));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Negative test case

        response = controller.addDealer(DealersRepositoryTest.createDealer(null));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        response = controller.addDealer(DealersRepositoryTest.createDealer(""));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        response = controller.addDealer(DealersRepositoryTest.createDealer("DLR-1"));
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testUpdateDealer() throws Exception
    {
        ResponseEntity response = controller.addDealer(DealersRepositoryTest.createDealer("DLR-1"));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        DealerInfo update = DealersRepositoryTest.createDealer("DLR-1");
        update.setContact("Jane Doe");

        response = controller.updateDealer("DLR-1", update);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<DealerInfo> getDealer = controller.getDealer("DLR-1");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        update = getDealer.getBody();
        assertNotNull(update);
        assertEquals("Jane Doe", update.getContact());

        // Negative tests

        update.setName(null);
        response = controller.updateDealer("DLR-1", update);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        update.setName("");
        response = controller.updateDealer("DLR-1", update);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        update = DealersRepositoryTest.createDealer("DLR-2");
        response = controller.updateDealer("DLR-2", update);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetDealers() throws Exception
    {
        {
            ResponseEntity<List<DealerInfo>> response = controller.getDealers();
            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        createDealers();

        {
            ResponseEntity<List<DealerInfo>> response = controller.getDealers();
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            List<DealerInfo> dealers = response.getBody();
            assertNotNull(dealers);
            assertEquals(4, dealers.size());
        }
    }

    private void createDealers()
    {
        controller.addDealer(DealersRepositoryTest.createDealer("DLR-1"));
        controller.addDealer(DealersRepositoryTest.createDealer("DLR-2"));
        controller.addDealer(DealersRepositoryTest.createDealer("DLR-3"));
        controller.addDealer(DealersRepositoryTest.createDealer("DLR-4"));
    }

    @Test
    public void testGetDealer() throws Exception
    {
        {
            ResponseEntity<DealerInfo> response = controller.getDealer("DLR-1");
            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        createDealers();

        {
            ResponseEntity<DealerInfo> response = controller.getDealer("DLR-1");
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            DealerInfo dealer = response.getBody();
            assertNotNull(dealer);
            assertEquals("DLR-1", dealer.getName());
        }
    }

    @Test
    public void testRemoveDealer() throws Exception
    {
        createDealers();

        {
            ResponseEntity<List<DealerInfo>> response = controller.getDealers();
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            List<DealerInfo> dealers = response.getBody();
            assertNotNull(dealers);
            assertEquals(4, dealers.size());
        }

        ResponseEntity remove = controller.removeDealer("DLR-1");
        assertNotNull(remove);
        assertEquals(HttpStatus.NO_CONTENT, remove.getStatusCode());

        {
            ResponseEntity<List<DealerInfo>> response = controller.getDealers();
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            List<DealerInfo> dealers = response.getBody();
            assertNotNull(dealers);
            assertEquals(3, dealers.size());
        }
    }

    private DealerController controller;
}
