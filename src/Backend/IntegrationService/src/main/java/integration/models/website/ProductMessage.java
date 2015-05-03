package integration.models.website;

import integration.models.mrp.CatalogItem;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to describe the list of products for the website to update.
 */
public class ProductMessage {

    private List<ProductItem> productList;

    public ProductMessage(){
        setProductList(new ArrayList<ProductItem>());
    }

    public ProductMessage(List<CatalogItem> catalogItems) {
        this();
        for(CatalogItem catalogItem: catalogItems){
            ProductItem inventoryItem = new ProductItem(catalogItem);
            this.productList.add(inventoryItem);
        }
    }

    public List<ProductItem> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductItem> productList) {
        this.productList = productList;
    }
}
