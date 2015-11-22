package yoPohApi;

/**
 * Created by ayushb on 20/11/15.
 */
public class GcmMessage {
    private static final long serialVersionUID = 7526472295622776147L;
    String gcmRegId;
    String message;
    private String channel;

    public String getGcmRegId() {
        return gcmRegId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setGcmRegId(String gcmRegId) {
        this.gcmRegId = gcmRegId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
