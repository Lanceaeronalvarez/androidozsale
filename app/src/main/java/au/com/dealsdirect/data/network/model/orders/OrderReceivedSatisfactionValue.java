package au.com.dealsdirect.data.network.model.orders;

public enum OrderReceivedSatisfactionValue {
    GOOD(30),
    NEUTRAL(20),
    BAD(10);

    private int value;

    OrderReceivedSatisfactionValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
