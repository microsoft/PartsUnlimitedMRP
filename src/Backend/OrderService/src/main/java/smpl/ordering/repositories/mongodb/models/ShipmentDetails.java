package smpl.ordering.repositories.mongodb.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import smpl.ordering.models.DeliveryAddress;
import smpl.ordering.models.PhoneInfo;
import smpl.ordering.models.ShipmentEventInfo;
import smpl.ordering.models.ShipmentRecord;

@Document(collection = "shipments")
public class ShipmentDetails
{
    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Id
    private String id;

    @Indexed
    private String orderId;

    private ShipmentEventInfo[] events;

    private DeliveryAddress deliveryAddress;

    private String contactName;

    private PhoneInfo primaryContactPhone;

    private PhoneInfo alternateContactPhone;

    public ShipmentDetails()
    {
    }

    public ShipmentDetails(ShipmentRecord from)
    {
        this.orderId = from.getOrderId();
        this.events = (from.getEvents() != null) ?
                from.getEvents().toArray(new ShipmentEventInfo[from.getEvents().size()]) :
                new ShipmentEventInfo[0];
        this.deliveryAddress = from.getDeliveryAddress();
        this.contactName = from.getContactName();
        this.primaryContactPhone = from.getPrimaryContactPhone();
        this.alternateContactPhone = from.getAlternateContactPhone();
    }

    public ShipmentRecord toShipmentRecord()
    {
        ShipmentRecord result = new ShipmentRecord();
        result.setOrderId(orderId);
        result.setDeliveryAddress(deliveryAddress);
        result.setPrimaryContactPhone(primaryContactPhone);
        result.setContactName(contactName);
        result.setAlternateContactPhone(alternateContactPhone);
        if (events != null)
        {
            for (ShipmentEventInfo info : events)
            {
                result.addEvent(info);
            }
        }
        return result;
    }
}
