package smpl.ordering;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Holds general service configuration properties.
 */
@ConfigurationProperties(prefix = "ordering")
public class OrderingServiceProperties
{
    private String storage = "memory";
    private String pingMessage = "The ordering service is available";
    private String validationMessage = "Version unknown";
    private String instrumentationKey = "";

    public String getStorage()
    {
        return storage;
    }

    public void setStorage(String storage)
    {
        this.storage = storage;
    }

    public String getValidationMessage()
    {
        return validationMessage;
    }

    public void setValidationMessage(String validationMessage)
    {
        this.validationMessage = validationMessage;
    }

    public String getPingMessage()
    {
        return pingMessage;
    }

    public void setPingMessage(String message)
    {
        this.pingMessage = message;
    }

    public String getInstrumentationKey()
    {
        return instrumentationKey;
    }

    public void setInstrumentationKey(String instrumentationKey)
    {
        this.instrumentationKey = instrumentationKey;
    }

}
