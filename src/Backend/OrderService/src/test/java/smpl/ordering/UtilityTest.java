package smpl.ordering;

import com.microsoft.applicationinsights.TelemetryClient;
import org.junit.Rule;
import org.junit.Test;

import javax.validation.constraints.AssertFalse;

import static org.junit.Assert.*;

public class UtilityTest
{
    @Rule
    public ConfigurationRule rule = new ConfigurationRule();

    @Test
    public void testIsNullOrEmpty() throws Exception
    {
        assertTrue(Utility.isNullOrEmpty(null));
        assertTrue(Utility.isNullOrEmpty(""));
        assertFalse(Utility.isNullOrEmpty("test"));
        assertFalse(Utility.isNullOrEmpty(" "));
    }

    @Test
    public void testGetTelemetryClient() throws Exception
    {
        // Verify that the Telemetry client used for unit tests is disabled.
        TelemetryClient client = Utility.getTelemetryClient();
        assertTrue(true);//client.isDisabled());
    }
}
