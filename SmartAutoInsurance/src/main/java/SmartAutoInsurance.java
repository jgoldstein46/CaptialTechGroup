import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Scanner;

/**
 * Name: michaelbartholic
 * Date: 06 January 2021
 * SmartAutoInsurance Proof of Concept for WTSC21 paper
 */

public class SmartAutoInsurance {

    // Data covers 5 minutes == 300 seconds
    public static final int fullTimeInterval = 300; // seconds

    // DEFINE ALL RULES GLOBALLY
    // isRaining is sampled at 0.5 Hz  (every 2 seconds)
    public static final double IS_RAINING_FREQ = 0.5; // Hz
    public static final String IS_RAINING_NAME = "CompanyXYZv2R1.";

    // velocity is sampled at 1 Hz (every 1 second)
    public static final double VELOCITY_FREQ = 1; // Hz
    public static final String VELOCITY_NAME = "CompanyXYZv2R2.";

    // acceleration is sample at 5 Hz (5 times per second)
    public static final double ACCELERATION_FREQ = 5; // Hz
    public static final String ACCELERATION_NAME = "CompanyXYZv2R3.";

    // Engine RPM is sampled at 5 Hz (5 times per second)
    public static final double ENGINE_RPM_FREQ = 5; // Hz
    public static final String ENGINE_RPM_NAME = "CompanyXYZv2R4.";

    public static class PublicationResults {

        SecretKey rulesKey;
        SecretKey evidenceKey;
        String eventID;

        public PublicationResults(SecretKey rules, SecretKey evidence, String id){
            this.rulesKey = rules;
            this.evidenceKey = evidence;
            this.eventID = id;
        }

    }

    public static void main(String[] args) throws IOException {

        // The proof of concept will involve running a number of examples,
        //  timing how long they take, and compiling results.

        // Step: Initialize everything you need

        final int RUN_COUNT = 1000;  // Intended number of test runs


        // Step: Run some number of test runs 1-2 for test, 1000s for real
        //  This will be a timed loop with results stored from each. Each phase
        //  can be timed separately and returned from the example.
        List<SystemExample> examples = new ArrayList<>();
        for (int i = 0; i < RUN_COUNT; ++i){

            if (i % 10 == 0 && i != 0){
                System.out.println("Completed " + i + " SystemExamples so far...");
            }

            new PrintWriter("isRaining.csv").close();
            new PrintWriter("velocity.csv").close();
            new PrintWriter("acceleration.csv").close();
            new PrintWriter("engineRPM.csv").close();

            examples.add(runSystemExample());
        }

        // Step: Compile results and print statistics about runtimes
        //  we will care about average, fastest, slowest, standard deviation for
        //  each of the individual phases, as well as how the phases compare.
        runSystemReport(examples, RUN_COUNT);
    }

    public static void runSystemReport(List<SystemExample> runExamples, int runCount){
        List<Double> riskTimes = new ArrayList<>();
        List<Double> monitorTimes = new ArrayList<>();
        List<Double> publishTimes = new ArrayList<>();
        List<Double> revealViolatedTimes = new ArrayList<>();
        List<Double> revealEvidenceTimes = new ArrayList<>();

        for (SystemExample systemExample : runExamples) {
            riskTimes.add(systemExample.getRiskRunTime());
            monitorTimes.add(systemExample.getMonitorRunTime());
            publishTimes.add(systemExample.getPublishTime());
            revealViolatedTimes.add(systemExample.getRevealViolatedTime());
            revealEvidenceTimes.add(systemExample.getRevealEvidenceTime());
        }

        DoubleSummaryStatistics riskSummary = riskTimes.stream().mapToDouble((a) -> a).summaryStatistics();
        DoubleSummaryStatistics monitorSummary = monitorTimes.stream().mapToDouble((a)->a).summaryStatistics();
        DoubleSummaryStatistics publishSummary = publishTimes.stream().mapToDouble((a)->a).summaryStatistics();
        DoubleSummaryStatistics revealViolatedSummary = revealViolatedTimes.stream().mapToDouble((a)->a).summaryStatistics();
        DoubleSummaryStatistics revealEvidenceSummary = revealEvidenceTimes.stream().mapToDouble((a)->a).summaryStatistics();

        System.out.println("Total Runs: " + runCount);
        output("LogData", monitorSummary, runCount, monitorTimes);
        output("RiskAssessment", riskSummary, runCount, riskTimes);
        output("PublishEvents", publishSummary, runCount, monitorTimes);
        output("RevealViolatedRules", revealViolatedSummary, runCount, revealViolatedTimes);
        output("RevealEvidence", revealEvidenceSummary, runCount, revealEvidenceTimes);
    }

