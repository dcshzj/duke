import java.util.Date;

public class Event extends Task {
    protected Date from;
    protected Date to;

    public Event(String description, Date from, Date to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (at: " + this.from + " - " + this.to + ")";
    }
}
