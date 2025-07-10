package com.univsoftdev.econova.component.wizard;

import com.github.cjwizard.AbstractPageFactory;
import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WizardFactory extends AbstractPageFactory {

    private static final int STEP_PRESENTACION = 0;

    protected final WizardPage[] pages = {
        new PresentacionWizardStep(),
        new DatabaseWizardStep(),
        new EmpresaWizardPage(),
        new JPanelStep3(),
        new JPanelStep3A(),
        new JPanelStep3B(),
        new JPanelStep3C(),
        new JPanelStep3C1(),
        new JPanelStep3C2(),
        new JPanelStep3C3(),
        new JPanelStep4()
    };

    @Override
    public WizardPage createPage(List<WizardPage> path, WizardSettings settings) {
        if (pages == null || pages.length == 0) {
            throw new IllegalStateException("No hay páginas definidas en el asistente");
        }

        if (path.isEmpty()) {
            return pages[STEP_PRESENTACION];
        }

        //last page viewed.
        WizardPage lastPage = path.get(path.size() - 1);

        // Lógica de ramificación especial
        if (lastPage instanceof JPanelStep3 jPanelStep3) {
            return handleJPanelStep3Branching(jPanelStep3);
        }

        // Navegación secuencial por defecto
        for (int i = 0; i < pages.length - 1; i++) {
            if (pages[i].getClass().equals(Objects.requireNonNull(lastPage).getClass())) {
                log.debug("Avanzando de {} a {}",
                        lastPage.getClass().getSimpleName(),
                        pages[i + 1].getClass().getSimpleName());
                return pages[i + 1];
            }
        }

        throw new IllegalStateException("No se encontró siguiente página para: "
                + lastPage.getClass().getSimpleName());
    }

    private WizardPage handleJPanelStep3Branching(JPanelStep3 jPanelStep3) {
        switch (jPanelStep3.getChoice()) {
            case A -> {
                return pages[4]; // JPanelStep3A
            }
            case B -> {
                return pages[5]; // JPanelStep3B
            }
            case C -> {
                return pages[6]; // JPanelStep3C
            }
            default ->
                throw new IllegalStateException("Opción no válida en JPanelStep3: "
                        + jPanelStep3.getChoice());
        }
    }
}
