package com.univsoftdev.econova.bsondb;

public class DuplicateIdException extends BsonDbException {
    public DuplicateIdException(Object id) {
        super("Duplicate ID found: " + id);
    }
}