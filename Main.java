import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class userinterface{

    Scanner inputScanner = new Scanner(System.in);
    void display (String message){
        System.out.println(message);
    }

    String getInputFor (String prompt){
        System.out.println(prompt);
        return inputScanner.nextLine();
    }

    int getInputIntFor (String prompt){
        System.out.println(prompt);
        return Integer.parseInt(inputScanner.nextLine());
    }

    Process getProcessInput(int processNumber, int defaultQuantum){
        while (true) {
            String name = getInputFor("Enter name for Process " + processNumber + ": ");
            int arrivalTime = getInputIntFor("Enter arrival time for Process " + processNumber + ": ");
            int burstTime = getInputIntFor("Enter burst time for Process " + processNumber + ": ");
            int priority = getInputIntFor("Enter priority for Process " + processNumber + ": ");
            if (verifyEntry(new Process(name, arrivalTime, burstTime, priority, defaultQuantum))) {
                return new Process(name, arrivalTime, burstTime, priority, defaultQuantum);
            }
        }
    }
    
    boolean verifyEntry(Process process){
        if (process.getArrivalTime() < 0 || process.getBurstTime() <= 0 || process.getPriority() < 0) {
            display("Invalid entry for process: " + process.getName() + ".\n Please re-enter the details.");
            return false;
        }
        return true;
    }

    void displayEntries(List<Process> processes, int contextSwitchingTime, int rrTimeQuantum){
        display("Configuration Loaded:");
        display("Processes: " + processes.size() 
                + "\nContext Switching Time: " + contextSwitchingTime
                + "\nRound Robin Time Quantum: " + rrTimeQuantum);
        display("Process Entries:");
        for (Process process : processes) {
            display("Name: " + process.getName() +
                    ", Arrival Time: " + process.getArrivalTime() +
                    ", Burst Time: " + process.getBurstTime() +
                    ", Priority: " + process.getPriority() +
                    ", Quantum: " + process.getQuantum());
        }
    }

}

class Process {
    private String name;
    private int arrivalTime;
    private int burstTime;
    private int priority;
    private int quantum;

    private int remainingTime;
    private int completionTime;

    private int waitingTime;
    private int turnaroundTime;
    private int averageWaitingTime;
    private int averageTurnaroundTime;

    public Process(String name, int arrivalTime, int burstTime, int priority, int quantum) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.quantum = quantum;
        this.remainingTime = burstTime;
    }

    // Getters and Setters
    public String getName() { return name; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getPriority() { return priority; }
    public int getQuantum() { return quantum; }
    public void setQuantum(int quantum) { this.quantum = quantum; }

    public int getRemainingTime() { return remainingTime; }
    public void setRemainingTime(int remainingTime) { this.remainingTime = remainingTime; }

    public int getCompletionTime() { return completionTime; }
    public void setCompletionTime(int completionTime) { this.completionTime = completionTime; }

    public int getAvgWaitingTime() { return averageWaitingTime; }
    public void setAvgWaitingTime(int averageWaitingTime) { this.averageWaitingTime = averageWaitingTime; }

    public int getAvgTurnaroundTime() { return averageTurnaroundTime; }
    public void setAvgTurnaroundTime(int averageTurnaroundTime) { this.averageTurnaroundTime = averageTurnaroundTime; }

    public int getWaitingTime() { return waitingTime; }
    public void setWaitingTime(int waitingTime) { this.waitingTime = waitingTime; }

    public int getTurnaroundTime() { return turnaroundTime; }
    public void setTurnaroundTime(int turnaroundTime) { this.turnaroundTime = turnaroundTime; }

    public boolean isFinished() {
        return remainingTime == 0;
    }
}

class main {
    public static void main(String[] args) {
        
        userinterface ui = new userinterface();
        
        int numProcesses = ui.getInputIntFor("Enter number of processes: ");
        int rrTimeQuantum = ui.getInputIntFor("Enter Round Robin Time Quantum: ");
        int contextSwitchingTime = ui.getInputIntFor("Enter Context Switching Time: ");

        List<Process> processes = new ArrayList<>();

        for (int i = 0; i < numProcesses; i++) {
            processes.add(ui.getProcessInput(i + 1, rrTimeQuantum));
        }


        ui.displayEntries(processes, contextSwitchingTime, rrTimeQuantum);
    }
}