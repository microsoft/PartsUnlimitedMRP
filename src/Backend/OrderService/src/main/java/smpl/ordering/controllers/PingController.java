package smpl.ordering.controllers;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import smpl.ordering.OrderingServiceProperties;
import smpl.ordering.PropertyHelper;
import smpl.ordering.Utility;

import java.util.Properties;

@Controller
@RequestMapping("/ping")
public class PingController
{
    @Autowired
    private OrderingServiceProperties orderingServiceProperties;

    @RequestMapping(method = RequestMethod.HEAD)
    public ResponseEntity ping()
    {
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getStatus() throws Exception
    {
        try
        {
            if (orderingServiceProperties != null)
            {
                String message =
                        String.format("%s\n%s\n",
                                orderingServiceProperties.getPingMessage(),
                                orderingServiceProperties.getValidationMessage());

                if ( props == null)
                {
                    props = PropertyHelper.getPropValues("buildinfo.properties");
                }

                if (props != null && props.containsKey("build.number"))
                {
                    message += "Build number:    " + props.getProperty("build.number") + "\n";
                }
                if (props != null && props.containsKey("build.timestamp"))
                {
                    message += "Build timestamp: " + props.getProperty("build.timestamp") + "\n";
                }

                return new ResponseEntity<>(message, HttpStatus.OK);
            }
            else
            {
                return new ResponseEntity(HttpStatus.OK);
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

    private Properties props;
}
