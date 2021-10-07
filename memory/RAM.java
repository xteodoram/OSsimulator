package memory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import assembly.Assembler;
import cpu.CPU;
import processes.Process;
import processes.ProcessScheduler;

public class RAM {
	private static int SIZE;
	private static Frame[] frames;
	private static ArrayList<Integer> freeFrames=new ArrayList<>();
	private static int numberOfFrames;
	private static int frameSize;
	private static Queue<Process>readyQueue=new LinkedList<>();
	private static Process runningProcess=null;;
	
	public static void constructor(){
		SIZE=4096;
		frameSize=32;
		numberOfFrames=128;
		frames=new Frame[numberOfFrames];
		for(int i=0; i<numberOfFrames; i++) {
			String binaryNumber=decToBinary(i);
			String newBinary="";
			for(int j=0; j<powerOfTwo(numberOfFrames)-binaryNumber.length(); j++)
				newBinary+="0";
			newBinary+=binaryNumber;
			frames[i]=new Frame(newBinary);
		}
		for(int i=0; i<numberOfFrames; i++)
			freeFrames.add(i);
	}
	public static void constructor(int size, int frameSize) {
		SIZE=size;
		RAM.frameSize=frameSize;
		numberOfFrames=size/frameSize;
		frames=new Frame[numberOfFrames];
		for(int i=0; i<numberOfFrames; i++) {
			String binaryNumber=decToBinary(i);
			String newBinary="";
			for(int j=0; j<powerOfTwo(numberOfFrames)-binaryNumber.length(); j++)
				newBinary+="0";
			newBinary+=binaryNumber;
			frames[i]=new Frame(newBinary);
		}
		for(int i=0; i<numberOfFrames; i++)
			freeFrames.add(i);
	}
	
	public static int getFrameSize() {
		return frameSize;
	}
	public static synchronized boolean load(Process process) {
		ArrayList<Page>pages=process.getPages();
		int numberOfPages=pages.size();
		int pageCounter=0;
		
		if(freeFrames.size() >= numberOfPages) {
			while(pageCounter < numberOfPages) {
				
				int index=freeFrames.get(0);
				process.addToPageTable(frames[index].getFrameNumber());
				frames[index].setPage(pages.get(pageCounter++));
				freeFrames.remove(0);
				}
			
			readyQueue.add(process);
			process.setState("READY");
			return true;
		}
		return false;
	}
	public static String getLocation(String frameNumber, String offset) {
		
		int indexF=Integer.parseInt(frameNumber,2);
		Frame frame=frames[indexF];
		Page page=frame.getPage();
		int indexP=Integer.parseInt(offset,2)/16;
		String location=page.getContent().get(indexP);
		
		return location;
	}
	public static void setLocation(String frameNumber, String offset, String data) {
		int indexF=Integer.parseInt(frameNumber,2);
		Frame frame=frames[indexF];
		Page page=frame.getPage();
		int indexP=Integer.parseInt(offset,2)/16;
		page.change(indexP, data);
	}
	public static void remove(Process process) {
		ArrayList<String> pageTable=process.getPageTable();
		for(int i=0; i<pageTable.size(); i++) {
			int indexF=Integer.parseInt(pageTable.get(i),2);
			frames[indexF].free();
			freeFrames.add(indexF);
		}
	}
	public static int powerOfTwo(int size) {
		int i=1;
		int counter=0;
		while(i<=size) {
			i*=2;
			counter++;
		}
		if (i/2 == size)
			return --counter;
		return -1;
	}
	public static String decToBinary(int n){
        String binaryNumber="";
        int[] binaryNum = new int[1000];
        int i = 0;
        
        while (n > 0) {
            binaryNum[i] = n % 2;
            n = n / 2;
            i++;
        }
        for (int j = i - 1; j >= 0; j--)
            binaryNumber+=String.valueOf(binaryNum[j]);
        
        return binaryNumber;
    }
	public static Queue<Process> getReadyQueue(){
		return readyQueue;
	}
	public static Process getRunningProcess(){
		return runningProcess;
	}
	public static void setRunningProcess(Process process) {
		runningProcess=process;
	}
	public static void removeRunningProcess() {
		runningProcess=null;
	}
	public static int getSize() {
		return SIZE;
	}
	public static int getNumberOfFrames() {
		return numberOfFrames;
	}
	public static Frame[] getFrames() {
		return frames;
	}
}
