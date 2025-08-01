package com.univsoftdev.econova.bsondb;

public class DatabaseInfo {

    private final String filePath;
    private final long fileSize;
    private final int collectionCount;
    private final int totalItems;

    public DatabaseInfo(String filePath, long fileSize, int collectionCount, int totalItems) {
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.collectionCount = collectionCount;
        this.totalItems = totalItems;
    }

    public String getFilePath() {
        return filePath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public int getCollectionCount() {
        return collectionCount;
    }

    public int getTotalItems() {
        return totalItems;
    }

}
