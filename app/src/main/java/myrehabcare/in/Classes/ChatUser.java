package myrehabcare.in.Classes;

public class ChatUser {
    private String name;
    private String type;
    private String id;
    private String email;
    private String phone;

    public ChatUser(String name, String type, String id, String email, String phone) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.email = email;
        this.phone = phone;
    }

    public ChatUser() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
