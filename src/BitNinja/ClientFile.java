package BitNinja;

import java.util.BitSet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.io.BufferedInputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Formatter;
import java.lang.String;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;

public class ClientFile {
    private String fileName;
    private byte[] key;
    private int chunkSize;
    private BitSet bufferMap;
    private long globalSize;
    private File file;
    private RandomAccessFile access;

    /**
     * Create a class from an existing file, all the parameters except
     * the chunksize were calculated from this file
     *
     * @param  cSize the chunk size for this session
     * @param  name  the name of the file to load
     */
    ClientFile(String name, int cSize) {
        fileName = name;
        chunkSize = cSize;
        try {
            file = new File(fileName);
            globalSize = file.length();
            access = new RandomAccessFile(file, "rw");
            key = createSha1();
        } catch(IOException e) {
            e.printStackTrace();
        }
        catch(NoSuchAlgorithmException a) {
            a.printStackTrace();
        }
        bufferMapInit(true);
    }

    /**
     * Create a class from an new file, all parameters are needed
     *
     * @param  cSize the chunk size for this session
     * @param  name  the name of the file to load
     * @param  fKey  the sha1 key of the file
     * @param  fSize the size of the file
     */
    ClientFile(String name, int cSize, byte[] fKey, long fSize) {
        fileName = name;
        key = fKey;
        chunkSize = cSize;
        globalSize = fSize;
        bufferMapInit(false);
        file = new File(fileName);
        try {
            file.createNewFile();
            access = new RandomAccessFile(file, "rw");
        }
        catch (IOException e) {
            e.printStackTrace(); 
        }
    }

    /**
     * Initialize the bufferMap with the correct value
     *
     * @param  value the initial value
     */
    private void bufferMapInit(boolean value) {
        int lenBuffer = (int)globalSize / chunkSize;
        if(((int)globalSize % chunkSize) != 0)
            lenBuffer++;
        bufferMap = new BitSet(lenBuffer);
        bufferMap.set(0, lenBuffer, value);
        bufferMap.set(lenBuffer,true);
    }

    /**
     * Compute the Sha1 key of the file
     *
     * @return  the Sha1 key
     */
    private byte[] createSha1() throws NoSuchAlgorithmException, IOException{
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        
        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
                final byte[] buffer = new byte[1024];
                for (int read = 0; (read = is.read(buffer)) != -1;) {
                    messageDigest.update(buffer, 0, read);
                  }
            }
        
        // Convert the byte to hex format
        try (Formatter formatter = new Formatter()) {
                for (final byte b : messageDigest.digest()) {
                    formatter.format("%02x", b);
                }
                return formatter.toString().getBytes(Charset.forName("UTF-8"));
            }

    }

    /**
     * Give the selected part of the file
     *
     * @param  numChunks the number of the chunk needed
     * @return a part of the file
     */
    public byte[] getChunks(int numChunks) {
        int readChunks = 0;
        byte buffertmp[] = new byte[chunkSize];
        try {
            access.seek(numChunks*chunkSize);
            readChunks = access.read(buffertmp, 0, chunkSize); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte buffer[] = new byte[readChunks];
        buffer = Arrays.copyOf(buffertmp, readChunks);
        return buffer;
    }

    /**
     * Add a content to the file
     *
     * @param  numChunks the number of the chunk to write
     * @param  buffer    the content to write
     * @return true if the operation was succesfull or false otherwise
     */
    public boolean setChunks(int numChunks, byte[] buffer) {
        if(numChunks*chunkSize > globalSize)
            return false;
        try {
            access.seek(numChunks*chunkSize);
            access.write(buffer, 0, buffer.length);
            bufferMap.set(numChunks);   
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    /**
     * Test the current state of the file download
     *
     * @return  true if the download is complete
     */
    public boolean finished() {
        return bufferMap.cardinality() == globalSize/chunkSize+1;
    }

    /**
     * @return the bufferMap of the file
     */
    public BitSet bufferMap(){
        return bufferMap;
    }

    /**
     * File name
     *
     * @return  the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Info of the file
     *
     * @return  the info of the file
     */
    public String info() {
        return fileName + " " + globalSize + " " + chunkSize + " " + key.toString();
    }

    /**
     * Check if the downloaded file had some mistake
     *
     * @return  true if the given key was the same as the file's one
     */
    public boolean checkFile() {
        boolean test = false;
        try {       
            test = (key == createSha1());
        } catch(IOException e) {
            e.printStackTrace();
        }
        catch(NoSuchAlgorithmException a) {
            a.printStackTrace();
        }
        return test;
    }
    
    public byte[] getKey(){
        if (key != null) {        
            return Arrays.copyOf(key, key.length);
        } else {
            return null;
        }
    }

    /**
     * File size
     *
     * @return  the file size
     */
    public long getFileSize(){
        return globalSize;
    }

    public  void setTest(int i){
        bufferMap.set(i);
    }

    /**
     * File size
     *
     * @return  the file size
     */
    public int getChunkSize(){
        return chunkSize;
    }
}