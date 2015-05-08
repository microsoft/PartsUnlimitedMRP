package smpl.ordering.controllers;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import smpl.ordering.OrderingInitializer;
import smpl.ordering.Utility;
import smpl.ordering.models.CatalogItem;
import smpl.ordering.repositories.CatalogItemsRepository;
import smpl.ordering.repositories.RepositoryFactory;

import java.util.List;

@Controller
@RequestMapping("/catalog")
public class CatalogController
{
    /**
     * Gets a list of available catalog item.
     *
     * @return An HttpResponse containing a list of catalog item.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getCatalogItems()
    {
        try
        {
            List<CatalogItem> catalog = getRepository().getCatalogItems();
            if (catalog == null || catalog.size() == 0)
            {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            else
            {
                return new ResponseEntity<>(catalog, HttpStatus.OK);
            }
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity<>(exc.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Gets a specific catalog item by its id.
     *
     * @param sku The SKU number
     * @return An HttpResponse containing an catalog item record.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{sku}")
    public ResponseEntity getCatalogItem(@PathVariable String sku)
    {
        try
        {
            CatalogItem catalogItem = getRepository().getCatalogItem(sku);
            if (catalogItem == null)
            {
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
            else
            {
                return new ResponseEntity<>(catalogItem, HttpStatus.OK);
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
     * Adds or updates an catalog item SKU
     *
     * @param info Information about the SKU
     * @return An HTTP status code.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity addCatalogItem(@RequestBody CatalogItem info)
    {
        String errorMsg = info.validate();
        if (errorMsg != null)
        {
            return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
        }

        try
        {
            CatalogItem catalogItem = getRepository().getCatalogItem(info.getSkuNumber());
            if (catalogItem != null)
            {
                return new ResponseEntity<>("The SKU already exists", HttpStatus.CONFLICT);
            }

            boolean result = getRepository().upsertCatalogItem(info.getSkuNumber(), info, null);
            String applicationPath = OrderingInitializer.getApplicationPath();
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", applicationPath + "/catalog/" + info.getSkuNumber());
            return new ResponseEntity(responseHeaders, result ? HttpStatus.OK : HttpStatus.CREATED);
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
     * Adds or updates an CatalogItem SKU
     *
     * @param sku  The SKU number
     * @param info Information about the SKU
     * @return An HTTP status code.
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{sku}")
    public ResponseEntity upsertCatalogItem(@PathVariable String sku, @RequestBody CatalogItem info)
    {
        String errorMsg = info.validate();
        if (errorMsg != null)
        {
            return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
        }

        try
        {
            CatalogItem catalogItem = getRepository().getCatalogItem(sku);
            if (catalogItem == null)
            {
                return new ResponseEntity<CatalogItem>(HttpStatus.NOT_FOUND);
            }
            boolean result = getRepository().upsertCatalogItem(sku, info, null);
            return new ResponseEntity(result ? HttpStatus.OK : HttpStatus.CREATED);
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
     * Remove an catalog item SKU from the catalog.
     *
     * @param sku The SKU number.
     * @return An HTTP status code.
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{sku}")
    public ResponseEntity removeCatalogItem(@PathVariable String sku)
    {

        try
        {
            if (getRepository().removeCatalogItem(sku, null))
            {
                return new ResponseEntity<CatalogItem>(HttpStatus.NO_CONTENT);
            }
            else
            {
                return new ResponseEntity<CatalogItem>(HttpStatus.NOT_FOUND);
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

    private CatalogItemsRepository getRepository()
    {
        return RepositoryFactory.getCatalogItemsRepository();
    }
}
