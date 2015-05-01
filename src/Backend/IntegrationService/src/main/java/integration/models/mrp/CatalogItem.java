package integration.models.mrp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CatalogItem {
    private String skuNumber;
    private String description;
    private String unit;
    private String price;
    private int inventory;
    private int leadTime;

    public String getSkuNumber() {
        return skuNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getUnit() {
        return unit;
    }

    public String getPrice() {
        return price;
    }

    public int getInventory() {
        return inventory;
    }

    public int getLeadTime() {
        return leadTime;
    }
}
