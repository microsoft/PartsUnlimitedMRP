package integration.models.mrp;

import integration.models.website.OrderMessage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

public class ShipmentRecord {
    private String orderId;
    private String deliveryDate;
    private List<ShipmentEventInfo> events;
    private DeliveryAddress deliveryAddress;
    private String contactName;
    private PhoneInfo primaryContactPhone;
    private PhoneInfo alternateContactPhone;

    public ShipmentRecord() {
        this.setEvents(new ArrayList());
    }

    public ShipmentRecord(OrderMessage message, String orderId) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 14);
        
        setEvents(new ArrayList());
        setOrderId(orderId);
        setDeliveryDate(dateFormat.format(c));
        setDeliveryAddress(new DeliveryAddress(message.getAddress(), message.getCity(), message.getState(), message.getPostalCode()));
        setContactName(message.getCustomerName());
        setPrimaryContactPhone(new PhoneInfo(message.getPhone()));
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public List<ShipmentEventInfo> getEvents() {
        return events;
    }

    public void setEvents(List<ShipmentEventInfo> events) {
        this.events = events;
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public PhoneInfo getPrimaryContactPhone() {
        return primaryContactPhone;
    }

    public void setPrimaryContactPhone(PhoneInfo primaryContactPhone) {
        this.primaryContactPhone = primaryContactPhone;
    }

    public PhoneInfo getAlternateContactPhone() {
        return alternateContactPhone;
    }

    public void setAlternateContactPhone(PhoneInfo alternateContactPhone) {
        this.alternateContactPhone = alternateContactPhone;
    }
}
