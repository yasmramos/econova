package com.univsoftdev.econova.security;

public class Roles {

    public static String USER;

    private Roles() {
        // Utility class
    }
    
    public static final String AUD_AUDITOR = "aud_auditor";
    public static final String CON_CONSULTOR = "con_consultor";
    public static final String CON_CONTADOR_OPERADOR = "con_contador_operador";
    public static final String CON_CONTADOR_PRINCIPAL = "con_contador_principal";
    public static final String CON_ESPECIALISTA_CONTABILIDAD = "con_especialista_contabilidad";
    public static final String CON_ESPECIALISTA_PRINCIPAL = "con_especialista_principal";
    public static final String CON_OPERADOR_INDICADORES = "con_operador_indicadores";
    public static final String CON_SIMPLIFICADO = "con_simplificado";
    public static final String COS_CONSULTOR = "cos_consultor";
    public static final String COS_CONTADOR_OPERADOR = "cos_contador_operador";
    public static final String COS_ESPECIALISTA_COSTO = "cos_especialista_costo";
    public static final String COS_ESPECIALISTA_PRINCIPAL = "cos_especialista_principal";
    public static final String DB_ACCESS_ADMIN = "db_access_admin";
    public static final String DB_BACKUP_OPERATOR = "db_backup_operator";
    public static final String DB_DATA_READER = "db_data_reader";
    public static final String DB_DATA_WRITER = "db_data_writer";
    public static final String DB_DDL_ADMIN = "db_ddl_admin";
    public static final String DB_DENY_DATA_READER = "db_deny_data_reader";
    public static final String DB_DENY_DATA_WRITER = "db_deny_data_writer";
    public static final String SYSTEM_ADMIN = "SYSTEM_ADMIN";
    public static final String ECONOMIC_ADMIN = "ECONOMIC_ADMIN";
    public static final String AUDITOR = "AUDITOR";
    public static final String CONSULTANT = "CONSULTANT";
    public static final String ACCOUNTING_OPERATOR = "ACCOUNTING_OPERATOR";
    public static final String FINANCIAL_ANALYST = "FINANCIAL_ANALYST";
    public static final String READ_ONLY = "READ_ONLY";
    public static final String ACCOUNTANT = "ACCOUNTANT";
}
