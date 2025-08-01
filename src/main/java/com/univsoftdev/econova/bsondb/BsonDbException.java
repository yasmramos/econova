package com.univsoftdev.econova.bsondb;

public class BsonDbException extends RuntimeException {

    public BsonDbException(String message) {
        super(message);
    }

    public BsonDbException(String message, Throwable cause) {
        super(message, cause);
    }
}
