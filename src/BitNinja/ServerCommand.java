package BitNinja;

import java.util.Arrays;


public class ServerCommand {
	private CommandType instruction;
	private int[] pieces;
	private String key;
	private byte[] buffermap;
	
	public ServerCommand () {
	}

	void setInstruction(String instr) {
		if (instr.equals("have")) {
			this.instruction = CommandType.HAVE;
		} else if (instr.equals("interested")) {
			this.instruction = CommandType.INTERESTED;
		} else if (instr.equals("getpieces")) {
			this.instruction = CommandType.GETPIECES;
		}
	}

	void setPieces(int[] pieces) {
		this.pieces = Arrays.copyOf(pieces, pieces.length);
	}

	void setKey(String key) {
		this.key = key;
	}

	void setBuffermap(byte[] buffermap) {
		this.buffermap = Arrays.copyOf(buffermap, buffermap.length);
	}

	public CommandType getInstruction() {
		return instruction;
	}

	public int[] getPieces() {
		if (pieces != null) {
			return Arrays.copyOf(pieces, pieces.length);
		} else {
			return null;
		}
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

	@Override
	public String toString() {
		return "[instruction:" + instruction + ", pieces" + pieces
		+ ", key" + key + "]";
	}

}