package net.maps.navigation.gps.location.sondermap.bean;

public class AgentBean {


    /**
     * status : 1
     * uset : Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36
     */

    private String status;
    private String uset;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUset() {
        return uset;
    }

    public void setUset(String uset) {
        this.uset = uset;
    }

    @Override
    public String toString() {
        return "AgentBean{" +
                "status='" + status + '\'' +
                ", uset='" + uset + '\'' +
                '}';
    }
}
