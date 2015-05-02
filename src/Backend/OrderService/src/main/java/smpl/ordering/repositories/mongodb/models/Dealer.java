package smpl.ordering.repositories.mongodb.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import smpl.ordering.models.DealerInfo;

@Document(collection = "dealers")
public class Dealer
{
    @Id
    private String id;

    @Indexed
    private String name;
    private String contact;
    private String address;
    private String email;
    private String phone;

    public Dealer()
    {
    }

    public Dealer(DealerInfo from)
    {
        this.name = from.getName();
        this.contact = from.getContact();
        this.address = from.getAddress();
        this.email = from.getEmail();
        this.phone = from.getPhone();
    }

    public DealerInfo toDealerInfo()
    {
        DealerInfo result = new DealerInfo();
        result.setName(name);
        result.setContact(contact);
        result.setAddress(address);
        result.setEmail(email);
        result.setPhone(phone);
        return result;
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

}
