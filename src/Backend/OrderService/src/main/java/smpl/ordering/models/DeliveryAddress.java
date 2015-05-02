package smpl.ordering.models;

public class DeliveryAddress
{
    @SuppressWarnings("SameParameterValue")
    public DeliveryAddress(String street, String city, String state, String postalCode, String specialInstructions)
    {
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.specialInstructions = specialInstructions;
    }

    public DeliveryAddress()
    {
    }

    public String getStreet()
    {
        return street;
    }

    public void setStreet(String street)
    {
        this.street = street;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
    }

    public String getSpecialInstructions()
    {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions)
    {
        this.specialInstructions = specialInstructions;
    }

    public boolean validate()
    {
        return isNotEmpty(city) && isNotEmpty(postalCode);
    }

    private static boolean isNotEmpty(String str)
    {
        return str != null && !str.isEmpty();
    }

    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String specialInstructions;
}
