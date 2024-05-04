
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.*;

public class Driver {

    List<PCB> q1;
    List<PCB> q2;
    int currentTime;

    public Driver() {
        q1 = new ArrayList<>();
        q2 = new ArrayList<>();
        int currentTime = 0;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Driver scheduler = new Driver();
        int numProcesses = 0;

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Enter process information.");
            System.out.println("2. Report detailed information about each process and different scheduling criteria.");
            System.out.println("3. Exit the program.");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:

                    System.out.print("\nEnter the number of processes (P): ");
                    numProcesses = scanner.nextInt();
                    System.out.println(" ");
                    for (int i = 0; i < numProcesses; i++) {
                        System.out.println("Enter process " + (i + 1) + " information:");
                        System.out.print("Priority (1 or 2): ");
                        int priority = scanner.nextInt();
                        System.out.print("Arrival time: ");
                        int arrivalTime = scanner.nextInt();
                        System.out.print("CPU burst: ");
                        int cpuBurst = scanner.nextInt();
                        scheduler.addProcess("P" + (i + 1), priority, arrivalTime, cpuBurst);
                        System.out.println("----");
                    }
                    break;
                case 2:
                    // Print scheduling order
                    if (numProcesses != 0) {
                        scheduler.currentTime = 0;
                        System.out.print("\nScheduling Order: [ ");
                        scheduler.schedule(numProcesses);
                    } else {
                        System.out.print("\nTHERE IS NO processes\n");
                    }

                    break;
                case 3:
                    System.out.println("Exiting the program.");
                    System.exit(0);
                default:
            }
        }
    }//end main

    public void addProcess(String processID, int priority, int arrivalTime, int cpuBurst) {
        PCB process = new PCB(processID, priority, arrivalTime, cpuBurst);
        if (priority == 1) {
            q1.add(process);
        } else {
            q2.add(process);
        }
    }

    public String calculateAverages(List<PCB> scheduleOrder) {
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;
        int totalResponseTime = 0;
        // Calculate totals
        for (PCB process : scheduleOrder) {
            totalTurnaroundTime += process.turnaroundTime;
            totalWaitingTime += process.waitingTime;
            totalResponseTime += process.responseTime;
        }
        // Calculate averages
        double avgTurnaroundTime = (double) totalTurnaroundTime / scheduleOrder.size();
        double avgWaitingTime = (double) totalWaitingTime / scheduleOrder.size();
        double avgResponseTime = (double) totalResponseTime / scheduleOrder.size();
        // Format averages
        String averages = "Average Turnaround Time: " + avgTurnaroundTime + "\n"
                + "Average Waiting Time: " + avgWaitingTime + "\n"
                + "Average Response Time: " + avgResponseTime;
        return averages;
    }

    public void schedule(int numProcesses) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Report.txt"))) {
            //SORTING THE LIST BY ARRIVAL TIME
            Collections.sort(q1, Comparator.comparingInt(process -> process.newarrivalTime));
            Collections.sort(q2, Comparator.comparingInt(process -> process.arrivalTime));
            boolean flag = true;
            int num = 0;
            List<PCB> scheduleOrder = new ArrayList<>();
            ArrayList<String> printOrder = new ArrayList<>();
            writer.write("Scheduling Order: [ ");

            while (flag) {
                //RR with Q=3
                //the second condition in the if statement to make sure we don't schedule any process befor it's arrival time
                if (!q1.isEmpty() && q1.get(0).arrivalTime <= currentTime) {
                    PCB currentProcess = q1.remove(0);
                    if (currentProcess.startTime == -1) {
                        num++;
                        currentProcess.startTime = currentTime;//Set start time only when the process starts
                    }
                    
                    //add the process in the print order list
                    printOrder.add(currentProcess.processID);

                    int remainingBurst = currentProcess.cpuBurst;
                    if (remainingBurst > 3) {
                        currentTime += 3;
                        currentProcess.newarrivalTime = currentTime;
                        currentProcess.cpuBurst -= 3;
                        q1.add(currentProcess);
                        Collections.sort(q1, Comparator.comparingInt(process -> process.newarrivalTime));
                    } else {
                        currentTime += remainingBurst;
                        currentProcess.responseTime = currentProcess.startTime - currentProcess.arrivalTime;
                        currentProcess.terminationTime = currentTime; // Set termination time
                        //when the process finishes
                        currentProcess.turnaroundTime = currentProcess.terminationTime - currentProcess.arrivalTime;
                        currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.originalCpuBurst;
                        scheduleOrder.add(currentProcess);
                    }

                    ///////////////////////////////////////////////////////////////////////////////////////////////////
                    //SJF
                    //the second condition in the if statement to make sure we don't schedule any process befor it's arrival time
                } else if (!q2.isEmpty() && q2.get(0).arrivalTime <= currentTime) {
                    PCB currentProcess = null;
                    List<PCB> checking = new ArrayList<>();
                    //loop for saving the processes that was arrived
                    for (PCB process : q2) {
                        if (process.arrivalTime <= currentTime) {
                            checking.add(process);
                        }
                    }//end for 

                    int smallestCpuBurst = Integer.MAX_VALUE;
                    //loop for searching the smalles CPU burst
                    for (PCB process : checking) {
                        if (process.originalCpuBurst < smallestCpuBurst) {
                            currentProcess = process;
                            smallestCpuBurst = process.originalCpuBurst;
                        }
                    }//end for

                    //if it was the first time to enter the CPU
                    if (currentProcess.startTime == -1) {
                        //num++;
                        currentProcess.startTime = currentTime;
                        currentProcess.responseTime = currentProcess.startTime - currentProcess.arrivalTime;
                    }

                    //add the process in the print order list
                     printOrder.add(currentProcess.processID);
                     
                     
                    //this do-while loop is to make the process working until there's a process in q1 it will stop
                    do {
                        currentTime++;
                        currentProcess.cpuBurst--;
                    } while (!(!q1.isEmpty() && q1.get(0).arrivalTime <= currentTime) && currentProcess.cpuBurst != 0);

                    //if the process is finish
                    if (currentProcess.cpuBurst == 0) {
                        currentProcess.terminationTime = currentTime;
                        currentProcess.turnaroundTime = currentProcess.terminationTime - currentProcess.arrivalTime;
                        currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.originalCpuBurst;
                        scheduleOrder.add(currentProcess);
                        q2.remove(currentProcess);
                    } else {
                        Collections.sort(q2, Comparator.comparingInt(process -> process.newarrivalTime));
                    }

                    ///////////////////////////////////////////////////////////////////////////////////////////////////
                } else if (!q1.isEmpty() || !q2.isEmpty()) {
                    //will enter this if statemnet if there's no processes arrives yet
                    currentTime++;
                } ///////////////////////////////////////////////////////////////////////////////////////////////////
                else {
                    //will enter here if the two queues are empty
                    flag = false;
                }

            }//end while

            
            //this loop for not printing "|" after the last process
            int i = 0;
            for (String element : printOrder) {
                if (i == (printOrder.size() - 1)) {
                    System.out.print(element);
                    writer.write(element);
                } else {
                    System.out.print(element + " | ");
                    writer.write(element + " | ");
                    i++;
                }
                
            }

            System.out.print(" ]");
            writer.write(" ]");
            System.out.println("\n" + "\n" + "ProcessID | Pr | AT | Burst | ST | ET | TT | WT | RT");
            writer.write("\n" + "\n" + "ProcessID | Pr | AT | Burst | ST | ET | TT | WT | RT " + "\n");

            Collections.sort(scheduleOrder, (s1, s2) -> {
                int p1 = Integer.parseInt(s1.processID.substring(1)); // Extracting number after 'P'
                int p2 = Integer.parseInt(s2.processID.substring(1));
                return Integer.compare(p1, p2);
            });

            for (PCB process : scheduleOrder) {

                System.out.println(process.processID + " | "
                        + process.priority + " | "
                        + process.arrivalTime + " | "
                        + process.originalCpuBurst + " | "
                        + process.startTime + " | "
                        + process.terminationTime + " | "
                        + process.turnaroundTime + " | "
                        + process.waitingTime + " | "
                        + process.responseTime);

                writer.write(process.processID + " | "
                        + process.priority + " | "
                        + process.arrivalTime + " | "
                        + process.originalCpuBurst + " | "
                        + process.startTime + " | "
                        + process.terminationTime + " | "
                        + process.turnaroundTime + " | "
                        + process.waitingTime + " | "
                        + process.responseTime + "\n");
            }
            System.out.println(calculateAverages(scheduleOrder));
            writer.write(calculateAverages(scheduleOrder));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}//end of class
