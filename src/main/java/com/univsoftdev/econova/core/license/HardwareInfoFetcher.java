package com.univsoftdev.econova.core.license;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

public class HardwareInfoFetcher {

    public static void main(String[] args) {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();

        String motherboardSerial = hardware.getComputerSystem().getSerialNumber();
        String hardDriveSerial = hardware.getDiskStores().get(0).getSerial(); // Primer disco duro

        String hardwareIdentifier = motherboardSerial + "-" + hardDriveSerial;

        System.out.println("Por favor, envíe el siguiente identificador de hardware:");
        System.out.println(hardwareIdentifier);
    }
}
