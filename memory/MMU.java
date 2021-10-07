package memory;

import java.util.ArrayList;

import cpu.CPU;

public class MMU {
	public static ArrayList<String> translate(String address) {
		
		ArrayList<String>pageTable=CPU.getPageTable();
		int frameLength=RAM.powerOfTwo(RAM.getNumberOfFrames());
		String pageNumber=address.substring(0,frameLength);
		String offset=address.substring(frameLength);
		
		int index=Integer.parseInt(pageNumber,2);
		String frameNumber=pageTable.get(index);
		ArrayList<String> list=new ArrayList<String>();
		list.add(frameNumber);
		list.add(offset);
		return list;
	}

}
