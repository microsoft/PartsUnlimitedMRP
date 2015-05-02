package smpl.ordering;

import com.microsoft.applicationinsights.TelemetryClient;
import org.springframework.context.ApplicationContext;

public class Utility
{
    public static int validateStringField(String field, String fieldName, int count, StringBuilder errors)
    {
        if (isNullOrEmpty(field))
        {
            if (count == 0)
            {
                errors.append(String.format("\"Empty %s field\"", fieldName));
            }
            else
            {
                errors.append(String.format(",\"Empty %s field\"", fieldName));
            }
            count += 1;
        }
        return count;
    }

    public static boolean isNullOrEmpty(String str)
    {
        return str == null || str.isEmpty();
    }

    public static TelemetryClient getTelemetryClient()
    {
        ApplicationContext ctx = OrderingConfiguration.getApplicationContext();
        if (ctx == null) return null;
        return ctx.getBean(TelemetryClient.class);
    }
}
