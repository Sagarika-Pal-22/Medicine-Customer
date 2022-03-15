package myrehabcare.in.Classes;

public class Chats {

    private String message;
    private long time;
    private boolean toDr;


    public Chats() {
    }

    public Chats(String message, long time, boolean toDr) {
        this.message = message;
        this.time = time;
        this.toDr = toDr;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isToDr() {
        return toDr;
    }

    public void setToDr(boolean toDr) {
        this.toDr = toDr;
    }

    @Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof Chats){
            Chats ptr = (Chats) v;
            retVal = ptr.getTime() == this.getTime();
        }

        return retVal;
    }
}
