package com.univsoftdev.econova.bsondb;

public class ObjectNotFoundException extends BsonDbException {
    public ObjectNotFoundException(Object id) {
        super("Object with ID " + id + " not found");
    }
}