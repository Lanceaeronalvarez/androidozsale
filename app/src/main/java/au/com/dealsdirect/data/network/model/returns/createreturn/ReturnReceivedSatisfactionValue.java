package au.com.dealsdirect.data.network.model.returns.createreturn;

public enum ReturnReceivedSatisfactionValue {
    GOOD(30),
    NEUTRAL(20),
    BAD(10);

    private int value;

    ReturnReceivedSatisfactionValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
