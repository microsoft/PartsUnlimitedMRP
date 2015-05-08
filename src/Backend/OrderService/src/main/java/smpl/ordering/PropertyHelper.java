package smpl.ordering;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Helper class for getting data out of the 'application.properties' file found on the
 * class path.
 */
public class PropertyHelper
{
    public static Properties getPropValues(String propFileName) throws IOException
    {

        Properties props = new Properties();

        InputStream inputStream = PropertyHelper.class.getClassLoader().getResourceAsStream(propFileName);
        props.load(inputStream);
        if (inputStream == null)
        {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        return props;
    }

    public static Properties getProperties()
    {
        return s_props;
    }

    static
    {
        try
        {
            s_props = getPropValues("application.properties");
        }
        catch (IOException e)
        {
            s_props = new Properties();
        }
    }


    private static Properties s_props;
}
