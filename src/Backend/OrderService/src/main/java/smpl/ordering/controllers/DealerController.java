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
import smpl.ordering.models.DealerInfo;
import smpl.ordering.repositories.DealersRepository;
import smpl.ordering.repositories.RepositoryFactory;

import java.util.List;

@Controller
@RequestMapping("/dealers")
public class DealerController
{
    /**
     * Gets a list of available dealers.
     *
     * @return An HttpResponse containing a list of dealers.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getDealers()
    {
		//Fix this line in Application Performance Monitoring HOL from 1000 to 1
		int numMongoDBCalls = 1000; 
		
        try
        {
			int count = 0; 
			List<DealerInfo> dealers = getRepository().getDealers();
			
			while(count < numMongoDBCalls - 1)
			{
				dealers = getRepository().getDealers();
				count++; 
			}
            if (dealers == null || dealers.size() == 0)
            {
                return new ResponseEntity<List<DealerInfo>>(HttpStatus.NOT_FOUND);
            }
            else
            {
                return new ResponseEntity<>(dealers, HttpStatus.OK);
            }
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity<>(exc.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets a specific dealer by its name.
     *
     * @param name The dealer name
     * @return An HttpResponse containing a list of catalog items.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{name}")
    public ResponseEntity getDealer(@PathVariable String name)
    {
        try
        {
            DealerInfo dealer = getRepository().getDealer(name);
            if (dealer == null)
            {
                return new ResponseEntity<DealerInfo>(HttpStatus.NOT_FOUND);
            }
            else
            {
                return new ResponseEntity<>(dealer, HttpStatus.OK);
            }
        }
        catch (Exception exc)
        {
            // Don't cache the client -- it's relying on thread-local storage.
            TelemetryClient client = Utility.getTelemetryClient();
            if (client != null) client.trackException(exc);
            return new ResponseEntity<>(exc.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Adds a dealer contact record
     *
     * @param info Information about the dealer
     * @return An HTTP status code.
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity addDealer(@RequestBody DealerInfo info)
    {
        String errorMsg = info.validate();
        if (errorMsg != null)
        {
            return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
        }

        try
        {
            DealerInfo dealer = getRepository().getDealer(info.getName());
            if (dealer != null)
            {
                return new ResponseEntity<>("Dealer already exists", HttpStatus.CONFLICT);
            }

            boolean result = getRepository().upsertDealer(info, null);
            String applicationPath = OrderingInitializer.getApplicationPath();
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", applicationPath + "/dealers/" + info.getName());
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
     * Adds a dealer contact record
     *
     * @param name The dealer name
     * @param info Information about the dealer
     * @return An HTTP status code.
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{name}")
    public ResponseEntity updateDealer(@PathVariable String name, @RequestBody DealerInfo info)
    {
        String errorMsg = info.validate();
        if (errorMsg != null)
        {
            return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
        }

        try
        {
            DealerInfo dealer = getRepository().getDealer(name);
            if (dealer == null)
            {
                return new ResponseEntity<DealerInfo>(HttpStatus.NOT_FOUND);
            }

            getRepository().upsertDealer(info, null);
            return new ResponseEntity(HttpStatus.OK);
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
     * @param name The dealer name.
     * @return An HTTP status code.
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{name}")
    public ResponseEntity removeDealer(@PathVariable String name)
    {

        try
        {
            if (getRepository().removeDealer(name, null))
            {
                return new ResponseEntity<DealerInfo>(HttpStatus.NO_CONTENT);
            }
            else
            {
                return new ResponseEntity<DealerInfo>(HttpStatus.NOT_FOUND);
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

    private static DealersRepository getRepository()
    {
        return RepositoryFactory.getDealersRepository();
    }
}
