package integration.models.website;

/**
 * This class is used to describe a product of the order from the website.
 */
public class OrderItem {
    private String skuNumber;
    private double price;

    public String getSkuNumber() {
        return skuNumber;
    }

    public void setSkuNumber(String skuNumber) {
        this.skuNumber = skuNumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
