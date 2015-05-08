package smpl.ordering.models;

import smpl.ordering.Utility;

/**
 * Represents the aggregate information stored about delivery (Quote, Order, Shipment).
 */
public class Delivery {
    private Quote quote;
    private Order order;
    private ShipmentRecord shipmentRecord;

    public Quote getQuote() {
        return this.quote;
    }

    public void setQuote(Quote quote) {
        this.quote = quote;
    }

    public Order getOrder() {
        return this.order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public ShipmentRecord getShipmentRecord() {
        return this.shipmentRecord;
    }

    public void setShipmentRecord(ShipmentRecord shipmentRecord) {
        this.shipmentRecord = shipmentRecord;
    }
}
