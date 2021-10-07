package processes;

import java.util.Queue;

import cpu.CPU;
import memory.RAM;

public class ProcessScheduler {
	
	public static synchronized void schedule() {
		Queue<Process>readyQueue=RAM.getReadyQueue();
		if(!readyQueue.isEmpty() && RAM.getRunningProcess() == null) {
			Process process=readyQueue.remove();
			RAM.setRunningProcess(process);
			process.setState("RUNNING");
			CPU.execute(process);
		}
	}
}
