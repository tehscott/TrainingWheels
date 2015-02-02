package group5.cs3750.trainingwheels.programmingobjects;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Variable extends ProgrammingObject {
    public static enum VariableType {
        NUMBER, STRING, BOOLEAN
    }

    private String name;
    private VariableType variableType;
    private Object value;
    private List<ProgrammingObjectType> allowedChildTypes = new ArrayList(); // The types of programming objects that can be children to this programming object, can be null
    private int drawColor = Color.RED;

    public Variable(int listPosition, String name, VariableType variableType, Object value) {
        super(ProgrammingObjectType.VARIABLE, listPosition);

        this.name = name;
        this.variableType = variableType;
        this.value = value;
    }

    public Variable(int listPosition, String name, VariableType variableType, Object value, int positionUnderParent, ProgrammingObject parent) {
        super(ProgrammingObjectType.VARIABLE, listPosition, positionUnderParent, parent);

        this.name = name;
        this.variableType = variableType;
        this.value = value;
    }

    @Override
    public String toString() {
        String type = "";

        switch (variableType) {
            case NUMBER:
                type = "Number";
                break;

            case STRING:
                type = "String";
                break;

            case BOOLEAN:
                type = "Boolean";
                break;
        }

        return type + " variable '" + name + "': " + value;
    }

    @Override
    public List<ProgrammingObjectType> getAllowedChildTypes() {
        return allowedChildTypes;
    }

    @Override
    public String getTypeName() {
        return "Variable";
    }

    @Override
    public int getDrawColor() {
        return drawColor;
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public void setVariableType(VariableType variableType) {
        this.variableType = variableType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
