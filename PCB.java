
public class PCB {
    String processID;
    int priority;
    int arrivalTime;
    int newarrivalTime;
    int originalCpuBurst;
    int cpuBurst;
    int startTime;
    int terminationTime;
    int turnaroundTime;
    int waitingTime;
    int responseTime;

    public PCB(String processID, int priority, int arrivalTime, int cpuBurst) {
        this.processID = processID;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.newarrivalTime = arrivalTime;
        this.originalCpuBurst = cpuBurst;
        this.cpuBurst = cpuBurst;
        this.startTime = -1; // Initialize with -1 indicating not yet started 
        this.terminationTime = -1; // Initialize with -1 indicating not yet terminated
        this.turnaroundTime = 0;
        this.waitingTime = 0;
        this.responseTime = 0;
    }

    public PCB() {
    }
}
