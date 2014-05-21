package BitNinja;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.net.InetAddress;

public class ClientCommand {
	private CommandType instruction;
	private String key;
	private Map<String, String[]> files;
	private Map<InetAddress,Integer> peers;
	private Map<Integer, byte[]> chunks;
	private byte[] buffermap;
	
	public ClientCommand () {
		peers = new HashMap<InetAddress,Integer>();
		chunks = new HashMap<Integer, byte[]>();
		files = new HashMap<String, String[]>();
	}

	void setInstruction(String instr) {
		if (instr.equals("have")) {
			this.instruction = CommandType.HAVE;
		} else if (instr.equals("ok")) {
			this.instruction = CommandType.OK;
		} else if (instr.equals("peers")) {
			this.instruction = CommandType.PEERS;
		} else if (instr.equals("list")) {
			this.instruction = CommandType.LIST;
		} else if (instr.equals("data")) {
			this.instruction = CommandType.DATA;
		} 
	}

	void setPeers(Map<InetAddress,Integer> peers) {
		this.peers.putAll(peers);
	}

	void setChunks(Map<Integer,byte[]> chunks) {
		this.chunks.putAll(chunks);
	}

	void setKey(String key) {
		this.key = key;
	}

	void setBuffermap(byte[] buffermap) {
		this.buffermap = Arrays.copyOf(buffermap, buffermap.length);
	}

	void setFiles(Map<String, String[]> files){
		this.files.putAll(files);
	}

	public CommandType getInstruction() {
		return instruction;
	}

	public Map<InetAddress,Integer> getPeers() {
		if(peers != null){
			Map<InetAddress,Integer> tmp = new HashMap<InetAddress,Integer>(); 
		    tmp.putAll(peers);
			return tmp;
		} else {
			return null;
		}
	}

	public Map<Integer, byte[]> getChunks() {
		if(chunks != null){
			Map<Integer, byte[]> tmp = new HashMap<Integer, byte[]>(); 
	        tmp.putAll(chunks);
			return tmp;
		} else {
			return null;
		}
	}

	public Map<String, String[]> getFiles() {
		if(files != null){
			Map<String, String[]> tmp = new HashMap<String, String[]>(); 
		    tmp.putAll(files);
			return tmp;	
		} else 
			return null;
		
	}

	public String getKey() {
		return key;
	}

	public byte[] getBuffermap() {
		if (buffermap != null) {		
			return Arrays.copyOf(buffermap, buffermap.length);
		} else {
			return null;
		}
	}

}