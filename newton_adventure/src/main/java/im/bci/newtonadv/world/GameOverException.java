package im.bci.newtonadv.world;

public class GameOverException extends Throwable {
    private String why;
    
    public GameOverException(String why) {
        this.why = why;    
    }

    public String getWhy() {
        return why;
    }
}