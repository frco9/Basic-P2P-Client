package BitNinja;

import java.util.Queue;

public interface EventProvider {

    public Event getEvent();

    public boolean eventAvailable();
}