package BitNinja;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;

public class CLI extends HMI {
    private BufferedReader input;
    private Thread runThread;
    private boolean running = true;
    private Attributes listDownload;
    private boolean writeLock = false;

    public void init() {
        // Welcome message
        System.out.println("Hello, welcome to BitNinja!");
        System.out.println("Please type a command, or \"help\" for some ideas");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Prints "Exiting" only when exited via SIGINT
                if (running) {
                    System.out.println("Exiting");
                }
                // System.exit(0);
            }
        });

        listDownload = new Attributes();
    }

    public void run() {
        // Init the command line
        input = new BufferedReader(new InputStreamReader(System.in));

        // Create the thread
        runThread = new Thread() {
            @Override
            public void run() {
                String inputLine;
                String line[] = {};

                // Read lines on the command line
                while (running) {
                    // Print the prompt
                    printPrompt();
                    try {
                        inputLine = input.readLine();
                        if (inputLine == null) {
                            System.out.println("exit");
                            inputLine = "exit";
                        }
                        line = inputLine.split(" ");
                    } catch(IOException e) {
                        System.out.println("Error");
                    }

                    // Execute the command
                    switch (line[0]) {
                        case "info":
                            eventInfo();
                            break;

                        case "search":
                            if (line.length == 2) {
                                eventSearch(line[1]);
                            } else {
                                helpCommand(line[0]);
                            }
                            break;

                        case "help":
                            help();
                            break;

                        case "exit":
                            running = false;
                            try {
                                input.close();
                            } catch (IOException e) {
                            }
                            eventExit();
                            break;

                        case "download":
                            String file = listDownload.getValue(line[1]);
                            System.out.println(file);
                            eventDownload(file);
                            break;

                        default:
                            unknownCommand(line[0]);
                            break;
                    }
                }
            }
        };
        runThread.start();
    }

    public void exit() {
        running = false;
        System.out.println("Thanks for using BitNinja!");
    }

    public void info(ClientFiles clientFiles) {
        lock();

        System.out.println("\b\b\bActive files :");
        for (Map.Entry<String, ClientFile> file : clientFiles.getSeedList().entrySet()) {
            // Name
            System.out.println("  " + file.getValue().getFileName());
        }

        printPrompt();
        unlock();
    }

    public void search(Map<String, String[]> result) {
        lock();

        System.out.println("\b\b\bResults found :");
        listDownload.clear();
        int i = 0;
        for (Map.Entry<String, String[]> res : result.entrySet()) {
            // State
            System.out.println("N \tName \tSize \tChunks \tKey");
            System.out.println("[" + i + "] \t" + res.getValue()[0] + " \t" + res.getValue()[1] + " \t" + res.getValue()[2] + " \t" + res.getKey());
            listDownload.putValue(Integer.toString(i), res.getValue()[0] + " " + res.getValue()[2] + " " + res.getKey() + " " + res.getValue()[1]);
            i++;
        }
        if (i==0) {
            System.out.println("No result found");
        }

        printPrompt();
        unlock();
    }

    public void help() {
        lock();

        System.out.println("Available commands :");
        System.out.println("  info");
        System.out.println("  search");
        System.out.println("  download");
        System.out.println("  help");
        System.out.println("  exit");

        unlock();
    }

    public void log(Event event) {
        lock();
        String print = "";

        // Print the log message
        switch (event.type) {
            case LOG_INFO:
                print += "[INFO] ";
                break;
            case LOG_ERROR:
                print += "[ERROR] ";
                break;
        }
        print += event.getAttributes().getValue("message");

        // Print
        System.out.println(print);

        unlock();
    }

    public void eventTrackerConnecting() {
        Event event = new Event(EventSource.CLIENT, EventType.LOG_INFO);
        event.addAttribute("message", "Connecting to tracker...");
        log(event);
    }

    public void eventTrackerConnected(Event event) {
        event.type = EventType.LOG_INFO;
        log(event);
    }

    public void eventTrackerConnectionError(Event event) {
        event.type = EventType.LOG_ERROR;
        log(event);
    }

    private void printPrompt() {
        System.out.print("> ");
    }

    private void lock() {
        while (writeLock) {}
        writeLock = true;
    }

    private void unlock() {
        writeLock = false;
    }

    public void helpCommand(String command) {
        switch (command) {
            case "search":
                System.out.println("Usage : search [filter]");
                System.out.println("Search a file on the tracker");
        }
    }
    
    public void unknownCommand(String command) {
        System.out.println("Unknown command " + command);
        help();
    }

}