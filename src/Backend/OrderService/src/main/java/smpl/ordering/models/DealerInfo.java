package smpl.ordering.models;

import smpl.ordering.Utility;

/**
 * Represents the information stored about an individual dealer.
 */
public class DealerInfo
{
    private String name;
    private String contact;
    private String address;
    private String email;
    private String phone;

    public DealerInfo()
    {
    }

    public DealerInfo(String name)
    {
        this.name = name;
    }

    public DealerInfo(DealerInfo other)
    {
        this.name = other.name;
        this.contact = other.contact;
        this.address = other.address;
        this.email = other.email;
        this.phone = other.phone;
    }

    public String validate()
    {
        int count = 0;
        StringBuilder errors = new StringBuilder("{\"errors\": [");
        count = Utility.validateStringField(name, "name", count, errors);
        errors.append("]}");

        return (count > 0) ? errors.toString() : null;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getContact()
    {
        return contact;
    }

    public void setContact(String contact)
    {
        this.contact = contact;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }
}

