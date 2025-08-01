package com.univsoftdev.econova.component.wizard;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class WizardDataOutput {
    
    public static void saveObject(Object object) {
        if (object != null) {
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("wizard.dat"))) {
                out.writeObject(object);
            } catch (FileNotFoundException ex) {
                System.getLogger(WizardDataOutput.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            } catch (IOException ex) {
                System.getLogger(WizardDataOutput.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
    }
    
    public static Object readObject(Object object) {
        if (object != null) {
            if (Files.exists(Path.of("wizard.dat"))) {
                try (ObjectInputStream out = new ObjectInputStream(new FileInputStream("wizard.dat"))) {
                    return out.readObject();
                } catch (FileNotFoundException ex) {
                    System.getLogger(WizardDataOutput.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                } catch (IOException | ClassNotFoundException ex) {
                    System.getLogger(WizardDataOutput.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
            }
        }
        return null;
    }
}
