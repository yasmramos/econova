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

    private final List<WizardPage> pages;

    public WizardFactory() {
        this.pages = List.of(
                new EmpresaWizardPage(),
                new DatabaseWizardPage(),
                new UsuariosWizardPage(),
                new JPanelStep3()
        );
    }

    @Override
    public WizardPage createPage(List<WizardPage> path, WizardSettings settings) {
        if (pages == null || pages.isEmpty()) {
            throw new IllegalStateException("No hay páginas definidas en el asistente");
        }

        if (path.isEmpty()) {
            return pages.get(STEP_PRESENTACION);
        }

        //last page viewed.
        WizardPage lastPage = path.get(path.size() - 1);

        // Lógica de ramificación especial
        if (lastPage instanceof JPanelStep3 jPanelStep3) {
            return handleJPanelStep3Branching(jPanelStep3);
        }

        // Navegación secuencial por defecto
        for (int i = 0; i < pages.size() - 1; i++) {
            if (pages.get(i).getClass().equals(Objects.requireNonNull(lastPage).getClass())) {
                return pages.get(i + 1);
            }
        }

        throw new IllegalStateException("No se encontró siguiente página para: "
                + lastPage.getClass().getSimpleName());
    }

    private WizardPage handleJPanelStep3Branching(JPanelStep3 jPanelStep3) {
        switch (jPanelStep3.getChoice()) {
            case A -> {
                return pages.get(4); // JPanelStep3A
            }
            case B -> {
                return pages.get(5); // JPanelStep3B
            }
            case C -> {
                return pages.get(6); // JPanelStep3C
            }
            default ->
                throw new IllegalStateException("Opción no válida en JPanelStep3: "
                        + jPanelStep3.getChoice());
        }
    }

    public List<WizardPage> getPages() {
        return pages;
    }

}
