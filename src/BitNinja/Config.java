package BitNinja;

import java.util.Vector;
import java.util.jar.Attributes;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.jar.Attributes;
import java.util.Vector;



public class Config {
    private String fileName = "BitNinja.cfg";
    private Attributes fields = new Attributes();
    private Vector<String> seedFiles = new Vector<String>();

    public Config() {
        fields.put(new Attributes.Name("port"), "43564");
        fields.put(new Attributes.Name("tracker_address"), "127.0.0.1");
        fields.put(new Attributes.Name("tracker_port"), "8080");
        fields.put(new Attributes.Name("max_peers"), "5");
        fields.put(new Attributes.Name("message_size"), "16384");
        fields.put(new Attributes.Name("update_interval"), "60"); // seconds
        fields.put(new Attributes.Name("chunk_size"), "2048");
    }

    public void load() {
        System.out.println("Reading config file : " + fileName + "...");
        File file = new File(fileName);
        InputStream inputStream = null;
        BufferedReader reader = null;

        // Check if the config file exists
        if (!file.exists()) {
            System.err.println("Warning : file not found, creating default config file");
            save();

        } else {
            // Open the file reader
            try {
                inputStream = new FileInputStream(fileName);
            } catch (FileNotFoundException e) {}
            reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

            try {
                // Read each line of the file
                String line;
                Integer i = 0;
                while ((line = reader.readLine()) != null) {
                    i++;
                    if (line.startsWith("#")) {
                        continue;
                    }
                    String lineTokens[] = line.split("=");
                    if (lineTokens.length != 2) {
                        System.err.println("Warning : invalid syntax on line " + i.toString());
                    }
                    Attributes.Name key = new Attributes.Name(lineTokens[0]);
                    if (lineTokens[0].equals("seed_file")) {
                        seedFiles.add(lineTokens[1]);

                    } else if (fields.containsKey(key)) {
                        fields.put(key, lineTokens[1]);

                    } else {
                        System.err.println("Warning : unknown key " + lineTokens[0] + " on line " + i.toString());
                    }
                }

                reader.close();
            } catch (IOException e) {
                System.err.println("Error while reading the config file");
            }
        }
    }

    public void save() {
        PrintWriter writer;
        try {
            writer = new PrintWriter(fileName, "UTF-8");
        } catch (FileNotFoundException e) {
            System.err.println("Error : unable to create " + fileName);
            return;
        } catch (UnsupportedEncodingException e) {
            System.err.println("Error : unsupported encoding UTF-8");
            return;
        }

        for (Object key : fields.keySet()) {
            writer.println(key.toString() + "=" + fields.get(key));
        }

        for (String seedFile : seedFiles) {
            writer.println("seed_file=" + seedFile);
        }
        writer.close();
    }

    public Vector<String> getSeedFiles() {
        return seedFiles;
    }

    public String get(String key) {
        return fields.getValue(key);
    }

    public int set(String key, String value) {
        Attributes.Name key_ = new Attributes.Name(key);
        if (fields.containsKey(key_)) {
            fields.put(key_, value);
            return 0;
        } else {
            return -1;
        }
    }
}