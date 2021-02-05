/**
 * Name: michaelbartholic
 * Date: 24 January 2021
 */
public class EventData {

    public long riskScore = 0;
    public String violatedRules = "";
    public String concatenatedEvidence = "";

    public EventData (long riskScore, String violatedRules, String evidence){
        this.riskScore = riskScore;
        this.violatedRules = violatedRules;
        this.concatenatedEvidence = evidence;
    }

    public String getViolatedRules () {
        return violatedRules;
    }

    public long getRiskScore() {
        return riskScore;
    }

    public String getConcatenatedEvidence() {
        return concatenatedEvidence;
    }
}
