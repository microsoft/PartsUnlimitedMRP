package smpl.ordering.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents data fpr a particular shipment. Each order corresponds to no more
 * than one shipment record. In other words, shipments are consolidated.
 */
public class ShipmentRecord
{
    /**
     * The order id is also the key of the shipment record.
     *
     * @return The order/shipment identity
     */
    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getDeliveryDate()
    {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate)
    {
        this.deliveryDate = deliveryDate;
    }

    public List<ShipmentEventInfo> getEvents()
    {
        return events;
    }

    public void addEvent(ShipmentEventInfo event)
    {
        this.events.add(event);
    }

    @SuppressWarnings("SameParameterValue")
    public void addEvent(String date, String comments)
    {
        this.events.add(new ShipmentEventInfo(date, comments));
    }

    public DeliveryAddress getDeliveryAddress()
    {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryAddress deliveryAddress)
    {
        this.deliveryAddress = deliveryAddress;
    }

    public String getContactName()
    {
        return contactName;
    }

    public void setContactName(String contactName)
    {
        this.contactName = contactName;
    }

    public PhoneInfo getPrimaryContactPhone()
    {
        return primaryContactPhone;
    }

    public void setPrimaryContactPhone(PhoneInfo primaryContactPhone)
    {
        this.primaryContactPhone = primaryContactPhone;
    }

    public PhoneInfo getAlternateContactPhone()
    {
        return alternateContactPhone;
    }

    public void setAlternateContactPhone(PhoneInfo alternateContactPhone)
    {
        this.alternateContactPhone = alternateContactPhone;
    }

    public ShipmentRecord()
    {
        this.events = new ArrayList<>();
    }

    public ShipmentRecord(ShipmentRecord other)
    {
        orderId = other.orderId;
        deliveryDate = other.deliveryDate;
        deliveryAddress = other.deliveryAddress;
        contactName = other.contactName;
        primaryContactPhone = other.primaryContactPhone;
        alternateContactPhone = other.alternateContactPhone;
        events = new ArrayList<>();
        for (ShipmentEventInfo event : other.events)
        {
            events.add(new ShipmentEventInfo(event.getDate(), event.getComments()));
        }
    }

    public String validate()
    {
        StringBuilder bldr = new StringBuilder();
        boolean ok = true;

        if (orderId == null || orderId.isEmpty())
        {
            bldr.append("No order id\n");
            ok = false;
        }
        if (deliveryDate == null || deliveryDate.isEmpty())
        {
            bldr.append("No delivery Date\n");
            ok = false;
        }
        if (deliveryAddress == null || !deliveryAddress.validate())
        {
            bldr.append("No or incomplete delivery address\n");
            ok = false;
        }
        if (contactName == null || contactName.isEmpty() || primaryContactPhone == null)
        {
            bldr.append("Contact information missing\n");
            ok = false;
        }

        return ok ? null : bldr.toString();
    }

    private String orderId;

    private String deliveryDate;

    private final List<ShipmentEventInfo> events;

    private DeliveryAddress deliveryAddress;

    private String contactName;

    private PhoneInfo primaryContactPhone;

    private PhoneInfo alternateContactPhone;
}

