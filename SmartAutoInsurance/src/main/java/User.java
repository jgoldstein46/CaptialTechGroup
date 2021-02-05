
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.math.BigInteger;

public class User {
    //    private int totalTime;
    private String UUID;
    private int totalRisk;
    private String timestamp;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    private String[][] events;
    private HashSet<String> eventFiles =  new HashSet<String>();
	
	
    protected Web3j web3j;
    protected Credentials credential;
    protected EventDataManagement2 contract;

    public User (String ID) {
        this.UUID = ID;
        this.totalRisk = 0;
        this.timestamp = dtf.format(LocalDateTime.now());
    }

    public HashSet<String> getEventFiles () {return eventFiles;}

    public String getTime () {
        return timestamp;
    }

    public void setTime(LocalDateTime newTimeStamp) {
        timestamp = dtf.format(newTimeStamp);
    }


    public void setEvents(String[][] newEvents) {
        events = newEvents;
    }

    // outputs events to a csv
    public void monitorForEvents(String[][] rawData) throws IOException{

        File log = null;
        //new File("output.txt");
        PrintWriter out = null;
        //new PrintWriter(new FileWriter(log, true));
        try {
//            out = new FileOutputStream("output.txt");
            for (int i = 0; i < rawData.length; i++) {
//                if (rawData[i][0] == "Engine RPM") {
//
//                    log = new File("Engine_RPM.csv");
//                    out = new PrintWriter(new FileWriter(log, true));
//                }

                log = new File(rawData[i][0] + ".csv");
                out = new PrintWriter(new FileWriter(log, true));
                eventFiles.add(rawData[i][0] + ".csv");
//                System.out.println(rawData[i]);
//                List<String[]> eventList = Arrays.asList(Arrays.copyOfRange(rawData[i],1,3));
//                toCSV("Engine_RPM.txt", Arrays.copyOfRange(rawData[i],1,3) )
//                        Arrays.copyOfRange(rawData[i],1,3));
//                out.write(rawData[i]);
                for (int j=2; j < rawData[i].length; j++) {
    //                System.out.println(element);
//                    int elementInt = Integer.parseInt(element);
//                    System.out.println(elementInt);
//                    convertToCSV()
//                    System.out.println(rawData[i][j]);
                    out.write(rawData[i][j]+",");
                }
                //            System.out.println(rawData[i].toString());
                if (out != null) {
                    out.close();
                }
            }

        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
	
    // reads from csv and returns list of what is in the csv
    public List<String> readFile(String fileName) throws IOException {
        Reader in = new FileReader(fileName);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
        List<String> eventsList = new ArrayList<String>();

        for (CSVRecord record: records) {
            for (String value: record) {

//                events = addX(value, events, )
                eventsList.add(value);
//                System.out.println(value);
            }
//            String[] events = new String[eventsList.size()];
//            events = eventsList.toArray(eventsList);
//            String author = record.toString();
//            System.out.println(author);
        }
        return eventsList;
    }
	
    // goes through the user's events and scores their risks
    public int scoreRisk(RuleTable ruleTable) throws IOException {

        for (String eventFile : eventFiles) {

            List<String> events = readFile(eventFile);
//            System.out.println(eventFile);
            for (String event: events) {
                ruleTable.setProp(eventFile, event);
//                if (eventFiles[i] == "acceleration.csv") {
//                    ruleTable.setAcceleration(Integer.parseInt(event));
//
//                } else if (eventFiles[i] == "")

                totalRisk += ruleTable.scoreRisk();
            }
        }

        return totalRisk;
    }

    public static void main(String[] args) throws IOException {
        String[][] events = {
                {"acceleration", String.valueOf(LocalDateTime.now()), "10"},
                {"isRaining", String.valueOf(LocalDateTime.now()), "false"}
        };


        User johnDoe = new User("1234");
	/**
	List<String> list = new ArrayList<String>();
	String[][] arr = {{"4", "5", "6"}, {"1", "2", "3"}};

        johnDoe.monitorForEvents([["Engine RPM", 2]]);
	**/
        johnDoe.monitorForEvents(events);

//        johnDoe.readFile("Engine_RPM.csv");
//        int lastIndx = "Hello.csv".lastIndexOf(".csv");
//        System.out.println("Hello.csv".substring(0,lastIndx));

//        System.out.println(johnDoe.getTime());

//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        johnDoe.setTime(LocalDateTime.now());
//        System.out.println(johnDoe.getTime());
        RuleTable ruleTable = new RuleTable();
//        System.out.println();
//        System.out.println(johnDoe.getEventFiles());
//        System.out.println(ruleTable.getAcceleration() +
//                " " + ruleTable.getIsRaining());

        System.out.println(johnDoe.scoreRisk(ruleTable));
//        System.out.println("Hello" == "Hello");
//        johnDoe.scoreRisk(ruleTable);
        SmartAutoInsurance.pushToCSV("10000", "acceleration.csv");
        SmartAutoInsurance.pushToCSV("10000", "velocity.csv");
    }
	
	
	

	protected void connectToRemoteNode(String httpService){
		this.web3j = Web3j.build(new HttpService(httpService));
		try {
			//log.info("Connected to Ethereum client version: "+ this.web3j.web3ClientVersion().send().getWeb3ClientVersion());
			System.out.println("Connected to Ethereum client version: "+ this.web3j.web3ClientVersion().send().getWeb3ClientVersion());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void loadCredential(String password, String pathToWalletFile){
		try {
			this.credential = WalletUtils.loadCredentials(password, pathToWalletFile);
			//log.info("Credentials loaded");
			System.out.println("Credentials loaded.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CipherException e) {
			e.printStackTrace();
		}
	}

	protected void loadContract(String contractAddress){
		ContractGasProvider contractGasProvider = new DefaultGasProvider();
        	this.contract = EventDataManagement2.load(contractAddress, this.web3j, this.credential, contractGasProvider); 
        	System.out.println("Contract address: " + contract.getContractAddress());
	}

	protected void shutdown(){
		this.web3j.shutdown();
		System.out.println("Web3j shut down...");
		System.out.println();
	}

	public void storeEventDataToBlockchain(String eventID, String riskScore, String voilatedRulesCipher, String evidenceCipher, String time){
		try {
			this.contract.publishEvents(this.convertToByte(eventID), riskScore ,voilatedRulesCipher, evidenceCipher, time).send();
			System.out.println("Event data stored in remote smart contract.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getViolatedRulesCipherFromBlockchain(String eventID) throws Exception{
		String violatedRulesCipher = this.contract.getViolatedRulesCipher(this.convertToByte(eventID)).send();
		return violatedRulesCipher;

	}

	public String getEvidenceCipherFromBlockchain(String eventID) throws Exception{
		String evidencesCipher = this.contract.getEvidenceCipher(this.convertToByte(eventID)).send();
		return evidencesCipher;
	}

	public String getRiskScoreFromBlockchain(String eventID) throws Exception{
		String riskScore = this.contract.getRiskScore(this.convertToByte(eventID)).send();
		return riskScore;
	}

	private byte[] convertToByte(String s){
		byte[] byteValue = s.getBytes();
        	byte[] byteValueLen32 = new byte[32];
        	System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        	return byteValueLen32;
	}
}
