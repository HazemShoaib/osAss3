import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SchedulerTest 
{
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() 
    {
        System.setOut(new PrintStream(outContent));
    }

    public void restoreStreams() 
    {
        System.setOut(originalOut);
    }

    private JSONArray loadTestCases(String fileName) throws IOException 
    {
        String content = new String(Files.readAllBytes(Paths.get(fileName)));
        return new JSONArray(content);
    }

    private void runTestFor(String algorithm, JSONObject testCase) 
    {
        JSONObject input = testCase.getJSONObject("input");
        JSONObject expectedOutput = testCase.getJSONObject("expectedOutput").getJSONObject(algorithm);

        int contextSwitch = input.getInt("contextSwitch");
        int rrQuantum = input.optInt("rrQuantum", 0);
        int agingInterval = input.optInt("agingInterval", 0);

        ArrayList<Process> processes = new ArrayList<>();
        JSONArray processesJson = input.getJSONArray("processes");
        for (int i = 0; i < processesJson.length(); i++) 
        {
            JSONObject p = processesJson.getJSONObject(i);
            processes.add(new Process(
                    p.getString("name"),
                    p.getInt("arrival"),
                    p.getInt("burst"),
                    p.getInt("priority")
            ));
        }

        outContent.reset();

        switch (algorithm) 
        {
            case "SJF":
                Scheduler.preemptiveSJF(processes, contextSwitch);
                break;
            case "RR":
                Scheduler.roundRobin(processes, rrQuantum, contextSwitch);
                break;
            case "Priority":
                Scheduler.preemptivePriority(processes, contextSwitch, agingInterval);
                break;
        }

        restoreStreams();
        String actualOutput = outContent.toString();

        List<String> expectedExecutionOrder = new ArrayList<>();
        expectedOutput.getJSONArray("executionOrder").forEach(item -> expectedExecutionOrder.add((String) item));
        String executionOrderString = "Execution Order: " + expectedExecutionOrder.toString();
        assertEquals(true, actualOutput.contains(executionOrderString));

        JSONArray expectedProcessResults = expectedOutput.getJSONArray("processResults");
        for (int i = 0; i < expectedProcessResults.length(); i++) 
        {
            JSONObject expectedResult = expectedProcessResults.getJSONObject(i);
            String name = expectedResult.getString("name");
            int waitingTime = expectedResult.getInt("waitingTime");
            int turnaroundTime = expectedResult.getInt("turnaroundTime");

            String processLineRegex = String.format("%-8s.*%-9d.*%-11d", name, waitingTime, turnaroundTime);
            boolean matches = actualOutput.matches("(?s).*" + processLineRegex + ".*");
        }


        double expectedAvgWaitingTime = expectedOutput.getDouble("averageWaitingTime");
        double expectedAvgTurnaroundTime = expectedOutput.getDouble("averageTurnaroundTime");

        String avgWaitingString = String.format("Average Waiting Time: %.2f", expectedAvgWaitingTime);
        String avgTurnaroundString = String.format("Average Turnaround Time: %.2f", expectedAvgTurnaroundTime);

        assertEquals(true, actualOutput.contains(avgWaitingString));
        assertEquals(true, actualOutput.contains(avgTurnaroundString));
    }

 
    @Test
    @DisplayName("Test Case 1: Basic mixed arrivals - SJF")
    void testSJF_BasicMixedArrivals() throws IOException 
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("SJF", testCases.getJSONObject(0));
    }

    @Test
    @DisplayName("Test Case 1: Basic mixed arrivals - RR")
    void testRR_BasicMixedArrivals() throws IOException 
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("RR", testCases.getJSONObject(0));
    }

    @Test
    @DisplayName("Test Case 1: Basic mixed arrivals - Priority")
    void testPriority_BasicMixedArrivals() throws IOException 
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("Priority", testCases.getJSONObject(0));
    }

    @Test
    @DisplayName("Test Case 2: All processes arrive at time 0 - SJF")
    void testSJF_AllAtZero() throws IOException 
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("SJF", testCases.getJSONObject(1));
    }
    
    @Test
    @DisplayName("Test Case 2: All processes arrive at time 0 - SJF")
    void testSRR_AllAtZero() throws IOException 
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("RR", testCases.getJSONObject(1));
    }
    
    @Test
    @DisplayName("Test Case 2: All processes arrive at time 0 - SJF")
    void testSPriority_AllAtZero() throws IOException 
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("Priority", testCases.getJSONObject(1));
    }
    
    @Test
    @DisplayName("Test Case 3: Varied burst times with starvation risk - SJF")
    void testSJF_StarvationRisk() throws IOException 
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("SJF", testCases.getJSONObject(2));
    }

    @Test
    @DisplayName("Test Case 3: Varied burst times with starvation risk - RR")
    void testRR_StarvationRisk() throws IOException 
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("RR", testCases.getJSONObject(2));
    }

    @Test
    @DisplayName("Test Case 3: Varied burst times with starvation risk - Priority")
    void testPriority_StarvationRisk() throws IOException 
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("Priority", testCases.getJSONObject(2));
    }

    @Test
    @DisplayName("Test Case 4: Large bursts with gaps in arrivals - SJF")
    void testSJF_LargeBursts() throws IOException
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("SJF", testCases.getJSONObject(3));
    }

    @Test
    @DisplayName("Test Case 4: Large bursts with gaps in arrivals - RR")
    void testRR_LargeBursts() throws IOException 
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("RR", testCases.getJSONObject(3));
    }

    @Test
    @DisplayName("Test Case 4: Large bursts with gaps in arrivals - Priority")
    void testPriority_LargeBursts() throws IOException 
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("Priority", testCases.getJSONObject(3));
    }

    @Test
    @DisplayName("Test Case 5: Short bursts with high frequency - SJF")
    void testSJF_ShortBursts() throws IOException 
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("SJF", testCases.getJSONObject(4));
    }

    @Test
    @DisplayName("Test Case 5: Short bursts with high frequency - RR")
    void testRR_ShortBursts() throws IOException
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("RR", testCases.getJSONObject(4));
    }

    @Test
    @DisplayName("Test Case 5: Short bursts with high frequency - Priority")
    void testPriority_ShortBursts() throws IOException
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("Priority", testCases.getJSONObject(4));
    }

    @Test
    @DisplayName("Test Case 6: Mixed scenario - comprehensive test - SJF")
    void testSJF_MixedScenario() throws IOException
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("SJF", testCases.getJSONObject(5));
    }

    @Test
    @DisplayName("Test Case 6: Mixed scenario - comprehensive test - RR")
    void testRR_MixedScenario() throws IOException 
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("RR", testCases.getJSONObject(5));
    }

    @Test
    @DisplayName("Test Case 6: Mixed scenario - comprehensive test - Priority")
    void testPriority_MixedScenario() throws IOException 
    {
        JSONArray testCases = loadTestCases("src/test/java/resources/test_cases.json");
        runTestFor("Priority", testCases.getJSONObject(5));
    }
}
