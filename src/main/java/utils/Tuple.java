package utils;

import java.util.List;

public class Tuple {
    List<Field> fields_;
    //TODO add schema for fields

    public Tuple() {

    }

    public void getSchema() {
        //TODO
    }

    public void addField(Field f) {

    }

    public Field getField(int offset) {
        return fields_.get(offset);
    }

    public List<Field> getFields() {
        return fields_;
    }
}
