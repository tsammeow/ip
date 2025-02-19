package duke;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A Deadline-type task.
 */
public class Deadline extends Task {
    private LocalDate deadline;

    /**
     * Creates a new Deadline.
     * @param details What needs to be done.
     * @param deadline When the deadline is.
     */
    public Deadline(String details, String deadline) {
        super(details);
        this.deadline = LocalDate.parse(deadline);
    }

    @Override
    public String getTaskIcon() {
        return "D";
    }

    @Override
    public String getDetails() {
        return details + " (by: " + deadline.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + ")";
    }

    @Override
    public String getEncodedDetails() {
        return details + "|" + deadline;
    }
}
