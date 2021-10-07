package cpu;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import memory.Frame;
import memory.MMU;
import memory.Page;
import memory.RAM;
import processes.Process;

public class CPU {
	private static Register IR=new Register();
	private static Register PC=new Register();
	private static Register R1=new Register("R1","10");
	private static Register R2=new Register("R2","11");
	private static Register R3=new Register("R3","01");
	private static Register F=new Register("Flags","00");
	private static ArrayList<String>pageTable=new ArrayList<String>();
	private static Process currentProcess;
	private static boolean executing;
	
	public static void execute(Process process) {
		
		executing=true;
		System.out.println("Process "+process.getPid()+" started its execution.");
		CPU.currentProcess=process;
		pageTable=currentProcess.getPageTable();
		ArrayList<Page> pages=currentProcess.getPages();
		int length=RAM.powerOfTwo(RAM.getSize());
		String firstInstruction="";
		for(int i=0; i<length; i++) {
			firstInstruction+="0";
		}
		
		PC.setValue(firstInstruction);
		while(executing) {
			ArrayList<String> list=MMU.translate(PC.getValue());
			String instruction=RAM.getLocation(list.get(0),list.get(1));
			IR.setValue(instruction);
			PC.increment();
			executeInstruction(IR.getValue());
		}
	}
	
