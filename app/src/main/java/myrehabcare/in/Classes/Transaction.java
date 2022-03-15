package myrehabcare.in.Classes;

import java.util.Date;

public class Transaction {

    private String toName;
    private String amount;
    private Date date;
    private String status;

    public Transaction(String toName, String amount, Date date, String status) {
        this.toName = toName;
        this.amount = amount;
        this.date = date;
        this.status = status;
    }

    public Transaction(){}

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
