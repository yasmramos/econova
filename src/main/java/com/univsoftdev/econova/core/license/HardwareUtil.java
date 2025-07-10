package com.univsoftdev.econova.core.license;

import java.net.NetworkInterface;
import java.util.Enumeration;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class HardwareUtil {

    public static String getMotherboardSerialNumber() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        return hardware.getComputerSystem().getSerialNumber();
    }

    public static String getHardDriveSerialNumber() {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        return hardware.getDiskStores().get(0).getSerial(); // Obtiene el primer disco duro
    }

    public static String getHardwareIdentifier() {
        String os = System.getProperty("os.name");
        String motherboardSerial = getMotherboardSerialNumber();
        String hardDriveSerial = getHardDriveSerialNumber();
        return os + "-" + motherboardSerial + "-" + hardDriveSerial;
    }

    public static String getMacAddress() throws Exception {
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface network = networkInterfaces.nextElement();
            byte[] mac = network.getHardwareAddress();
            if (mac != null) {
                StringBuilder sb = new StringBuilder();
                for (byte b : mac) {
                    sb.append(String.format("%02X", b));
                }
                return sb.toString();
            }
        }
        throw new Exception("No MAC address found");
    }
}
