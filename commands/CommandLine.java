package commands;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

import assembly.Assembler;
import memory.RAM;
import processes.Process;

public class CommandLine{
	private Scanner scan;
	private final static String[] commands= {"exe", "exit", "list","print"};
	
	public CommandLine() {
		scan=new Scanner(System.in);
		RAM.constructor();
		this.start();
	}
	public void start() {
		try {
			while(scan.hasNextLine()) {
				String command=scan.nextLine();
				if(isValid(command))
					executeCommand(command);
				else
					System.out.println("Error! '"+command.split(" ")[0]+"' is not recognized as a command!");
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void start2() {
		String[]list= {"exe pr1.txt","exe pr2.txt res.txt","exe pr3.txt res.txt",
				"exe pr4.txt res.txt","exe pr5.txt res.txt","list"};
		for(int i=0; i<list.length; i++)
			executeCommand(list[i]);
		start();
	}
	private void executeCommand(String command) {
		String[]list=command.split(" ");
		if(list[0].equals(commands[0]) && (list.length == 3 || list.length == 2)) {
			File file=new File(list[1]);
			if(file.exists()) {
				ArrayList<String>codeAndData=Assembler.convert(list[1]);
				int index=list[1].indexOf('.');
				String name=list[1].substring(0,index)+".asm";
				if(list.length == 3)
					new Process(codeAndData,name,list[2]);
				else
					new Process(codeAndData,name,null);
			}else {
				System.out.println("Error! File '"+list[1]+"' does not exist!");
			}
		}
		else if(list[0].equals(commands[1]) && list.length == 1)
			new CommandThread(command,this);
		
		else if(list[0].equals(commands[2]) && list.length == 1)
			new CommandThread(command,this);
		
		else if(list[0].equals(commands[3]) && list.length == 1)
			new CommandThread(command,this);
		
		else
			System.out.println("Error! Invalid parameters!");
	}
	private boolean isValid(String command) {
		String[]list=command.split(" ");
		if(!list[0].equals(commands[0]) && !list[0].equals(commands[1]) && !list[0].equals(commands[2]) && !list[0].equals(commands[3]))
			return false;
		return true;
	}
	public void exit() {
		System.out.println("Goodbye!");
		System.exit(0);
	}
	public static void main(String[]args) {
		CommandLine cmd=new CommandLine();
	}
}