	public static void executeInstruction(String instruction) {
		String opCode=instruction.substring(0,4);
		if(opCode.equals("0000")) {
			try {
				Thread.sleep(5000);
			}catch(Exception e) {
				e.printStackTrace();
			}
			System.out.println("Result: "+Integer.parseInt(R1.getValue(),2));
			if(currentProcess.getFile() != null)
				writeToFile();
			executing=false;
			printRM();
			System.out.println("Process "+currentProcess.getPid()+" has completed its execution.");
			System.out.println();
			currentProcess.exit();
		}
		else if(opCode.equals("0001")) {
			String newInstruction="";
			String address=instruction.substring(4);
			int length=RAM.powerOfTwo(RAM.getSize())-address.length();
			for(int i=0; i<length; i++) {
				newInstruction+="0";
			}
			newInstruction+=address;
			PC.setValue(newInstruction);
		}
		else if(opCode.equals("0010")) {
			String register=instruction.substring(4,6);
			String memoryLocation=instruction.substring(6);
			int length=RAM.powerOfTwo(RAM.getSize());
			String dataLocation="";
			for(int i=0; i<length-memoryLocation.length(); i++) {
				dataLocation+="0";
			}
			dataLocation+=memoryLocation;
			ArrayList<String>list=MMU.translate(dataLocation);
			String data=RAM.getLocation(list.get(0), list.get(1));
			
			if(register.equals(R1.getAddress()))
				R1.setValue(data);
			else if(register.equals(R2.getAddress()))
				R2.setValue(data);
			else
				R3.setValue(data);
		}
		else if(opCode.equals("0011")) {
			String register=instruction.substring(14);
			String memoryLocation=instruction.substring(4,14);
			int length=RAM.powerOfTwo(RAM.getSize());
			String dataLocation="";
			for(int i=0; i<length-memoryLocation.length(); i++) {
				dataLocation+="0";
			}
			dataLocation+=memoryLocation;
			ArrayList<String>list=MMU.translate(dataLocation);
			
			String data="";
			if(register.equals(R1.getAddress()))
				data=R1.getValue();
			else if(register.equals(R2.getAddress()))
				data=R2.getValue();
			else
				data=R3.getValue();
			
			String newData="";
			for(int i=0; i<16-data.length();i++)
				newData+="0";
			newData+=data;
			
			RAM.setLocation(list.get(0), list.get(1), newData);
		}
		else if(opCode.equals("0100") || opCode.equals("0101") || opCode.equals("0110") || opCode.equals("0111")) {
			String register1=instruction.substring(8,10);
			String register2=instruction.substring(14);
			String data1="";
			String data2="";
			
			if(register1.equals(R1.getAddress())) 
				data1=R1.getValue();
			else if(register1.equals(R2.getAddress())) 
				data1=R2.getValue();
			else 
				data1=R3.getValue();
			
			if(register2.equals(R1.getAddress())) 
				data2=R1.getValue();
			else if(register2.equals(R2.getAddress())) 
				data2=R2.getValue();
			else 
				data2=R3.getValue();
			
			int dataNumber1=Integer.parseInt(data1,2);
			int dataNumber2=Integer.parseInt(data2,2);
			int result=0;
			
			if(opCode.equals("0100"))
				result=dataNumber1+dataNumber2;
			else if(opCode.equals("0101"))
				result=dataNumber1-dataNumber2;
			else if(opCode.equals("0110"))
				result=dataNumber1*dataNumber2;
			else
				result=dataNumber1/dataNumber2;
			
			String binaryNumber="";
			if(result == 0)
				binaryNumber="0";
			else 
				binaryNumber=RAM.decToBinary(result);
			
			if(register1.equals(R1.getAddress()))
				R1.setValue(binaryNumber);
			else
				R2.setValue(binaryNumber);
		}
		else if(opCode.equals("1000")) {
			int zeroFlag=Integer.parseInt(String.valueOf(F.getValue().charAt(0)));
			if(zeroFlag == 0) {
				String newInstruction="";
				String address=instruction.substring(4);
				int length=RAM.powerOfTwo(RAM.getSize())-address.length();
				for(int i=0; i<length; i++) {
					newInstruction+="0";
				}
				newInstruction+=address;
				PC.setValue(newInstruction);
			}	
		}
		else if(opCode.equals("1001")) {
			String register1=instruction.substring(8,10);
			String register2=instruction.substring(14);
			String data1="";
			String data2="";
			
			if(register1.equals(R1.getAddress())) 
				data1=R1.getValue();
			else if(register1.equals(R2.getAddress())) 
				data1=R2.getValue();
			else 
				data1=R3.getValue();
			
			if(register2.equals(R1.getAddress())) 
				data2=R1.getValue();
			else if(register2.equals(R2.getAddress())) 
				data2=R2.getValue();
			else 
				data2=R3.getValue();
			
			int dataNumber1=Integer.parseInt(data1,2);
			int dataNumber2=Integer.parseInt(data2,2);
			int result=dataNumber1-dataNumber2;
			
			if(result>0)
				F.setValue("00");
			else if(result<0)
				F.setValue("01");
			else
				F.setValue("10");
		}
	}
	public static void setToZero() {
		R1.setValue("");
		R2.setValue("");
		PC.setValue("");
		IR.setValue("");
		F.setValue("");
	}
	public static void writeToFile(){
		String result=R1.getValue();
		String file=currentProcess.getFile();
		try {
			FileWriter myWriter = new FileWriter(file);
			myWriter.write(String.valueOf(Integer.parseInt(result,2)));
			myWriter.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void print() {
		System.out.println("State of memory and registers:");
		Frame[] frames=RAM.getFrames();
		for(int i=0; i<frames.length; i++) {
			
			System.out.println("Frame "+i+": ");
			Page page=frames[i].getPage();
			
			if(page != null) {
				ArrayList<String>content=page.getContent();
				for(String instruction: content)
					System.out.println(instruction);
			}else {
				System.out.println("null");
			}
		}
		System.out.println();
		System.out.println("IR: "+IR.getValue());
		System.out.println("PC: "+PC.getValue());
		System.out.println("R1: "+R1.getValue());
		System.out.println("R2: "+R2.getValue());
		System.out.println("R3: "+R3.getValue());
	}
	public static void printRM() {
		System.out.println("State of memory and registers:");
		Frame[] frames=RAM.getFrames();
		int counter=0;
		for(int i=0; i<frames.length; i++) {
			Page page=frames[i].getPage();
			
			if(page != null) {
				ArrayList<String>content=page.getContent();
				for(String instruction: content) {
					String number=binaryToHex(instruction);
					String data="";
					for(int j=0; j<4-number.length(); j++)
						data+="0";
					System.out.print(data+""+number+" ");
				}
				for(int j=0; j<RAM.getFrameSize()/16-content.size(); j++)
					System.out.print("  x  ");
			}else {
				for(int j=0; j<RAM.getFrameSize()/16; j++)
					System.out.print("  x  ");
			}
			counter++;
			if(counter % 10 == 0)
				System.out.println();
		}
		System.out.println();
		System.out.print("IR:"+binaryToHex(IR.getValue())+" ");
		System.out.print("PC:"+binaryToHex(PC.getValue())+" ");
		System.out.print("R1:"+binaryToHex(R1.getValue())+" ");
		System.out.print("R2:"+binaryToHex(R2.getValue())+" ");
		System.out.print("R3:"+binaryToHex(R3.getValue()));
		System.out.println();
	}
	public static String binaryToHex(String value) {
		try {
		int number=Integer.parseInt(value,2);
		String hex=Integer.toHexString(number);
		return hex;
		}catch(Exception e) {
			return null;
		}
	}
	public static ArrayList<String> getPageTable(){
		return pageTable;
	}
}
