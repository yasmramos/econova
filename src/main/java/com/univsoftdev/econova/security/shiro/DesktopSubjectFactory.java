package com.univsoftdev.econova.security.shiro;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Subject factory optimizada para aplicaciones de escritorio. Deshabilita la
 * creación de sesiones por defecto para mejorar el rendimiento en entornos de
 * escritorio donde las sesiones web no son necesarias.
 */
public class DesktopSubjectFactory extends DefaultWebSubjectFactory {

    private static final Logger log = LoggerFactory.getLogger(DesktopSubjectFactory.class);

    /**
     * Crea un Subject optimizado para aplicaciones de escritorio. Deshabilita
     * la creación automática de sesiones para reducir overhead.
     *
     * @param context el contexto para la creación del Subject
     * @return un Subject configurado para entorno desktop
     */
    @Override
    public Subject createSubject(SubjectContext context) {
        // Deshabilitar session creation por defecto para operaciones background
        // Esto mejora el rendimiento en aplicaciones desktop
        context.setSessionCreationEnabled(false);

        if (log.isDebugEnabled()) {
            log.debug("Creando Subject con session creation deshabilitado para desktop");
        }

        Subject subject = super.createSubject(context);

        if (log.isTraceEnabled()) {
            log.trace("Subject creado exitosamente para contexto: {}", context);
        }

        return subject;
    }

    /**
     * Crea un Subject con sesión habilitada cuando sea necesario. Útil para
     * operaciones que requieren estado de sesión explícito.
     *
     * @param context el contexto para la creación del Subject
     * @return un Subject con sesión habilitada
     */
    public Subject createSubjectWithSession(SubjectContext context) {
        context.setSessionCreationEnabled(true);

        if (log.isDebugEnabled()) {
            log.debug("Creando Subject con session creation habilitado");
        }

        return super.createSubject(context);
    }
}
