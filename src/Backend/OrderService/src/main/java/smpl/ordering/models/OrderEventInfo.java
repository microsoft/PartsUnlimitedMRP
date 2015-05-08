package smpl.ordering.models;

import java.text.DateFormat;
import java.util.Date;

/**
 * Information on order-related events (comments).
 */
public class OrderEventInfo
{
    private String date;
    private String comments;

    public OrderEventInfo()
    {
    }

    public OrderEventInfo(String comments)
    {
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        this.setDate(df.format(new Date()));
        this.setComments(comments);
    }

    public OrderEventInfo(String date, String comments)
    {
        this.setDate(date);
        this.setComments(comments);
    }

    public String getComments()
    {
        return comments;
    }

    public void setComments(String comments)
    {
        this.comments = comments;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }
}
