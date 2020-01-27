package org.yoda.util;

import org.yoda.type.LogRecordId;

enum LogRecordType {
    READ, WRITE
}

public class LogRecord {
    //TODO we can use unsigned int instead
    long epochId_;
    LogRecordId logRecordId_;
    int tableId;
    int tuplePos_; // log data
    LogRecordType logRecordType_;

    LogRecord() {

    }

    void getLogRecordId(LogRecordId lid) {
        logRecordId_ = lid;
    }

    long getEpochId() {
        return epochId_;
    }

    void setEpochId(long eid) {
        epochId_ = eid;
    }

    LogRecordId getLogRecordId() {
        return logRecordId_;
    }

    LogRecordType getType() {
        return logRecordType_;
    }
}
