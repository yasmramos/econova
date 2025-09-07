package com.univsoftdev.econova;

import com.univsoftdev.econova.config.model.Role;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.config.service.ConfigService;
import com.univsoftdev.econova.config.service.CurrencyService;
import com.univsoftdev.econova.config.service.PermissionService;
import com.univsoftdev.econova.config.service.RoleService;
import com.univsoftdev.econova.config.service.UserService;
import com.univsoftdev.econova.contabilidad.AccountType;
import com.univsoftdev.econova.contabilidad.NatureOfAccount;
import com.univsoftdev.econova.contabilidad.model.Account;
import com.univsoftdev.econova.contabilidad.model.ChartOfAccounts;
import com.univsoftdev.econova.contabilidad.model.Permission;
import com.univsoftdev.econova.contabilidad.service.AccountingService;
import com.univsoftdev.econova.core.Injector;
import com.univsoftdev.econova.security.Permissions;
import com.univsoftdev.econova.security.Roles;
import java.util.List;
import java.util.Set;

public class TestData {

    public static void init() {
        UserService userService = Injector.get(UserService.class);
        List<User> allUsers = userService.findAll();

        if (allUsers.isEmpty()) {
            try {
                initializeTestData();
            } catch (Exception e) {
                System.err.println("Error initializing test data: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void initializeTestData() {
        // Get services
        ConfigService configService = Injector.get(ConfigService.class);
        RoleService roleService = Injector.get(RoleService.class);
        PermissionService permissionService = Injector.get(PermissionService.class);
        AccountingService accountingService = Injector.get(AccountingService.class);
        CurrencyService currencyService = Injector.get(CurrencyService.class);
        UserService userService = Injector.get(UserService.class);
      

        // 1. First create permissions
        Permission createUserPerm = createPermissionIfNotExists(permissionService,
                Permissions.CREATE_USER, "Create Users");

        Permission deleteUserPerm = createPermissionIfNotExists(permissionService,
                Permissions.DELETE_USER, "Delete Users");

        Permission viewUserPerm = createPermissionIfNotExists(permissionService,
                Permissions.VIEW_USER, "View Users");

        Permission editUserPerm = createPermissionIfNotExists(permissionService,
                Permissions.EDIT_USER, "Edit Users");

        Permission adminAccessPerm = createPermissionIfNotExists(permissionService,
                Permissions.ADMIN_ACCESS, "Admin Access");

        // 2. Create roles with permissions
        Role adminRole = createRoleWithPermissions(roleService,
                Roles.SYSTEM_ADMIN,
                "System Administrator",
                Set.of(Permissions.CREATE_USER, Permissions.DELETE_USER, Permissions.VIEW_USER,
                        Permissions.EDIT_USER, Permissions.ADMIN_ACCESS));

        Role userRole = createRoleWithPermissions(roleService,
                Roles.USER,
                "Regular User",
                Set.of(Permissions.VIEW_USER));

        // 3. Create admin user
        User adminUser = configService.createUser(
                "admin",
                "admin",
                "admin@example.com",
                "admin123".toCharArray()
        );

        // Assign admin role to admin user
        if (adminUser != null) {
            userService.assignRoleToUser(adminUser.getId(), adminRole.getId());
        }
        
        

        // 4. Initialize accounting data
        initializeAccountingData(accountingService, currencyService);

        System.out.println("Test data initialized successfully!");
    }

    private static Permission createPermissionIfNotExists(PermissionService service,
            String code, String name) {
        return service.getPermissionByCode(code)
                .orElseGet(() -> {
                    Permission permission = new Permission(code, name);
                    service.save(permission);
                    System.out.println("Created permission: " + code);
                    return permission;
                });
    }

    private static Role createRoleWithPermissions(RoleService roleService,
            String roleCode, String roleName,
            Set<String> permissionCodes) {
        // Check if role already exists
        return roleService.getRoleByName(roleCode)
                .orElseGet(() -> {
                    // Create the role
                    Role role = roleService.createRole(roleCode, roleName, null);

                    // Assign permissions
                    for (String permCode : permissionCodes) {
                        try {
                            // This assumes your roleService has a method to assign permission by code
                            roleService.assignPermissionByCode(role.getId(), permCode);
                        } catch (Exception e) {
                            System.err.println("Failed to assign permission " + permCode + " to role " + roleCode);
                        }
                    }

                    System.out.println("Created role: " + roleCode);
                    return role;
                });
    }

    private static void initializeAccountingData(AccountingService accountingService,
            CurrencyService currencyService) {
        try {
            // Create currency
            var currency = currencyService.createCurrency("CUP", "Cuban Peso", true);

            // Create chart of accounts
            ChartOfAccounts chartOfAccounts = accountingService.createChartOfAccounts(
                    1L, "Main Chart of Accounts");

            // Create sample accounts
            Account account100 = accountingService.createAccount(
                    "100",
                    "Cash Account",
                    NatureOfAccount.DEBTOR,
                    AccountType.ASSET,
                    currency,
                    chartOfAccounts
            );

            Account account100_001 = accountingService.createAccount(
                    "100.001",
                    "Petty Cash",
                    NatureOfAccount.DEBTOR,
                    AccountType.ASSET,
                    currency,
                    chartOfAccounts
            );
            
            accountingService.addSubAccount(account100, account100_001);

            System.out.println("Accounting test data created successfully");

        } catch (Exception e) {
            System.err.println("Failed to create accounting test data: " + e.getMessage());
        }
    }
}
