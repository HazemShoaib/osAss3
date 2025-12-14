class userinterface{

    void display (String message){
        System.out.println(message);
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
        System.out.println("Hello, World!");
    }
}