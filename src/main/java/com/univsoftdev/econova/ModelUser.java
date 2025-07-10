package com.univsoftdev.econova;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModelUser {

    private String userName;
    private String mail;
    private Role role;

    public enum Role {

        ADMIN, STAFF;

        @Override
        public String toString() {
            if (this == ADMIN) {
                return "Admin";
            }
            return "Staff";
        }
    }
}
