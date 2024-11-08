import java.io.*;
import java.util.*;

class CPUScheduler {
    static class Process {
        int processNumber;
        int arrivalTime;
        int cpuBurst;
        int priority;
        int remainingTime;

        Process(int processNumber, int arrivalTime, int cpuBurst, int priority) {
            this.processNumber = processNumber;
            this.arrivalTime = arrivalTime;
            this.cpuBurst = cpuBurst;
            this.priority = priority;
            this.remainingTime = cpuBurst;
        }
    }

    public static void main(String[] args) {
        try {
            List<Process> processes = new ArrayList<>();
            String algorithm = "";
            int timeQuantum = 0;

            // Read input file
            try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
                algorithm = br.readLine().trim();
                if (algorithm.startsWith("RR")) {
                    timeQuantum = Integer.parseInt(algorithm.split(" ")[1]);
                    algorithm = "RR";
                }
                int numProcesses = Integer.parseInt(br.readLine().trim());

                for (int i = 0; i < numProcesses; i++) {
                    String[] tokens = br.readLine().trim().split(" ");
                    int processNumber = Integer.parseInt(tokens[0]);
                    int arrivalTime = Integer.parseInt(tokens[1]);
                    int cpuBurst = Integer.parseInt(tokens[2]);
                    int priority = Integer.parseInt(tokens[3]);
                    processes.add(new Process(processNumber, arrivalTime, cpuBurst, priority));
                }
            }

            List<String> output = new ArrayList<>();
            double avgWaitingTime = 0.0;

            switch (algorithm) {
                case "RR":
                    avgWaitingTime = roundRobin(processes, timeQuantum, output);
                    break;
                case "SJF":
                    avgWaitingTime = shortestJobFirst(processes, output);
                    break;
                case "PR_noPREMP":
                    avgWaitingTime = prioritySchedulingNoPreemption(processes, output);
                    break;
                case "PR_withPREMP":
                    avgWaitingTime = prioritySchedulingWithPreemption(processes, output);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown scheduling algorithm");
            }

            output.add(String.format("AVG Waiting Time: %.2f", avgWaitingTime));

            // Write output to output.txt
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
                bw.write(algorithm);
                bw.newLine();
                for (String line : output) {
                    bw.write(line);
                    bw.newLine();
                }
            }

            System.out.println("Scheduling complete. Results written to output.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Round Robin Scheduling
    private static double roundRobin(List<Process> processes, int timeQuantum, List<String> output) {
        Queue<Process> queue = new LinkedList<>();
        int currentTime = 0;
        int totalWaitingTime = 0;
        int completedProcesses = 0;

        // Sort processes by arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int index = 0;

        while (completedProcesses < processes.size()) {
            // Add processes that have arrived to the queue
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                queue.add(processes.get(index));
                index++;
            }

            if (queue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process currentProcess = queue.poll();
            int executionTime = Math.min(currentProcess.remainingTime, timeQuantum);
            currentTime += executionTime;
            currentProcess.remainingTime -= executionTime;
            output.add(currentTime + " " + currentProcess.processNumber);

            if (currentProcess.remainingTime > 0) {
                // If not finished, re-queue the process
                queue.add(currentProcess);
            } else {
                // Calculate waiting time
                totalWaitingTime += currentTime - currentProcess.arrivalTime - currentProcess.cpuBurst;
                completedProcesses++;
            }
        }

        return (double) totalWaitingTime / processes.size();
    }

    // Shortest Job First Scheduling
    private static double shortestJobFirst(List<Process> processes, List<String> output) {
        PriorityQueue<Process> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.cpuBurst));
        int currentTime = 0;
        int totalWaitingTime = 0;
        int completedProcesses = 0;
        int index = 0;

        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        while (completedProcesses < processes.size()) {
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                queue.add(processes.get(index));
                index++;
            }

            if (queue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process currentProcess = queue.poll();
            output.add(currentTime + " " + currentProcess.processNumber);
            currentTime += currentProcess.cpuBurst;
            totalWaitingTime += currentTime - currentProcess.arrivalTime - currentProcess.cpuBurst;
            completedProcesses++;
        }

        return (double) totalWaitingTime / processes.size();
    }

    // Priority Scheduling without Preemption
    private static double prioritySchedulingNoPreemption(List<Process> processes, List<String> output) {
        PriorityQueue<Process> queue = new PriorityQueue<>(
                (p1, p2) -> p1.priority != p2.priority ? Integer.compare(p1.priority, p2.priority)
                        : Integer.compare(p1.processNumber, p2.processNumber));
        int currentTime = 0;
        int totalWaitingTime = 0;
        int completedProcesses = 0;
        int index = 0;

        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        while (completedProcesses < processes.size()) {
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                queue.add(processes.get(index));
                index++;
            }

            if (queue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process currentProcess = queue.poll();
            output.add(currentTime + " " + currentProcess.processNumber);
            currentTime += currentProcess.cpuBurst;
            totalWaitingTime += currentTime - currentProcess.arrivalTime - currentProcess.cpuBurst;
            completedProcesses++;
        }

        return (double) totalWaitingTime / processes.size();
    }

    // Priority Scheduling with Preemption
    private static double prioritySchedulingWithPreemption(List<Process> processes, List<String> output) {
        PriorityQueue<Process> queue = new PriorityQueue<>(
                (p1, p2) -> p1.priority != p2.priority ? Integer.compare(p1.priority, p2.priority)
                        : Integer.compare(p1.processNumber, p2.processNumber));
        int currentTime = 0;
        int totalWaitingTime = 0;
        int completedProcesses = 0;
        int index = 0;

        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        while (completedProcesses < processes.size()) {
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                queue.add(processes.get(index));
                index++;
            }

            if (queue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process currentProcess = queue.poll();
            output.add(currentTime + " " + currentProcess.processNumber);
            int executionTime = currentProcess.remainingTime;

            if (!queue.isEmpty() && queue.peek().priority < currentProcess.priority) {
                executionTime = queue.peek().arrivalTime - currentTime;
            }

            currentTime += executionTime;
            currentProcess.remainingTime -= executionTime;

            if (currentProcess.remainingTime > 0) {
                queue.add(currentProcess);
            } else {
                totalWaitingTime += currentTime - currentProcess.arrivalTime - currentProcess.cpuBurst;
                completedProcesses++;
            }
        }

        return (double) totalWaitingTime / processes.size();
    }
}
