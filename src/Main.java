import java.lang.reflect.Array;
import java.util.*;

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
            if (defaultQuantum < 0) {
                defaultQuantum = getInputIntFor("Enter quantum for Process " + processNumber + ": ");
            }
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
    private int lastUpdateTime;

    private int remainingTime;
    private int completionTime;

    private int waitingTime;
    private int turnaroundTime;
    private int averageWaitingTime;
    private int averageTurnaroundTime;

    private int cumulativeWaiting = 0;
    private int agingCount = 0;   

    public Process(String name, int arrivalTime, int burstTime, int priority, int quantum) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.quantum = quantum;
        this.remainingTime = burstTime;
        this.lastUpdateTime = arrivalTime;
    }
    //    another constructor for some algorithms
    public Process(String n, int a, int b, int p) {
        this.name = n;
        this.arrivalTime = a;
        this.burstTime = b;
        this.priority = p;
        this.remainingTime = b;
    }

    // Getters and Setters
    public String getName() { return name; }
    public int getArrivalTime() { return arrivalTime; }
    public int getBurstTime() { return burstTime; }
    public int getPriority() { return priority; }
    public int getQuantum() { return quantum; }
    public int getCumulativeWaiting() { return cumulativeWaiting; }
    public int getAgingCount() { return agingCount; }
     public int getLastUpdateTime() { return lastUpdateTime;}
    
    public void setLastUpdateTime(int time) {this.lastUpdateTime = time;}
    public void setQuantum(int quantum) { this.quantum = quantum; }
    public void setPriority(int priority) 
    {
    this.priority = Math.max(0, priority);
    }

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

    public void incrementWaiting() 
    {
        cumulativeWaiting++; 
    }

    public void incrementAgingCount() 
    {
        agingCount++; 
    }

    public void agePriority() 
    { 
    if (priority > 1) 
    {
        priority--;
    }
    }
    
}

