package BitNinja;

import java.nio.charset.Charset;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestClient {

	/**
	 * Test the two class builder of the clientFile
	 * and the info fonction 
	 */
	private void testAnnounce(){
	    Config config = new Config();
	    ClientFiles clientFiles = new ClientFiles(config);
		try {
		    Client myClient = new Client(InetAddress.getLocalHost().toString(), "8080", "43564", clientFiles);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		System.out.println("testInfo OK");
	}

	private void testLook(){
	    Config config = new Config();
	    ClientFiles clientFiles = new ClientFiles(config);
		try{
		    Client myClient1 = new Client(InetAddress.getLocalHost().toString(), "8080", "43564", clientFiles);
		    Client myClient2 = new Client(InetAddress.getLocalHost().toString(), "8080", "45563", clientFiles);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
}