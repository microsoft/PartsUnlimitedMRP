package integration.models.website;

import integration.models.mrp.CatalogItem;

/**
 * This class is used to describe the updates to the website product.
 */
public class ProductItem {

    private String skuNumber;
    private int inventory;
    private int leadTime;

    public ProductItem(){

    }

    public ProductItem(CatalogItem catalogItem){
        setInventory(catalogItem.getInventory());
        setSkuNumber(catalogItem.getSkuNumber());
        setLeadTime(catalogItem.getLeadTime());
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public String getSkuNumber() {
        return skuNumber;
    }

    public void setSkuNumber(String skuNumber) {
        this.skuNumber = skuNumber;
    }

    public int getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(int leadTime) {
        this.leadTime = leadTime;
    }
}
