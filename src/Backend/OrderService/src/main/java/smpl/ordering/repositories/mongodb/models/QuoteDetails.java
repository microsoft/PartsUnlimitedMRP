package smpl.ordering.repositories.mongodb.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import smpl.ordering.models.QuoteItemInfo;
import smpl.ordering.models.Quote;

import java.util.List;

@Document(collection = "quotes")
public class QuoteDetails
{
    @Id
    private String id;

    public String getQuoteId()
    {
        return quoteId;
    }

    @Indexed
    private String quoteId;

    private String validUntil;

    private String customerName;

    public String getDealerName()
    {
        return dealerName;
    }

    @Indexed
    private String dealerName;
    private QuoteItemInfo[] quoteItems;
    private double totalCost;
    private double discount;
    private String city;
    private String postalCode;
    private String state;

    public QuoteDetails()
    {
    }

    public QuoteDetails(Quote from)
    {
        this.quoteId = from.getQuoteId();
        this.validUntil = from.getValidUntil();
        this.customerName = from.getCustomerName();
        this.dealerName = from.getDealerName();
        this.totalCost = from.getTotalCost();
        this.discount = from.getDiscount();
        this.city = from.getCity();
        this.postalCode = from.getPostalCode();
        this.state = from.getState();

        List<QuoteItemInfo> ai = from.getQuoteItems();
        this.quoteItems = (ai != null && ai.size() > 0) ?
                ai.toArray(new QuoteItemInfo[ai.size()]) :
                new QuoteItemInfo[0];
    }

    public Quote toQuote()
    {
        Quote result = new Quote();
        result.setQuoteId(quoteId);
        result.setValidUntil(validUntil);
        result.setCustomerName(customerName);
        result.setDealerName(dealerName);
        result.setTotalCost(totalCost);
        result.setDiscount(discount);
        result.setCity(city);
        result.setPostalCode(postalCode);
        result.setState(state);
        if (quoteItems != null)
        {
            for (QuoteItemInfo item : quoteItems)
            {
                result.addQuoteItem(item.getSkuNumber(), item.getAmount());
            }
        }
        return result;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }
}