    public static void output (String process, DoubleSummaryStatistics processSummary, int runCount, List<Double> processTimes) {
        double meanTime = processSummary.getAverage();
        double fastestTime = processSummary.getMin();
        double slowestTime = processSummary.getMax();
        double stdTime = calcStd(meanTime, runCount, processTimes);


        System.out.println("--------------------------------------------------------------------");
        System.out.println(process + ":");
        System.out.println("Average time for " + process + " " + meanTime + " nano seconds.");
        System.out.println("Fastest time for " + process + " " + fastestTime + " nano seconds.");
        System.out.println("Slowest time for " + process + " " + slowestTime + " nano seconds.");
        System.out.println("Standard deviation for " + process + " " + stdTime + " nano seconds.");
//        System.out.println("---------------------------------------------------------------------");
    }

    public static double calcStd (double mean, int count, List<Double> data) {
        double std = 0;
        for (Double dataPoint : data) {
            std += (Math.pow((dataPoint - mean ), 2)/count);
        }
        return Math.sqrt(std);
    }

    public static SystemExample runSystemExample() throws IOException{
        SystemExample systemExample = new SystemExample();

        // NOTE: we assume that data types to measure are predefined
        // NOTE: we assume that rules are predefined and accessible on blockchain

        // Prepare "randomized" example data for this run:
        double startTime = System.nanoTime();
        String[] fileNames = monitorForEvents();
        double endTime = System.nanoTime();
        systemExample.setMonitorRunTime(endTime - startTime);
//        System.out.println(fileNames);

        // Run and time risk assessment phase
        startTime = System.nanoTime();
        EventData assessmentData = riskAssessment(fileNames);
        endTime = System.nanoTime();
        systemExample.setRiskRunTime(endTime - startTime);
//        System.out.println(assessmentData.getViolatedRules());

        // Run and time blockchain publish phase for this example
        startTime = System.nanoTime();
        PublicationResults results = publishEvents(assessmentData);
        endTime = System.nanoTime();
        systemExample.setPublishTime(endTime - startTime);

        // This would be where we retrieve the protected ephemeral, single-use,
        //  RULES key from the "Can a Public Blockchain Keep a Secret" systems.
        String rulesKey = mockEphemeralKeyRetrieval();

        // Run and time blockchain info access for this example
        startTime = System.nanoTime();
        String these_rules = revealProtectedInformationViolatedRules(results.rulesKey,
                results.eventID);
        endTime = System.nanoTime();
        systemExample.setRevealViolatedTime(endTime - startTime);

        // This would be where we retrieve the protected ephemeral, single-use,
        //  RULES key from the "Can a Public Blockchain Keep a Secret" systems.
        String evidenceKey = mockEphemeralKeyRetrieval();

        // Run and time blockchain info access for this example
        startTime = System.nanoTime();
        String this_evidence = revealProtectedInformationEvidence(results.evidenceKey,
                results.eventID);
        endTime = System.nanoTime();
        systemExample.setRevealEvidenceTime(endTime - startTime);

        return systemExample;
    }

    public static String[] monitorForEvents() throws IOException {
        // Generate four (pseudo random) example files
        // Generate a sample file for isRaining
        double min = 0;
        double max = 1;

        for (int i=0; i < fullTimeInterval * IS_RAINING_FREQ; ++i) {
            double rainProb = Math.random() * (max - min) + min;
            String rainBool = "false";
            if (rainProb > .8) {
                rainBool = "true";
            }
            pushToCSV(rainBool, "isRaining.csv");
        }

        // generate velocity sample file in mph
        min = 0;
        max = 100;
        for (int i=0; i < fullTimeInterval * VELOCITY_FREQ; ++i) {

            double speed = Math.random() * (max - min + 1) + min;
            // weight it so that speeding is less likely
            if (speed > 85 && Math.random() * 2 < .6) {
                speed = speed - 10;
            }
            pushToCSV(Double.toString(speed), "velocity.csv");
        }

        // generate an acceleration file in m/s^2
        min = 0;
        max = 3.5;
        for (int i=0; i < fullTimeInterval * ACCELERATION_FREQ; ++i) {

            double accel = Math.random() * (max - min) + min;
            // weight it so that accelerating fast is less likely
            if (accel > 3.0 && Math.random() * 2 < .6) {
                accel = accel - .5;
            }
            pushToCSV(Double.toString(accel), "acceleration.csv");
        }

        // generate an Engine RPM file
        min = 0;
        max = 8000;
        for (int i=0; i < fullTimeInterval * ENGINE_RPM_FREQ; ++i) {

            int engineRPM = (int)(Math.random() * (max - min) + min);
            // weight so that high engine RPM is less likely
            if (engineRPM > 6000 && Math.random() * 2 > .6) {
                engineRPM = engineRPM - 1000;
            }
            pushToCSV(Integer.toString(engineRPM), "engineRPM.csv");
        }

        return new String[]{"isRaining.csv", "velocity.csv", "acceleration.csv", "engineRPM.csv"};
    }

