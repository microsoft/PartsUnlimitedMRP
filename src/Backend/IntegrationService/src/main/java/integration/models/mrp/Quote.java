package integration.models.mrp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import integration.models.website.OrderItem;
import integration.models.website.OrderMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Quote {
    private String quoteId;
    private String customerName;
    private String dealerName;
    private String validUntil;
    private String city;
    private String postalCode;
    private String state;
    private double totalCost;
    private double discount;
    private List<QuoteItemInfo> quoteItems;

    public Quote() {
    }

    public Quote(OrderMessage message) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 1);
    
        this.customerName = message.getCustomerName();
        this.dealerName = "Website";
        this.setCity(message.getCity());
        this.postalCode = message.getPostalCode();
        this.state = message.getState();
        this.totalCost = message.getTotalCost();
        this.discount = message.getDiscount();
        this.validUntil = dateFormat.format(c);
        this.quoteItems = new ArrayList<QuoteItemInfo>();
        
        for (OrderItem orderItem : message.getItems()){
            QuoteItemInfo quoteItem = new QuoteItemInfo(orderItem);
            this.quoteItems.add(quoteItem);
        }
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<QuoteItemInfo> getQuoteItems() {
        return quoteItems;
    }

    public void setQuoteItems(List<QuoteItemInfo> quoteItems) {
        this.quoteItems = quoteItems;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }
}

