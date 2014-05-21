package BitNinja;

import java.util.jar.Attributes;

enum EventSource {
    HMI,
    SERVER,
    CLIENT
};

enum EventType {
    HMI_SEARCH,
    HMI_DOWNLOAD,
    HMI_PAUSE_FILE,
    HMI_PAUSE_ALL,
    HMI_RESUME_FILE,
    HMI_RESUME_ALL,
    HMI_DELETE_FILE,
    HMI_INFO,
    HMI_EXIT,
    TRACKER_CONNECTED,
    TRACKER_CONNECTION_ERROR,
    LOG_INFO,
    LOG_ERROR
}

public class Event {
    public EventSource source;
    public EventType type;
    private Attributes attributes = new Attributes();

    public Event(EventSource _source, EventType _type) {
        source = _source;
        type = _type;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void addAttribute(String key, String value) {
        attributes.put(new Attributes.Name(key), value);
    }
}