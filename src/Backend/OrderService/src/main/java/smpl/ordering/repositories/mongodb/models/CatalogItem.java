package smpl.fabrikant.ordering.repositories.mongodb.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "catalog")
public class CatalogItem
{
    public CatalogItem()
    {
    }

    public CatalogItem(smpl.fabrikant.ordering.models.CatalogItem from)
    {
        this.skuNumber = from.getSkuNumber();
        this.description = from.getDescription();
        this.price = from.getPrice();
        this.inventory = from.getInventory();
        this.leadTime = from .getLeadTime();
    }

    public smpl.fabrikant.ordering.models.CatalogItem toCatalogItem()
    {
        int calculatedLeadTime = (inventory > 0 ) ? 0 : leadTime;

        smpl.fabrikant.ordering.models.CatalogItem result = new smpl.fabrikant.ordering.models.CatalogItem();
        result.setSkuNumber(skuNumber);
        result.setDescription(description);
        result.setPrice(price);
        result.setInventory(inventory);
        result.setLeadTime(calculatedLeadTime);
        return result;
    }

    @Id
    private String id;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Indexed
    private String skuNumber;
    private String description;
    private double price;
    private int inventory;
    private int leadTime;
}
