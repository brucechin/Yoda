package utils;

import execution.LogRecord;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;

enum RWType{
    INVALID,
    READ,
    READ_OWN,  // select for update
    UPDATE,
    INSERT,
    DELETE,
    INS_DEL,  // delete after insert.
}

public class ReadWriteSet {
    //just use the target table name and tuple offset for validation
    HashMap<Integer, LogRecord> epochRWSet;


}
