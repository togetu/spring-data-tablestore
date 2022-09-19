package in.togetu.tablestore.repository.bean;

public class KeyRange {

    private Key from;
    private Key to;

    public Key getFrom() {
        return from;
    }

    public void setFrom(Key from) {
        this.from = from;
    }

    public Key getTo() {
        return to;
    }

    public void setTo(Key to) {
        this.to = to;
    }
}
