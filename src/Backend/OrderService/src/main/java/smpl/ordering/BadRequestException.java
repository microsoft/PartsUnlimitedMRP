package smpl.ordering;

/**
 * Communicating a bad REST request from a called API to the controller, which should use
 * it to create a BAD_REQUEST HTTP response.
 */
public class BadRequestException
        extends Exception
{
    public BadRequestException(String message)
    {
        super(message);
    }
}