    public static void pushToCSV (String value, String filename) throws IOException {
        File log;
        PrintWriter out = null;
        try {
            log = new File(filename);
            out = new PrintWriter(new FileWriter(log, true));
            out.write(value + ",");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }


    public static EventData riskAssessment(String[] filesNames) throws IOException{
        // Inputs: data files, rules
        // For proof of concept, we hard code 4 rules.
        // Rules are made of three components:
        //  - Applicable data type(s)
        //  - Logical analysis of the trigger (lambda function)
        //  - Output penalty

        long totalRisk = 0;
        String ruleViolations = "";  // 4 rules here
        boolean thisRuleViolated;
        // NOTE: might be able to run faster by only loading one at a time?
        // Load data from each of the files (ideally this would be a loop)
        List<String> isRainingData = readCSV(filesNames[0]);
        List<String> velocityData = readCSV(filesNames[1]);
        List<String> accelData = readCSV(filesNames[2]);
        List<String> engineRPMData = readCSV(filesNames[3]);

        // Check rule 1 data: (isRaining)
        //  Check rules for each data point
        //  If rule is violated, add to total risk score, and collect identifier
        thisRuleViolated = false;
        String value;
        for (String isRainingDatum : isRainingData) {
            // Logical analysis of trigger: Boolean is False
            if (isRainingDatum.equals("true")) {
                totalRisk += 5;
                thisRuleViolated = true;
            }

        }

        if (thisRuleViolated) {
            ruleViolations += IS_RAINING_NAME;
        }

        thisRuleViolated = false; // Reset violation flag before continuing

        // Check rule 2 data: (velocity)
        //  Check rules for each data point
        //  If rule is violated, add to total risk score, and collect identifier
        for (String velocityDatum : velocityData) {
            value = velocityDatum;
            if (!value.equals("") && Double.parseDouble(value) > 70) {
                totalRisk += 10;
                thisRuleViolated = true;
            }
        }
        if (thisRuleViolated) {
            ruleViolations += VELOCITY_NAME;
        }

        // Check rule 3 data: (acceleration)
        //  Check rules for each data point
        //  If rule is violated, add to total risk score, and collect identifier
        thisRuleViolated = false;
        for (String accelDatum : accelData) {
            value = accelDatum;
            if (!value.equals("") && Double.parseDouble(value) > 1.341) {  // 3mph/s
                totalRisk += 15;
                thisRuleViolated = true;
            }
        }
        if (thisRuleViolated) {
            ruleViolations += ACCELERATION_NAME;
        }

        // Check rule 4 data: (engineRPM)
        //  Check rules for each data point
        //  If rule is violated, add to total risk score, and collect identifier
        thisRuleViolated = false;
        for (String engineRPMDatum : engineRPMData) {
            value = engineRPMDatum;
            if (!value.equals("") && Integer.parseInt(value) > 6000) {
                totalRisk += 10;
                thisRuleViolated = true;
            }
        }

        if (thisRuleViolated) {
            ruleViolations += ENGINE_RPM_NAME;
        }

        String evidence = concat(isRainingData) + concat(velocityData) +
                concat(accelData) + concat(engineRPMData);
//        System.out.println(evidence);

        return new EventData(totalRisk, ruleViolations, evidence);
    }

    public static String concat(List<String> list) {
        StringBuilder total = new StringBuilder();
        for (String s : list) {
            total.append(s).append(",");
        }
        total.append(";");
        return total.toString();
    }

    // reads from csv and returns a list of its contents
    public static java.util.List<String> readCSV(String fileName) throws IOException{
        List<String> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(fileName))) {
            while (scanner.hasNextLine()) {
                List<String> line = getRecordFromLine(scanner.nextLine());
                records.addAll(line);
            }
        }
        return records;
    }

    public static List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    public static PublicationResults publishEvents(EventData eventData) {
        try {

            AES aes = new AES();

            // This is the function that needs to publish to blockchain

            // Extract risk score to publish in plaintext
//            System.out.print("Risk score: " + eventData.riskScore);

            // Extract "violated rules" list
            // Generate ephemeral symmetric key to encrypt "violated rules" list
            // Encrypt "violated rules" list
            SecretKey rules_secret = aes.generateSecret(128);
            IvParameterSpec initializationVector1 = aes.generateIv();
            String rules_cipher = aes.encryp(eventData.violatedRules, rules_secret,
                    initializationVector1);
//            System.out.print(eventData.violatedRules);

            // Extract "evidence" list
            // Generate ephemeral symmetric key to encrypt "evidence" list
            // Encrypt "evidence" list
            SecretKey evidence_secret = aes.generateSecret(128);
            IvParameterSpec initializationVector2 = aes.generateIv();
            String evidence_cipher = aes.encryp(eventData.violatedRules, evidence_secret,
                    initializationVector2);
//            System.out.print(eventData.concatenatedEvidence);

            // TODO: upload evidence data off-chain (where? just mock this?)

            // Write to blockchain (risk score; encrypted "violated rules"; url to
            //  encrypted evidence data)
//            user.connectToRemoteNode("http://172.16.145.164:8543");
//            user.loadCredential("password", "/Users/js4488/keystore/UTC--2018-12-03T18-59-35
//            .267788741Z--059bb1d4b9fbca8145be2c6d3d5f3b062a85badf");
//            user.loadContract("0x8c3647b6788489Fadae75CE789E62E4F5816e3a8");
//
//            user.storeEventDataToBlockchain(eventID, eventData.riskScore, cipher_violatedRules,
//            cipher_evidence, "02" +
//                    "/01/2020 09:00AM");

            // This would be where we hand off ephemeral, single-use keys to the
            //  "Can a Public Blockchain Keep a Secret" systems.
            if (mockEphemeralKeyPublish(rules_secret, evidence_secret)){
                return new PublicationResults(rules_secret, evidence_secret, "eventID?");
            } else {
                throw new RuntimeException("Unable to publish.");
            }
        } catch (Exception e){
            System.exit(1);
        }
        return null;
    }

    public static boolean mockEphemeralKeyPublish(SecretKey ruleKey, SecretKey evidenceKey){
        // Returns boolean representing the success of the operation
        // TODO: add some delay here which is on par with what would be expected
        //  from actually doing this operation. Refer to paper?
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex){
            Thread.currentThread().interrupt();
        }
        // TODO: make this actually the real key types to be dealing with the expected data weight?
        return true;
    }

    public static String mockEphemeralKeyRetrieval(){
        // Returns key needed to reveal info
        // TODO: add some delay here which is on par with what would be expected
        //  from actually doing this operation. Refer to paper?
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex){
            Thread.currentThread().interrupt();
        }
        return "<example_private_key>"; // TODO: make this the private key type?
    }

    public static String revealProtectedInformationViolatedRules(SecretKey ruleKey, String eventID){

        // Retrieve info of list of info from blockchain and decrypt with provided key
        String violated_rules = "";

//        user.getViolatedRulesCipherFromBlockchain(eventID);
//        user.getEvidenceCipherFromBlockchain(eventID);
//        user.getRiskScoreFromBlockchain(eventID);

//        AES aes = new AES();
//        IvParameterSpec initializationVector = aes.generateIv();
//        String violated_rules = aes.decrypt(cipherText1, ruleKey, initializationVector);
//        //System.out.println(violated_rules);

        // TODO Decrypt info using ephemeral symmetric key above

        return violated_rules;
    }

    public static String revealProtectedInformationEvidence(SecretKey evidenceKey, String eventID){

        // Retrieve info of list of info from blockchain and decrypt with provided key
        String evidence_output = "";

//        user.getViolatedRulesCipherFromBlockchain(eventID);
//        user.getEvidenceCipherFromBlockchain(eventID);
//        user.getRiskScoreFromBlockchain(eventID);

//        AES aes = new AES();
//        IvParameterSpec initializationVector = aes.generateIv();
//        String evidence_output = aes.decrypt(cipherText1, evidenceKey, initializationVector);
//        //System.out.println(evidence_output);

        // TODO Decrypt info using ephemeral symmetric key above

        return evidence_output;
    }

}
