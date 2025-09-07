package com.univsoftdev.econova;

import com.licify.Licify;
import com.licify.Licify.License;
import com.univsoftdev.econova.core.FileUtils;
import com.univsoftdev.econova.security.SecurityContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class LicenseUtils {

    public static boolean loadAndValidateTrialLicense() {
        Path licenseFile = Path.of(FileUtils.APP_DATA + "trial.lic");
        if (Files.exists(licenseFile)) {
            try {
                License license = Licify.load(licenseFile.toString());
                Licify.verify(license, SecurityContext.getPublicKey());
                SecurityContext.setLicense(license);
                return true;
            } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException ex) {
                System.getLogger(Econova.class.getName()).log(System.Logger.Level.ERROR,
                        "Error procesando licencia", ex);
                return false;
            } catch (Exception ex) {
                System.getLogger(LicenseUtils.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                return false;
            }
        }
        return false;
    }

}
