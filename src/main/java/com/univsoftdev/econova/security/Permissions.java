package com.univsoftdev.econova.security;

public class Permissions {

    // Permissions generales del sistema
    public static final String CREATE_USER = "user:create";
    public static final String EDIT_USER = "user:edit";
    public static final String DELETE_USER = "user:delete";
    public static final String VIEW_USERS = "user:view";

    // Permissions para módulo contable
    public static final String ACCOUNTING_VIEW = "accounting:view";
    public static final String ACCOUNTING_CREATE = "accounting:create";
    public static final String ACCOUNTING_EDIT = "accounting:edit";
    public static final String ACCOUNTING_DELETE = "accounting:delete";
    public static final String ACCOUNTING_APPROVE = "accounting:approve";

    // Permissions para gestión de comprobantes
    public static final String VOUCHER_CREATE = "voucher:create";
    public static final String VOUCHER_EDIT = "voucher:edit";
    public static final String VOUCHER_DELETE = "voucher:delete";
    public static final String VOUCHER_VIEW = "voucher:view";
    public static final String VOUCHER_APPROVE = "voucher:approve";
    public static final String VOUCHER_ANULATE = "voucher:anulate";

    // Permissions para reportes
    public static final String REPORT_GENERATE = "report:generate";
    public static final String REPORT_VIEW = "report:view";
    public static final String REPORT_EXPORT = "report:export";

    // Permissions para libros contables
    public static final String LEDGER_VIEW = "ledger:view";
    public static final String LEDGER_EXPORT = "ledger:export";

    // Permissions para configuración del sistema
    public static final String CONFIG_VIEW = "config:view";
    public static final String CONFIG_EDIT = "config:edit";

    // Permissions para plan de cuentas
    public static final String CHART_OF_ACCOUNTS_VIEW = "coa:view";
    public static final String CHART_OF_ACCOUNTS_EDIT = "coa:edit";
    public static final String CHART_OF_ACCOUNTS_CREATE = "coa:create";
    public static final String CHART_OF_ACCOUNTS_DELETE = "coa:delete";
    public static final String CHART_OF_ACCOUNTS_IMPORT = "coa:import";
    public static final String CHART_OF_ACCOUNTS_EXPORT = "coa:export";

    // Permissions para conciliación bancaria
    public static final String BANK_RECONCILIATION_VIEW = "bankrec:view";
    public static final String BANK_RECONCILIATION_CREATE = "bankrec:create";
    public static final String BANK_RECONCILIATION_EDIT = "bankrec:edit";
    public static final String BANK_RECONCILIATION_APPROVE = "bankrec:approve";

    // Permissions para cierres contables y períodos
    public static final String CLOSING_PROCESS_EXECUTE = "closing:execute";
    public static final String CLOSING_PROCESS_REVERSE = "closing:reverse";
    public static final String CLOSING_PROCESS_VIEW = "closing:view";
    public static final String PERIOD_CLOSE_MONTHLY = "period:close:monthly";
    public static final String PERIOD_CLOSE_ANNUAL = "period:close:annual";
    public static final String PERIOD_REOPEN = "period:reopen";
    public static final String PERIOD_LOCK = "period:lock";
    public static final String PERIOD_UNLOCK = "period:unlock";
    public static final String PERIOD_ADJUSTMENT = "period:adjustment";

    // Permissions para gestión de fechas de procesamiento
    public static final String PROCESSING_DATE_VIEW = "processingdate:view";
    public static final String PROCESSING_DATE_CHANGE = "processingdate:change";
    public static final String PROCESSING_DATE_OVERRIDE = "processingdate:override";
    public static final String PROCESSING_DATE_LOCK = "processingdate:lock";

    // Permissions para cuentas específicas
    public static final String ACCOUNT_VIEW_DETAIL = "account:view:detail";
    public static final String ACCOUNT_EDIT_BALANCE = "account:edit:balance";
    public static final String ACCOUNT_RECLASSIFY = "account:reclassify";
    public static final String ACCOUNT_MERGE = "account:merge";
    public static final String ACCOUNT_INACTIVATE = "account:inactivate";
    public static final String ACCOUNT_ACTIVATE = "account:activate";
    public static final String ACCOUNT_HISTORY_VIEW = "account:history:view";

    // Permissions para asientos de ajuste
    public static final String ADJUSTING_ENTRY_CREATE = "adjusting:create";
    public static final String ADJUSTING_ENTRY_APPROVE = "adjusting:approve";
    public static final String ADJUSTING_ENTRY_REVERSE = "adjusting:reverse";

    // Permissions para auditoría
    public static final String AUDIT_LOG_VIEW = "audit:view";
    public static final String AUDIT_LOG_EXPORT = "audit:export";

    // Rol de administrador
    public static final String ADMIN = "admin";
    public static final String SUPER_ADMIN = "superadmin";

    // Permissions para integraciones
    public static final String INTEGRATION_ACCOUNTING = "integration:accounting";
    public static final String INTEGRATION_BANKING = "integration:banking";
    public static final String INTEGRATION_ERP = "integration:erp";
}
