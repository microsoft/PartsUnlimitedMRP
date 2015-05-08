package smpl.ordering.models;

import java.text.DateFormat;
import java.util.Date;

/**
 * Order update record
 */
@SuppressWarnings("SameParameterValue")
public class OrderUpdateInfo
{
    private OrderStatus status;
    private OrderEventInfo eventInfo;

    public OrderUpdateInfo()
    {
    }

    public OrderUpdateInfo(OrderStatus status, String comments)
    {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        this.status = status;
        this.eventInfo = new OrderEventInfo(df.format(new Date()), comments);
    }

    public OrderStatus getStatus()
    {
        return status;
    }

    public void setStatus(OrderStatus status)
    {
        this.status = status;
    }

    public OrderEventInfo getEventInfo()
    {
        return eventInfo;
    }

    public void setEventInfo(OrderEventInfo eventInfo)
    {
        this.eventInfo = eventInfo;
    }
}
