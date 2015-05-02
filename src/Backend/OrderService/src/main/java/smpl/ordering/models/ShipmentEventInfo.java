package smpl.ordering.models;

public class ShipmentEventInfo
{
    public ShipmentEventInfo()
    {
    }

    public ShipmentEventInfo(String date, String comments)
    {
        this.date = date;
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

    public String getComments()
    {
        return comments;
    }

    public void setComments(String comments)
    {
        this.comments = comments;
    }

    public String validate()
    {
        StringBuilder builder = new StringBuilder();
        boolean ok = true;

        if (comments == null || comments.isEmpty())
        {
            builder.append("No or shipment event comment\n");
            ok = false;
        }
        return ok ? null : builder.toString();
    }

    private String date;
    private String comments;
}
