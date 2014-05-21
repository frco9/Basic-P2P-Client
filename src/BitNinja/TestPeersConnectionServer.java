package BitNinja;

import java.nio.charset.Charset;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestPeersConnectionServer {

	public static void main(String[] args){
		Config config = new Config();
		config.load();
		System.out.println("salut");
		ClientFiles clientFiles = new ClientFiles(config);
		System.out.println("salut");
		try {
		    Client client = new Client(InetAddress.getLocalHost().toString(), "8080", "43564", clientFiles);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		// String keyStr = "8945FDAB45605e92afeb80fc7722ec89eb0bf096";
		// byte[] key = keyStr.getBytes(Charset.forName("UTF-8")); 
		// clientFiles.addFile("file_a.dat", 2048, key, (long) 207152);
		// ClientFile test = clientFiles.getFile(keyStr);
		// test.setTest(2);
		// test.setTest(5);
		// test.setTest(10);
		// test.setTest(9);
		// test.setTest(7);
		// test.setTest(25);
		System.out.println("salut");
		Server server = new Server(43564, clientFiles); 
		System.out.println("salut");
	}

	
}