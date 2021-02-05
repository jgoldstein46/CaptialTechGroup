public class SystemExample {
    private Double monitorRunTime;
    private Double riskRunTime;
    private Double publishTime;
    private Double accessTime;
    private Double revealViolatedTime;
    private Double revealEvidenceTime;

    public SystemExample () {
        monitorRunTime = 0.0;
        riskRunTime = 0.0;
        publishTime = 0.0;
        accessTime = 0.0;
        revealViolatedTime = 0.0;
        revealEvidenceTime = 0.0;
    }

    public Double getAccessTime() {
        return accessTime;
    }

    public Double getMonitorRunTime() {
        return monitorRunTime;
    }

    public Double getPublishTime() {
        return publishTime;
    }

    public Double getRevealViolatedTime() {
        return revealViolatedTime;
    }

    public Double getRevealEvidenceTime() {return revealEvidenceTime;}

    public Double getRiskRunTime() {
        return riskRunTime;
    }

    public void setAccessTime(Double accessTime) {
        this.accessTime = accessTime;
    }

    public void setMonitorRunTime(Double monitorRunTime) {
        this.monitorRunTime = monitorRunTime;
    }

    public void setRevealEvidenceTime(Double revealEvidenceTime) {this.revealEvidenceTime = revealEvidenceTime;}

    public void setRevealViolatedTime(Double revealViolatedTime) {this.revealViolatedTime = revealViolatedTime; }

    public void setPublishTime(Double publishTime) {
        this.publishTime = publishTime;
    }

    public void setRiskRunTime(Double riskRunTime) {
        this.riskRunTime = riskRunTime;
    }
}

