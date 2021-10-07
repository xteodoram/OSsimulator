package commands;

import java.util.ArrayList;

import assembly.Assembler;
import cpu.CPU;
import processes.Process;

public class CommandThread extends Thread{
	private String command;
	private CommandLine commandLine;
	
	public CommandThread(String command, CommandLine commandLine) {
		this.command=command;
		this.commandLine=commandLine;
		this.start();
	}
	@Override
	public void run() {
		if(command.equals("list"))
			Process.list();
		else if(command.equals("exit"))
			commandLine.exit();
		else
			CPU.printRM();
	}
}
