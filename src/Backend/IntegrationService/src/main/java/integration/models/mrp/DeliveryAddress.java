package integration.models.mrp;

public class DeliveryAddress {

    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String specialInstructions;

    public DeliveryAddress() {
    }

    public DeliveryAddress(String street, String city, String state, String postalCode) {
        setStreet(street);
        setCity(city);
        setState(state);
        setPostalCode(postalCode);
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }
}
