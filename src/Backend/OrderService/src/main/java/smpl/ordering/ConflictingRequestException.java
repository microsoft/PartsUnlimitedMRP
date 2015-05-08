package smpl.ordering;

/**
 * Communicating a conflicting REST request from a called API to the controller, which should use
 * it to create a CONFLICT HTTP response.
 */
public class ConflictingRequestException
        extends BadRequestException
{
    public ConflictingRequestException(String message)
    {
        super(message);
    }
}
