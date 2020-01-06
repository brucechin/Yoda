package utils;

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

}
