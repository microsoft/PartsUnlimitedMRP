package smpl.ordering.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import smpl.ordering.TestPath;
import smpl.ordering.models.CatalogItem;
import smpl.ordering.repositories.CatalogItemsRepository;
import smpl.ordering.repositories.RepositoryFactory;

import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class CatalogControllerTest
{
    @Before
    public void setUp() throws Exception
    {
        // Make sure we're using memory-based repositories.
        RepositoryFactory.reset("memory");

        // Make sure the repository is empty.
        CatalogItemsRepository catalog = RepositoryFactory.getCatalogItemsRepository();
        ((TestPath) catalog).reset();

        controller = new CatalogController();
    }

    @Test
    public void testAddCatalogItem() throws Exception
    {
        ResponseEntity response =
                controller.addCatalogItem(new CatalogItem("ACC-0001", "Shelving", 10.50, 4, 3));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Negative test case

        response = controller.addCatalogItem(new CatalogItem("", "Shelving", 11.50, 4, 3));
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        response = controller.addCatalogItem(new CatalogItem("ACC-0001", "Shelving", 11.50, 4, 3));
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testUpsertCatalogItem() throws Exception
    {
        ResponseEntity response =
                controller.upsertCatalogItem("ACC-0001", new CatalogItem("ACC-0001", "Shelving", 10.50, 4, 3));
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        response = controller.addCatalogItem(new CatalogItem("ACC-0001", "Shelving", 10.50, 4, 3));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response =
                controller.upsertCatalogItem("ACC-0001", new CatalogItem("ACC-0001", "Shelving", 12.50, 4, 3));
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResponseEntity<CatalogItem> getResponse = controller.getCatalogItem("ACC-0001");
        assertNotNull(getResponse);
        assertNotNull(getResponse.getBody());
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        CatalogItem catalogItem = getResponse.getBody();
        assertEquals("ACC-0001", catalogItem.getSkuNumber());
        assertEquals(12.50, catalogItem.getPrice(), 0.01);
    }

    @Test
    public void testGetCatalogItems() throws Exception
    {
        {
            ResponseEntity<List<CatalogItem>> response = controller.getCatalogItems();
            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

            List<CatalogItem> list = response.getBody();
            assertNull(list);
        }

        controller.addCatalogItem(new CatalogItem("ACC-0001", "Shelving", 10.50, 4, 3));
        controller.addCatalogItem(new CatalogItem("ACC-0002", "Refrigeration Unit", 2500, 2, 7));
        controller.addCatalogItem(new CatalogItem("ACC-0003", "Freezer Unit", 4500, 4, 5));

        {
            ResponseEntity<List<CatalogItem>> response = controller.getCatalogItems();
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());

            List<CatalogItem> list = response.getBody();
            assertEquals(3, list.size());
        }
    }

    @Test
    public void testGetCatalogItem() throws Exception
    {
        {
            ResponseEntity<CatalogItem> response = controller.getCatalogItem("ACC-0002");
            assertNotNull(response);
            assertNull(response.getBody());
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        controller.addCatalogItem(new CatalogItem("ACC-0001", "Shelving", 10.50, 4, 3));
        controller.addCatalogItem(new CatalogItem("ACC-0002", "Refrigeration Unit", 2500, 2, 7));
        controller.addCatalogItem(new CatalogItem("ACC-0003", "Freezer Unit", 4500, 4, 5));

        {
            ResponseEntity<CatalogItem> response = controller.getCatalogItem("ACC-0002");
            assertNotNull(response);
            assertNotNull(response.getBody());
            assertEquals(HttpStatus.OK, response.getStatusCode());

            CatalogItem catalogItem = response.getBody();
            assertEquals("ACC-0002", catalogItem.getSkuNumber());
        }
    }

    @Test
    public void testRemoveCatalogItem() throws Exception
    {
        ResponseEntity response = controller.addCatalogItem(new CatalogItem("ACC-0001", "Shelving", 10.50, 4, 3));
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        response = controller.removeCatalogItem("ACC-0001");
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<CatalogItem> getResponse = controller.getCatalogItem("ACC-0001");
        assertNotNull(getResponse);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    private CatalogController controller;
}
