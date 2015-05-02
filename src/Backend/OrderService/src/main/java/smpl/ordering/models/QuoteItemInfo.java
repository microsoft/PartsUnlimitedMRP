package smpl.ordering.models;

/**
 * Represents a line item of catalog item products, coming from the client to the service
 *
 * @see QuoteItemInfo
 * @see smpl.ordering.models.CatalogItem
 */
public class QuoteItemInfo
        implements Comparable<QuoteItemInfo>
{
    private String skuNumber;
    private double amount;

    public QuoteItemInfo()
    {
    }

    public QuoteItemInfo(String sku, double amount)
    {
        this.skuNumber = sku;
        this.amount = amount;
    }

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") QuoteItemInfo other)
    {
        return skuNumber.compareTo(other.skuNumber);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuoteItemInfo itemInfo = (QuoteItemInfo) o;

        return Double.compare(itemInfo.amount, amount) == 0 && skuNumber.equals(itemInfo.skuNumber);
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        result = skuNumber.hashCode();
        temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String getSkuNumber()
    {
        return skuNumber;
    }

    public void setSkuNumber(String sku)
    {
        this.skuNumber = sku;
    }

    /**
     * Gets the amount of the item that is quoted / ordered.
     */
    public double getAmount()
    {
        return amount;
    }

    /**
     * Sets the amount of the item that is quoted / ordered.
     */
    public void setAmount(double amount)
    {
        this.amount = amount;
    }
}