// the class of algorithms and the print
class Scheduler {
    // ==================algorithms==============================
    public static void roundRobin(
            ArrayList<Process> processes,
            int quantum,
            int contextSwitch
    ) {
        ArrayList<Process> list = new ArrayList<>();
        for (Process p : processes)
            list.add(new Process(
                    p.getName(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    p.getPriority()
            ));

        list.sort(Comparator.comparingInt(Process::getArrivalTime));

        Queue<Process> queue = new LinkedList<>();
        int time = 0;
        int idx = 0;
        ArrayList<String> order = new ArrayList<>();

        while (idx < list.size() && list.get(idx).getArrivalTime() <= time)
            queue.add(list.get(idx++));

        while (!queue.isEmpty() || idx < list.size()) {

            if (queue.isEmpty()) {
                time = list.get(idx).getArrivalTime();
                while (idx < list.size() && list.get(idx).getArrivalTime() <= time)
                    queue.add(list.get(idx++));
            }

            Process current = queue.poll();
            order.add(current.getName());

            int execTime = Math.min(quantum, current.getRemainingTime());

            time += execTime;
            current.setRemainingTime(current.getRemainingTime() - execTime);

            while (idx < list.size() && list.get(idx).getArrivalTime() <= time)
                queue.add(list.get(idx++));

            if (current.getRemainingTime() == 0) {
                current.setCompletionTime(time);
            } else {
                queue.add(current);
            }

            if (!queue.isEmpty() || idx < list.size()) {
                time += contextSwitch;
                while (idx < list.size() && list.get(idx).getArrivalTime() <= time)
                    queue.add(list.get(idx++));
            }
        }

        for (Process p : list) {
            p.setTurnaroundTime(p.getCompletionTime() - p.getArrivalTime());
            p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
        }

        printRR(list, order, quantum, contextSwitch);
    }

    // =========================================================================

    public static void preemptiveSJF(
            ArrayList<Process> processes,
            int contextSwitch
    ) {
        ArrayList<Process> list = new ArrayList<>();
        for (Process p : processes)
            list.add(new Process(
                    p.getName(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    p.getPriority()
            ));

        list.sort(Comparator.comparingInt(Process::getArrivalTime));

        ArrayList<String> order = new ArrayList<>();
        int time = 0, completed = 0, idx = 0;

        PriorityQueue<Process> readyQueue = new PriorityQueue<>(
                Comparator.comparingInt(Process::getRemainingTime)
                        .thenComparingInt(Process::getArrivalTime)
        );

        Process current = null;
        boolean needContextSwitch = false;

        while (completed < list.size()) {

            while (idx < list.size() && list.get(idx).getArrivalTime() <= time)
                readyQueue.add(list.get(idx++));

            if (needContextSwitch) {
                time += contextSwitch;
                while (idx < list.size() && list.get(idx).getArrivalTime() <= time)
                    readyQueue.add(list.get(idx++));
                needContextSwitch = false;
            }

            Process shortest = readyQueue.peek();

            if (current == null ||
                    (shortest != null &&
                            shortest.getRemainingTime() < current.getRemainingTime())) {

                if (current != null && current.getRemainingTime() > 0) {
                    readyQueue.add(current);
                    order.add(current.getName());
                    needContextSwitch = true;
                }

                if (shortest != null) {
                    current = readyQueue.poll();
                    order.add(current.getName());
                }
            }

            if (current != null) {
                current.setRemainingTime(current.getRemainingTime() - 1);
                time++;

                if (current.getRemainingTime() == 0) {
                    current.setCompletionTime(time);
                    completed++;
                    order.add(current.getName());
                    needContextSwitch = true;
                    current = null;
                }
            } else {
                time++;
            }
        }

        for (Process p : list) {
            p.setTurnaroundTime(p.getCompletionTime() - p.getArrivalTime());
            p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
        }

        printSJF(list, order, contextSwitch);
    }

    //=====================================================================

    public static void preemptivePriority(ArrayList<Process> processes, int contextSwitch,int agingInterval)
    {
        ArrayList<Process> list = new ArrayList<>();
        for (Process p : processes) 
        {
            Process np = new Process(
                    p.getName(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    p.getPriority()
            );
            np.setLastUpdateTime(p.getArrivalTime());
            list.add(np);
        }

        list.sort(Comparator.comparingInt(Process::getArrivalTime));

        ArrayList<String> order = new ArrayList<>();
        ArrayList<Process> ready = new ArrayList<>();

        int time = 0, idx = 0, completed = 0;
        Process current = null;

        while (completed < list.size() || current != null || idx < list.size()) 
        {

            while (idx < list.size() && list.get(idx).getArrivalTime() <= time)
            {
                ready.add(list.get(idx++));
            }

            if (current == null && ready.isEmpty()) 
            {
                if (idx < list.size()) 
                {
                    time = list.get(idx).getArrivalTime();
                    continue;
                } 
                else 
                {
                    break;
                }
            }

            for (Process p : ready) 
            {
                if (p.getPriority() > 1 &&
                    time - p.getLastUpdateTime() >= agingInterval) 
                {

                    p.setPriority(p.getPriority() - 1);
                    p.setLastUpdateTime(time);
                }
            }

            Process next = current;
            for (Process p : ready) 
            {
                if (next == null || p.getPriority() < next.getPriority() || (p.getPriority() == next.getPriority() && p.getArrivalTime() < next.getArrivalTime())) 
                {
                    next = p;
                }
            }

            if (next != current)
            {
                if (current != null) 
                {
                    ready.add(current);
                }
                ready.remove(next);
                if (time > 0) 
                {
                    time += contextSwitch;
                }
                
                order.add(next.getName());
                current = next;
            }
            
            current.setRemainingTime(current.getRemainingTime() - 1);
            time++;

            if (current.getRemainingTime() == 0)
            {
                current.setCompletionTime(time);
                completed++;
                current = null;
            }
        }

        for (Process p : list) 
        {
            p.setTurnaroundTime(p.getCompletionTime() - p.getArrivalTime());
            p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
        }

        printPriority(list, order, contextSwitch);
    }

    //================================ AG ===================================

    public static void AGsceduler(ArrayList<Process> processes, int contextSwitch) {
        
        ArrayList<Process> activProcesses = new ArrayList<>();
        for (Process p : processes)
            activProcesses.add(new Process(
                    p.getName(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    p.getPriority(),
                    p.getQuantum()
            ));
        
        activProcesses.sort(Comparator.comparingInt(Process::getArrivalTime));
        Queue <Process> readyQueue = new LinkedList<>();
        ArrayList<String> order = new ArrayList<>();
        ArrayList<Process> completedProcesses = new ArrayList<>();
        int currTime = 0;
        Process currProcess = null;
        int currProccessTime = 0;
        
        while (completedProcesses.size() < activProcesses.size()) {
        
            for (Process p : activProcesses){
                if (p.getArrivalTime() == currTime && !p.isFinished() && p != currProcess && !readyQueue.contains(p)) {
                    readyQueue.add(p);
                }
            }

            if (currProcess == null || currProcess.isFinished()){
                if (!readyQueue.isEmpty()) {
                    currProcess = readyQueue.poll();
                    // currProcess.setWaitingTime(currTime - currProcess.getArrivalTime() );
                    currProccessTime = 0;
                }
                else {
                    currTime++;
                    continue;
                }
            }

            order.add(currProcess.getName());

            int quantum = currProcess.getQuantum();
            int q25 = (int) Math.ceil(0.25 * quantum);
            int q50 = (int) Math.ceil(0.50 * quantum);

            //execute 
            currProcess.setRemainingTime(currProcess.getRemainingTime() - 1);
            currProccessTime++;
            currTime++;

            for (Process p : activProcesses){
                if (p.getArrivalTime() == currTime && !p.isFinished() && p != currProcess && !readyQueue.contains(p)) {
                    readyQueue.add(p);
                }
            }

            if (currProcess.isFinished()){
                currProcess.setCompletionTime(currTime);
                currProcess.setQuantum(0);
                // currProcess.setTurnaroundTime(currProccessTime);
                completedProcesses.add(currProcess);
                currProcess = null;
                continue;
            }

            if (currProccessTime == quantum) {
                currProcess.setQuantum(currProcess.getQuantum() + 2);
                readyQueue.add(currProcess);
                currProcess = null;
                continue;
            }

            //phase 2
            if (currProccessTime >= q25 && currProccessTime < q50) {
                Process highestPrioProccess = null;
                for (Process p : readyQueue) {
                    if (highestPrioProccess == null || p.getPriority() < highestPrioProccess.getPriority()) {
                        highestPrioProccess = p;
                    }
                }
                
                if (highestPrioProccess != null && currProcess.getPriority() > highestPrioProccess.getPriority()) {
                    int remQ = quantum - currProccessTime;
                    currProcess.setQuantum(currProcess.getQuantum() + (int) Math.ceil(remQ / 2.0));
                    readyQueue.add(currProcess);
                    
                    readyQueue.remove(highestPrioProccess);
                    currProcess = highestPrioProccess;
                    currProccessTime = 0;
                    continue;
                }
            }

            //phase 3
            if (currProccessTime >= q50) {
                Process shortestProccess = null;
                for (Process p : readyQueue){
                    if (shortestProccess == null || p.getRemainingTime() < shortestProccess.getRemainingTime()) {
                        shortestProccess = p;
                    }
                }
                
                if(shortestProccess != null && 
                   currProcess.getRemainingTime() > shortestProccess.getRemainingTime() &&
                   currProcess.getRemainingTime() > (quantum - currProccessTime)) {
                    int remQ = quantum - currProccessTime;
                    currProcess.setQuantum (currProcess.getQuantum() + remQ);
                    readyQueue.add(currProcess);
                    
                    readyQueue.remove(shortestProccess);
                    currProcess = shortestProccess;
                    currProccessTime = 0;
                    continue;
                }
            }       
        }
        for (Process p : completedProcesses) {
                p.setTurnaroundTime(p.getCompletionTime() - p.getArrivalTime());
                p.setWaitingTime(p.getTurnaroundTime() - p.getBurstTime());
            }
            
            // You can reuse your existing print function or create a new one
            printAG(completedProcesses, order); 
    }

    // ===========================prints==============================================

    private static void printRR(ArrayList<Process> ps, ArrayList<String> order,
                                int quantum, int contextSwitch) {

        double w = 0, t = 0;

        System.out.println("\n========= ROUND ROBIN SCHEDULING =========");
        System.out.println("Quantum: " + quantum + ", Context Switch: " + contextSwitch);
        System.out.println("Execution Order: " + order);
        System.out.println("\nProcess  Arrival  Burst  Waiting  Turnaround");
        System.out.println("---------------------------------------------");

        for (Process p : ps) {
            System.out.printf("%-8s%-9d%-7d%-9d%-11d\n",
                    p.getName(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    p.getWaitingTime(),
                    p.getTurnaroundTime());

            w += p.getWaitingTime();
            t += p.getTurnaroundTime();
        }

        System.out.printf("Average Waiting Time: %.2f\n", w / ps.size());
        System.out.printf("Average Turnaround Time: %.2f\n", t / ps.size());
    }

    private static void printSJF(ArrayList<Process> ps, ArrayList<String> order,
                                 int contextSwitch) {

        ArrayList<String> compressed = new ArrayList<>();
        String last = "";

        for (String s : order) {
            if (!s.equals(last)) {
                compressed.add(s);
                last = s;
            }
        }

        double w = 0, t = 0;

        System.out.println("\n========= PREEMPTIVE SJF SCHEDULING =========");
        System.out.println("Context Switch: " + contextSwitch);
        System.out.println("Execution Order: " + compressed);
        System.out.println("\nProcess  Arrival  Burst  Waiting  Turnaround");

        for (Process p : ps) {
            System.out.printf("%-8s%-9d%-7d%-9d%-11d\n",
                    p.getName(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    p.getWaitingTime(),
                    p.getTurnaroundTime());

            w += p.getWaitingTime();
            t += p.getTurnaroundTime();
        }

        System.out.printf("Average Waiting Time: %.2f\n", w / ps.size());
        System.out.printf("Average Turnaround Time: %.2f\n", t / ps.size());
    }

    
    private static void printPriority(ArrayList<Process> ps, ArrayList<String> order, int contextSwitch)
    {
        ArrayList<String> compressed = new ArrayList<>();
        String last = "";

        for (String s : order) 
        {
            if (!s.equals(last)) 
            {
                compressed.add(s);
                last = s;
            }
        }

        double w = 0, t = 0;

        System.out.println("Context Switch: " + contextSwitch);
        System.out.println("Execution Order: " + compressed);
        System.out.println("Process  Waiting  Turnaround");

        for (Process p : ps) 
        {
            System.out.printf("%-8s %-9d %-11d\n",
                    p.getName(),
                    p.getWaitingTime(),
                    p.getTurnaroundTime());

            w += p.getWaitingTime();
            t += p.getTurnaroundTime();
        }

        System.out.printf("Average Waiting Time: %.2f\n", w / ps.size());
        System.out.printf("Average Turnaround Time: %.2f\n", t / ps.size());
    }

    private static void printAG(ArrayList<Process> ps, ArrayList<String> order) {
        // Compress order for display (e.g., P1, P1, P2 -> P1, P2)
        ArrayList<String> compressed = new ArrayList<>();
        if (!order.isEmpty()) {
            String last = order.get(0);
            compressed.add(last);
            for (int i = 1; i < order.size(); i++) {
                if (!order.get(i).equals(last)) {
                    last = order.get(i);
                    compressed.add(last);
                }
            }
        }

        System.out.println("\n========= AG SCHEDULING =========");
        System.out.println("Execution Order: " + compressed);
        System.out.println("Process  Arrival  Burst  Priority  Quantum  Waiting  Turnaround");
        
        double w = 0, t = 0;
        // Sort by name for clean output
        ps.sort(Comparator.comparing(Process::getName));

        for (Process p : ps) {
            System.out.printf("%-8s%-9d%-7d%-10d%-9d%-9d%-11d\n",
                    p.getName(), p.getArrivalTime(), p.getBurstTime(), p.getPriority(), p.getQuantum(),
                    p.getWaitingTime(), p.getTurnaroundTime());
            w += p.getWaitingTime();
            t += p.getTurnaroundTime();
        }
        System.out.printf("Average Waiting Time: %.2f\n", w / ps.size());
        System.out.printf("Average Turnaround Time: %.2f\n", t / ps.size());
    }

}



class Main {
    public static void main(String[] args) {

        userinterface ui = new userinterface();
        int numProcesses = 0;
        int rrTimeQuantum = 0;
        int contextSwitchingTime = 0;
        int agingInterval = 0;

        int schedulerChoice = ui.getInputIntFor("Choose which schedulers to run:\n1. Round Robin\n2. Preemptive SJF\n3. Preemptive Priority\n4. AG Scheduler\n5. Exit\nEnter your choice (1-5): ");
        if (schedulerChoice < 1 || schedulerChoice > 5) {
            ui.display("Invalid choice. Exiting program.");
            return;
        }
        else if (schedulerChoice == 5) {
            ui.display("Exiting program.");
            return;
        }
        else if (schedulerChoice >= 1 && schedulerChoice <= 3) {
            numProcesses = ui.getInputIntFor("Enter number of processes: ");
            rrTimeQuantum = ui.getInputIntFor("Enter Round Robin Time Quantum: ");
            contextSwitchingTime = ui.getInputIntFor("Enter Context Switching Time: ");
            agingInterval = ui.getInputIntFor("Enter Aging Interval for Priority Scheduling: ");
        }
        else{
            numProcesses = ui.getInputIntFor("Enter number of processes: ");
        }
        ArrayList<Process> processes = new ArrayList<>();

        for (int i = 0; i < numProcesses; i++) {
            processes.add(ui.getProcessInput(i + 1, -1));
        }

        ui.displayEntries(processes, contextSwitchingTime, rrTimeQuantum);

        switch (schedulerChoice) {
            case 1:
                System.out.println("\n================ RUNNING ROUND ROBIN ================");
                Scheduler.roundRobin(processes, rrTimeQuantum, contextSwitchingTime);
                break;
            case 2:
                System.out.println("\n================ RUNNING PREEMPTIVE SJF ================");
                Scheduler.preemptiveSJF(processes, contextSwitchingTime);
                break;
            case 3:
                System.out.println("\n================ RUNNING PREEMPTIVE PRIORITY ================");
                Scheduler.preemptivePriority(processes, contextSwitchingTime, agingInterval);
                break;
            case 4:
                System.out.println("\n================ RUNNING AG SCHEDULER ================");
                Scheduler.AGsceduler(processes, 0);
                break;
        }
    }
}
