package BitNinja;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.nio.charset.Charset;
import java.net.InetAddress;


class TestParser {

    private byte fake[];
	private void testServerParse(){
		String input;
		ServerCommand cmd;

		input = "interested 9BEF2485410E9378BC9ADFB3E32236AF4F683FA2";
		cmd = Parser.serverParse(input);
		assert cmd.getInstruction() == CommandType.INTERESTED;
		assert cmd.getKey().equals("9BEF2485410E9378BC9ADFB3E32236AF4F683FA2");
		assert cmd.getPieces() == null;
		assert cmd.getBuffermap() == null;
		
		input = "interested 9BEF2485410E9378BC9ADFB3E32236AF4F683FA2 [3 4 5 6]";
		cmd = Parser.serverParse(input);
		assert cmd.getInstruction() == CommandType.INTERESTED;
		assert cmd.getKey().equals("9BEF2485410E9378BC9ADFB3E32236AF4F683FA2");
		assert cmd.getPieces() == null;
		assert cmd.getBuffermap() == null;
		
		input = "interesteed 9BEF2485410E9378BC9ADFB3E32236AF4F683FA2";
		cmd = Parser.serverParse(input);
		assert cmd.getInstruction() == null;
		assert cmd.getKey() == null;
		assert cmd.getPieces() == null;
		assert cmd.getBuffermap() == null;

		input = "interested";
		cmd = Parser.serverParse(input);
		assert cmd.getInstruction() == null;
		assert cmd.getKey() == null;
		assert cmd.getPieces() == null;
		assert cmd.getBuffermap() == null;

		input = "interested azerty";
		cmd = Parser.serverParse(input);
		assert cmd.getInstruction() == null;
		assert cmd.getKey() == null;
		assert cmd.getPieces() == null;
		assert cmd.getBuffermap() == null;

		

		input = "have 9BEF2485410E9378BC9ADFB3E32236AF4F683FA2 101010101111";
		cmd = Parser.serverParse(input);
		assert cmd.getInstruction() == CommandType.HAVE;
		assert cmd.getKey().equals("9BEF2485410E9378BC9ADFB3E32236AF4F683FA2");
		assert cmd.getPieces() == null;
		assert Arrays.equals(cmd.getBuffermap(), new String("101010101111").getBytes(Charset.forName("UTF-8")));

		input = "huve 9BEF2485410E9378BC9ADFB3E32236AF4F683FA2 101010101111";
		cmd = Parser.serverParse(input);
		assert cmd.getInstruction() == null;
		assert cmd.getKey() == null;
		assert cmd.getPieces() == null;
		assert cmd.getBuffermap() == null;

		input = "have 9BEF2485410E9378BC9ADFB3E32236AF4F683FA2";
		cmd = Parser.serverParse(input);
		assert cmd.getInstruction() == null;
		assert cmd.getKey() == null;
		assert cmd.getPieces() == null;
		assert cmd.getBuffermap() == null;

		input = "have 9BEF2485410E9378BC9ADFB3E32236AF4F683FA2 231234545454334";
		cmd = Parser.serverParse(input);
		assert cmd.getInstruction() == null;
		assert cmd.getKey() == null;
		assert cmd.getPieces() == null;
		assert cmd.getBuffermap() == null;

		


		input = "getpieces 9BEF2485410E9378BC9ADFB3E32236AF4F683FA2 [3 5 7 8 9]";
		cmd = Parser.serverParse(input);
		assert cmd.getInstruction() == CommandType.GETPIECES;
		assert cmd.getKey().equals("9BEF2485410E9378BC9ADFB3E32236AF4F683FA2");
		assert Arrays.equals(cmd.getPieces(), new int[] {3,5,7,8,9});
		assert cmd.getBuffermap() == null;

		input = "getppieces 9BEF2485410E9378BC9ADFB3E32236AF4F683FA2 [3 5 7 8 9]";
		cmd = Parser.serverParse(input);
		assert cmd.getInstruction() == null;
		assert cmd.getKey() == null;
		assert cmd.getPieces() == null;
		assert cmd.getBuffermap() == null;

		input = "getpieces 9BEF2485410E9378BC9ADFB3E32236AF4F683FA2 [3 5 7 8 9 ]";
		cmd = Parser.serverParse(input);
		assert cmd.getInstruction() == null;
		assert cmd.getKey() == null;
		assert cmd.getPieces() == null;
		assert cmd.getBuffermap() == null;

		input = "getpieces";
		cmd = Parser.serverParse(input);
		assert cmd.getInstruction() == null;
		assert cmd.getKey() == null;
		assert cmd.getPieces() == null;
		assert cmd.getBuffermap() == null;

		input = "getpieces [3 5 7 8 9]";
		cmd = Parser.serverParse(input);
		assert cmd.getInstruction() == null;
		assert cmd.getKey() == null;
		assert cmd.getPieces() == null;
		assert cmd.getBuffermap() == null;

		input = "getpieces 9BEF2485410E9378BC9ADFB3E32236AF4F683FA2";
		cmd = Parser.serverParse(input);
		assert cmd.getInstruction() == null;
		assert cmd.getKey() == null;
		assert cmd.getPieces() == null;
		assert cmd.getBuffermap() == null;

	}

private void testClientParse(){
		String input;
		ClientCommand cmd;
		
		input = "ok";
		cmd = Parser.clientParse(input, 0, fake);
		assert cmd.getInstruction() == CommandType.OK;
		assert cmd.getKey() == null;
		assert cmd.getBuffermap() == null;
		assert cmd.getPeers().size() == 0;
		assert cmd.getChunks().size() == 0;
		assert cmd.getFiles().size() == 0;

		input ="ook";
		cmd = Parser.clientParse(input, 0, fake);
		assert cmd.getInstruction() == null;
		assert cmd.getKey() == null;
		assert cmd.getBuffermap() == null;
		assert cmd.getPeers().size() == 0;
		assert cmd.getChunks().size() == 0;
		assert cmd.getFiles().size() == 0;	

		input = "list [file_a.dat 2097152 1024 9BEF2485410E9378BC9ADFB3E32236AF4F683FA2 file_b.dat 4097152 1024 8BEF2485410E9378BC9ADFB3E32236AF4F683FA7]";
		cmd = Parser.clientParse(input, 0, fake);
		assert cmd.getInstruction() == CommandType.LIST;
		assert cmd.getKey() == null;
		assert cmd.getBuffermap() == null;
		assert cmd.getPeers().size() == 0;
		assert cmd.getChunks().size() == 0;		
		Map<String, String[]> files = new HashMap<String, String[]>();
		files = cmd.getFiles();
		String[] info = new String[3];
		info = files.get("9BEF2485410E9378BC9ADFB3E32236AF4F683FA2");
		assert info[0].equals("file_a.dat");
		assert info[1].equals("2097152");
		assert info[2].equals("1024");
		info = files.get("8BEF2485410E9378BC9ADFB3E32236AF4F683FA7");
		assert info[0].equals("file_b.dat");
		assert info[1].equals("4097152");
		assert info[2].equals("1024");

		input = "liesst [file_a.dat 2097152 1024 9BEF2485410E9378BC9ADFB3E32236AF4F683FA2]";
		cmd = Parser.clientParse(input, 0, fake);
		assert cmd.getInstruction() == null;
		assert cmd.getKey() == null;
		assert cmd.getBuffermap() == null;
		assert cmd.getPeers().size() == 0;
		assert cmd.getChunks().size() == 0;	
		assert cmd.getFiles().size() == 0;
		
		input = "peers 9BEF2485410E9378BC9ADFB3E32236AF4F683FA2";
		System.out.println("\n>Tests Parseur : OK");
	}

    
}