package utils;

import java.util.HashMap;

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
