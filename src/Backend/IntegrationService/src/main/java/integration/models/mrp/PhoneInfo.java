package integration.models.mrp;

public class PhoneInfo {
    private String phoneNumber;
    private String kind;

    public PhoneInfo() {

    }

    public PhoneInfo(String phoneNumber) {
        setPhoneNumber(phoneNumber);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
