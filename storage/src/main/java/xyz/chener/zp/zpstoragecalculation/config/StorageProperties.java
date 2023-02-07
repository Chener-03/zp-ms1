package xyz.chener.zp.zpstoragecalculation.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import xyz.chener.zp.zpstoragecalculation.utils.HardwareUtils;

@ConfigurationProperties(prefix = "zp.storage")
@RefreshScope
public class StorageProperties {

    public static final String HARDWARE_UID = "hardwareUid";

    private String tempLocation = System.getProperty("java.io.tmpdir");

    private String location = tempLocation+ "zpstoragecalculation/";

    private String hardwareUid = HardwareUtils.getHardwareUid();


    public String getTempLocation() {
        return tempLocation;
    }

    public void setTempLocation(String tempLocation) {
        this.tempLocation = tempLocation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getHardwareUid() {
        return hardwareUid;
    }

    public void setHardwareUid(String hardwareUid) {
        this.hardwareUid = hardwareUid;
    }
}
