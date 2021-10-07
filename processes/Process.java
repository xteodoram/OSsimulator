package processes;

import java.util.ArrayList;
import java.util.Queue;

import cpu.CPU;
import memory.Page;
import memory.RAM;

public class Process extends Thread{
	
	private static ArrayList<Process> processes=new ArrayList<>(); 
	private static int counter=0;
	private int pid;
	private String state;
	private int programCounter;
	private int size;
	private String name;
	private ArrayList<String> codeAndData=new ArrayList<>();
	private ArrayList<Page> pages=new ArrayList<>();
	private ArrayList<String> pageTable=new ArrayList<>();
	private String file;
	
	public Process(ArrayList<String> codeAndData,String name, String file) {
		pid=counter++;
		state="NEW";
		programCounter=0;
		this.codeAndData=codeAndData;
		this.name=name;
		this.file=file;
		size=codeAndData.size()*16;
		processes.add(this);
		this.start();
	}
	@Override
	public void run() {
		splitIntoPages();
		load();
	}
	public void splitIntoPages() {
		int frameSize=RAM.getFrameSize();
		int number=frameSize/16;
		int counter=0;
		while(counter<codeAndData.size()) {
			Page page=new Page();
			for(int i=0; i<number; i++) {
				if(counter<codeAndData.size()) {
					page.add(codeAndData.get(counter));
					counter++;
					}
			}
			pages.add(page);
		}
	}
	public void load() {
		boolean loaded=false;
		while(!loaded) {
			loaded=RAM.load(this);
		}
		if(RAM.getRunningProcess() == null)
			ProcessScheduler.schedule();
	}
	public void exit() {
		this.state="TERMINATED";
		CPU.setToZero();
		processes.remove(this);
		RAM.removeRunningProcess();
		RAM.remove(this);
		ProcessScheduler.schedule();
	}
	public static void list() {
		Queue<Process>readyProcesses=RAM.getReadyQueue();
		Process runningProcess=RAM.getRunningProcess();
		if(runningProcess == null && readyProcesses.isEmpty())
			System.out.println("There are no processes that are currently in ready or running state.");
		else {
			System.out.println("List of processes:");
			if(runningProcess != null) {
				System.out.println("\tPID: "+runningProcess.pid);
				System.out.println("\tName: "+runningProcess.name);
				System.out.println("\tState: "+runningProcess.state);
				System.out.println("\tSize: "+runningProcess.size);
			}
			if(!readyProcesses.isEmpty()) {
				for(Process process: readyProcesses) {
					System.out.println("\tPID: "+process.pid);
					System.out.println("\tName: "+process.name);
					System.out.println("\tState: "+process.state);
					System.out.println("\tSize: "+runningProcess.size);
				}
			}
		}
	}
	public int getPid() {
		return pid;
	}
	public String getFile() {
		return file;
	}
	public void setState(String state) {
		this.state=state;
	}
	public ArrayList<String> getPageTable(){
		return pageTable;
	}
	public ArrayList<Page> getPages(){
		return pages;
	}
	public void addToPageTable(String frameNumber) {
		pageTable.add(frameNumber);
	}
	public void printPT() {
		System.out.println(pageTable);
	}
	public void printPages() {
		System.out.println(pages);
	}
}
