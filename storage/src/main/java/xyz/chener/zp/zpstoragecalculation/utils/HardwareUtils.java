package xyz.chener.zp.zpstoragecalculation.utils;

import oshi.SystemInfo;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

// depedency: oshi-core
public class HardwareUtils {

    public static String getHardwareUid()
    {
        SystemInfo systemInfo = new SystemInfo();
        String cpuID = systemInfo.getHardware().getProcessor().getProcessorIdentifier().getProcessorID();
        AtomicLong macID = new AtomicLong(0);
        systemInfo.getHardware().getNetworkIFs().forEach(e->{
            if (e.isConnectorPresent())
            {
                Arrays.stream(e.getMacaddr().split("[:]")).forEach(s->{
                    macID.addAndGet(Long.parseLong(s,16));
                });
            }
        });
        String diskID = systemInfo.getHardware().getDiskStores().get(0).getSerial();
        String allID = String.format("%s%s%s",cpuID,macID,diskID).replace(" ","").replace("-","");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(allID.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            allID = sb.toString();
        }catch (Exception ignored) { }
        return allID;
    }


}
