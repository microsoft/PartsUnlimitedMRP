package smpl.ordering.models;

import smpl.ordering.Utility;

import java.util.*;

/**
 * Represents quote information sent from the service to the client.
 *
 * @see smpl.ordering.models.Order
 */
public class Quote
{
    private String quoteId;
    private String validUntil;
    private String customerName;
    private String dealerName;
    private List<QuoteItemInfo> quoteItems;
    private double totalCost;
    private double discount;
    private String city;
    private String postalCode;
    private String state;

    public Quote()
    {
    }

    public Quote(Quote quote) {
        this.quoteId = quote.quoteId;
        this.customerName = quote.getCustomerName();
        this.dealerName = quote.getDealerName();
        this.validUntil = quote.getValidUntil();
        this.totalCost = quote.getTotalCost();
        this.discount = quote.getDiscount();
        this.city = quote.getCity();
        this.postalCode = quote.getPostalCode();
        this.state = quote.getState();
        this.quoteItems = quote.getQuoteItems();
    }

    public String validate()
    {
        int count = 0;
        StringBuilder errors = new StringBuilder("{\"errors\": [");
        count = Utility.validateStringField(dealerName, "dealerName", count, errors);
        count = Utility.validateStringField(customerName, "customerName", count, errors);
        errors.append("]}");

        return (count > 0) ? errors.toString() : null;
    }

    public String getQuoteId()
    {
        return quoteId;
    }

    public void setQuoteId(String quoteId)
    {
        this.quoteId = quoteId;
    }

    public String getDealerName()
    {
        return dealerName;
    }

    public void setDealerName(String dealerName)
    {
        this.dealerName = dealerName;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public String getValidUntil()
    {
        return validUntil;
    }

    public void setValidUntil(String validUntil)
    {
        this.validUntil = validUntil;
    }

    public List<QuoteItemInfo> getQuoteItems()
    {
        return quoteItems;
    }

    public void setQuoteItems(List<QuoteItemInfo> quoteItems)
    {
        this.quoteItems = quoteItems;
    }

    /**
     * Adds an item to the quote items list.
     *
     * @param sku        The item sku number
     * @param amount     The amount or number of units quoted.
     */
    public void addQuoteItem(String sku, double amount)
    {
        if (quoteItems == null)
        {
            this.quoteItems = new ArrayList<QuoteItemInfo>();
        }
        quoteItems.add(new QuoteItemInfo(sku, amount));
    }

    /**
     * Gets the overall cost of the quote, before any discount is applied.
     */
    public double getTotalCost()
    {
        return totalCost;
    }

    /**
     * Sets the overall cost of the quote, before any discount is applied.
     */
    public void setTotalCost(double totalCost)
    {
        this.totalCost = totalCost;
    }

    /**
     * Gets the overall discount, as an amount (not percentage).
     */
    public double getDiscount()
    {
        return discount;
    }

    /**
     * Sets the overall discount, as an amount (not percentage).
     */
    public void setDiscount(double discount)
    {
        this.discount = discount;
    }

    /**
     * Gets city where the unit is to be delivered.
     * Used to estimate delivery costs and for capturing local regulatory purposes.
     */
    public String getCity()
    {
        return city;
    }

    /**
     * Sets city where the unit is to be delivered.
     * Used to estimate delivery costs and for capturing local regulatory purposes.
     */
    public void setCity(String city)
    {
        this.city = city;
    }

    /**
     * Gets postal code where the unit is to be delivered.
     * Used to estimate delivery costs and for capturing local regulatory purposes.
     */
    public String getPostalCode()
    {
        return postalCode;
    }

    /**
     * Sets postal code where the unit is to be delivered.
     * Used to estimate delivery costs and for capturing local regulatory purposes.
     */
    public void setPostalCode(String postalCode)
    {
        this.postalCode = postalCode;
    }

    /**
     * Gets state where the unit is to be delivered.
     * Used to estimate delivery costs and for capturing local regulatory purposes.
     */
    public String getState()
    {
        return state;
    }

    /**
     * Sets state where the unit is to be delivered.
     * Used to estimate delivery costs and for capturing local regulatory purposes.
     */
    public void setState(String state)
    {
        this.state = state;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Quote quote = (Quote) o;


        if (Double.compare(quote.totalCost, totalCost) != 0) return false;
        if (city != null ? !city.equals(quote.city) : quote.city != null) return false;
        if (customerName != null ? !customerName.equals(quote.customerName) : quote.customerName != null) return false;
        if (dealerName != null ? !dealerName.equals(quote.dealerName) : quote.dealerName != null) return false;
        if (postalCode != null ? !postalCode.equals(quote.postalCode) : quote.postalCode != null) return false;
        if (quoteId != null ? !quoteId.equals(quote.quoteId) : quote.quoteId != null) return false;
        if (state != null ? !state.equals(quote.state) : quote.state != null) return false;
        if (validUntil != null ? validUntil.equals(quote.validUntil) : quote.validUntil != null) return false;

        if (quoteItems.size() != quote.quoteItems.size()) return false;

        if (quoteItems.size() > 0)
        {
            QuoteItemInfo arr1[] = new QuoteItemInfo[quoteItems.size()];
            QuoteItemInfo arr2[] = new QuoteItemInfo[quote.quoteItems.size()];

            quoteItems.toArray(arr1);
            quote.quoteItems.toArray(arr2);

            Arrays.sort(arr1);
            Arrays.sort(arr2);

            if (!Arrays.equals(arr1, arr2)) return false;
        }
        // if all conditions are true return true.
        return true;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = quoteId != null ? quoteId.hashCode() : 0;
        result = 31 * result + (validUntil != null ? validUntil.hashCode() : 0);
        result = 31 * result + (customerName != null ? customerName.hashCode() : 0);
        result = 31 * result + (dealerName != null ? dealerName.hashCode() : 0);
        result = 31 * result + (quoteItems != null ? quoteItems.hashCode() : 0);
        temp = Double.doubleToLongBits(totalCost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(discount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        return result;
    }


}
