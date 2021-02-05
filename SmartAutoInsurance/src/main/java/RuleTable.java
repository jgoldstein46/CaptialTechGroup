public class RuleTable {
    private boolean isRaining;
    private int acceleration;
    private int risk;
    public RuleTable(boolean raining, int inputAcceleration) {
        isRaining = raining;
        acceleration = inputAcceleration;
    }

    public RuleTable(){
        isRaining = false;
        acceleration = 0;
    }

    public boolean getIsRaining () {
        return isRaining;
    }

    public int getAcceleration () {
        return acceleration;
    }

    public void setProp (String prop, String value) {
        int lastIndx = prop.lastIndexOf(".csv");
        prop = prop.substring(0,lastIndx);
//        prop = prop.substring(0,-3);
//        System.out.println(prop);
//        System.out.println(prop.equals("acceleration"));
        if (!value.equals("") && prop.equals("acceleration")) {
//            System.out.println(value);
            acceleration = Integer.parseInt(value);
        } else if (!value.equals("") && prop.equals("isRaining")) {
            isRaining = Boolean.parseBoolean(value);
        }
    }

    public void setAcceleration (int accel) {
        acceleration = accel;
    }
    public void setIsRaining (boolean raining) {
        isRaining = raining;
    }

    public int scoreRisk () {
        int totalRisk = 0;
//        System.out.println(acceleration);
//        System.out.println(isRaining);
        if (isRaining) {
            totalRisk += 5;
        }
        if (acceleration > 5){
            totalRisk += 10;
        }
        return totalRisk;
    }


}
