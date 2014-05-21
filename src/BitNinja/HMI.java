package BitNinja;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Map;

public abstract class HMI implements EventProvider {
    protected Queue<Event> eventQueue = new ConcurrentLinkedQueue<Event>();

    public void init() {
    }

    public void run() {
    }

    public void log(Event e) {
    }

    public void info(ClientFiles clientFiles) {
    }

    public void search(Map<String,String[]> result) {
    }

    public void exit() {
    }

    public void eventInfo() {
        Event event = new Event(EventSource.HMI, EventType.HMI_INFO);
        eventQueue.add(event);
    }

    public void eventSearch(String query) {
        Event event = new Event(EventSource.HMI, EventType.HMI_SEARCH);
        event.addAttribute("query", query);
        eventQueue.add(event);
    }

    public void eventDownload(String file) {
        Event event = new Event(EventSource.HMI, EventType.HMI_DOWNLOAD);
        event.addAttribute("file", file);
        eventQueue.add(event);
    }

    public void eventTrackerConnecting() {
    }

    public void eventTrackerConnected(Event event) {
    }

    public void eventTrackerConnectionError(Event event) {
    }

    public void eventExit() {
        Event event = new Event(EventSource.HMI, EventType.HMI_EXIT);
        eventQueue.add(event);
    }

    public Event getEvent() {
        return eventQueue.poll();
    }

    public boolean eventAvailable() {
        return !eventQueue.isEmpty();
    }

}