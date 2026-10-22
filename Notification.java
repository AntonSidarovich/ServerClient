import java.io.Serializable;
import java.time.LocalDateTime;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String text;
    private final LocalDateTime sendTime;

    public Notification(String text, LocalDateTime sendTime) {
        this.text = text;
        this.sendTime = sendTime;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }
}