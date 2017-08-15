package au.com.dealsdirect.service.ourpay;

/*
 * Created by CodeineBot on 9/27/16.
 */

public class OurpayTransaction {

    private int number;
    private String amount;
    private String plannedDate;
    private int state;

    public OurpayTransaction(int number, String amount, String plannedDate, int state) {
        this.number = number;
        this.amount = amount;
        this.plannedDate = plannedDate;
        this.state = state;
    }

    public int getNumber() {
        return number;
    }

    public String getAmount() {
        return amount;
    }

    public String getPlannedDate() {
        return plannedDate;
    }

    public int getState() {
        return state;
    }
}
