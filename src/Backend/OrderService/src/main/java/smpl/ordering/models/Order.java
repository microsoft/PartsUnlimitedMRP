package smpl.ordering.models;

import smpl.ordering.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an agreed-upon order of a refrigeration or freezer room,
 * along with any catalog item, such as shelving and cooling equipment.
 */
public class Order
{
    private String orderId;
    private String quoteId;
    private String orderDate;
    private OrderStatus status;
    private List<OrderEventInfo> events;

    public String validate()
    {
        int count = 0;
        StringBuilder errors = new StringBuilder("{\"errors\": [");
        count = Utility.validateStringField(quoteId, "quoteId", count, errors);
        count = Utility.validateStringField(orderDate, "orderDate", count, errors);
        errors.append("]}");

        return (count > 0) ? errors.toString() : null;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (!events.equals(order.events)) return false;
        if (!orderDate.equals(order.orderDate)) return false;
        return orderId.equals(order.orderId) && quoteId.equals(order.quoteId) && status == order.status;
    }

    @Override
    public int hashCode()
    {
        int result = orderId.hashCode();
        result = 31 * result + quoteId.hashCode();
        result = 31 * result + orderDate.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + events.hashCode();
        return result;
    }

    public Order()
    {
        events = new ArrayList();
        status = OrderStatus.None;
    }

    public String getQuoteId()
    {
        return quoteId;
    }

    public void setQuoteId(String quoteId)
    {
        this.quoteId = quoteId;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getOrderDate()
    {
        return orderDate;
    }

    public void setOrderDate(String orderDate)
    {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus()
    {
        return status;
    }

    public void setStatus(OrderStatus status)
    {
        this.status = status;
    }

    public List<OrderEventInfo> getEvents()
    {
        return events;
    }

    public void setEvents(List<OrderEventInfo> events)
    {
        this.events = events;
    }

    public void addEvent(OrderEventInfo event)
    {
        events.add(event);
    }
}
