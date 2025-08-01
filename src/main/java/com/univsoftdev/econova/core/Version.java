package com.univsoftdev.econova.core;

import java.io.Serializable;

/**
 *
 * @author Yasmany Ramos Garcia
 */
public class Version implements Comparable<Version>, Serializable {

    private static final long serialVersionUID = 1L;
    private int major;
    private int minor;
    private int patch;
    private int build;
    private String preRelease; //Estado de la versión (ej. "beta" , "alpha")
    private int preReleaseNumber; //Número de la versión pre-lanzamiento
    private boolean maintenance; // Indica si es una versión de mantenimiento

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public Version(int major, int minor, int patch, int build) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.build = build;
    }

    public Version(int major, int minor, int patch, int build, String preRelease, int preReleaseNumber, boolean maintenance) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.build = build;
        this.preRelease = preRelease;
        this.preReleaseNumber = preReleaseNumber;
        this.maintenance = maintenance;
    }

    public boolean isMaintenance() {
        return maintenance;
    }

    public void setMaintenance(boolean maintenance) {
        this.maintenance = maintenance;
    }

    public String getPreRelease() {
        return preRelease;
    }

    public void setPreRelease(String preRelease) {
        this.preRelease = preRelease;
    }

    public int getPreReleaseNumber() {
        return preReleaseNumber;
    }

    public void setPreReleaseNumber(int preReleaseNumber) {
        this.preReleaseNumber = preReleaseNumber;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getPatch() {
        return patch;
    }

    public void setPatch(int patch) {
        this.patch = patch;
    }

    public int getBuild() {
        return build;
    }

    public void setBuild(int build) {
        this.build = build;
    }

    @Override
    public String toString() {
        String version = major + "." + minor + "." + patch + "." + build;
        if (maintenance) {
            version += " (Maintenance)";
        }
        if (preRelease != null) {
            version += "-" + preRelease + (preReleaseNumber > 0 ? "." + preReleaseNumber : "");
        }
        return version;
    }

    @Override
    public int compareTo(Version other) {
        if (this.major != other.major) {
            return Integer.compare(major, other.major);
        }
        if (this.minor != other.minor) {
            return Integer.compare(minor, other.minor);
        }
        if (this.patch != other.patch) {
            return Integer.compare(patch, other.patch);
        }
        if (this.build != other.build) {
            return Integer.compare(build, other.build);
        }
        if (this.preRelease == null && other.preRelease == null) {
            return 0; //Ambos son versiones estables
        }
        if (this.preRelease == null) {
            return 1; //La versión estable es mayor que la de pre-lanzamiento
        }
        if (other.preRelease == null) {
            return -1; //La versión pre-lanzamiento es menor que la estable
        }

        //Comparar por nombre de pre-lanzamiento
        int preReleaseComparison = this.preRelease.compareTo(other.preRelease);
        if (preReleaseComparison != 0) {
            return preReleaseComparison;
        }

        //Comparar números de pre-lanzamiento
        return Integer.compare(this.preReleaseNumber, other.preReleaseNumber);
    }
}
