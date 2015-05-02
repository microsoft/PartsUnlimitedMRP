package smpl.ordering.models;

/**
 * Information regarding phone numbers for individuals related
 * to quotes, orders, and shipments.
 */
public class PhoneInfo
{
    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getKind()
    {
        return kind;
    }

    public void setKind(String kind)
    {
        this.kind = kind;
    }

    public PhoneInfo()
    {
    }

    public PhoneInfo(String phoneNumber, String kind)
    {
        this.phoneNumber = phoneNumber;
        this.kind = kind;
    }

    private String phoneNumber;
    private String kind;
}
