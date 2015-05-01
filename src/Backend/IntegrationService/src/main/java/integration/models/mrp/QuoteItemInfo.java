package integration.models.mrp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import integration.models.website.OrderItem;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuoteItemInfo {
    private String skuNumber;
    private double amount;

    public QuoteItemInfo(){
    }

    public QuoteItemInfo(OrderItem orderItem){
        setSkuNumber(orderItem.getSkuNumber());
        setAmount(orderItem.getPrice());
    }

    public String getSkuNumber() {
        return skuNumber;
    }

    public void setSkuNumber(String skuNumber) {
        this.skuNumber = skuNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
