package group5.cs3750.trainingwheels.programmingobjects;

import java.util.ArrayList;
import java.util.List;

import group5.cs3750.trainingwheels.R;

public class Variable extends ProgrammingObject {
    public static enum VariableType {
        NUMBER, STRING, BOOLEAN
    }

    public static enum Action {
        INSTANTIATE, SET, GET
    }

    private String name;
    private VariableType variableType;
    private Object value;
    private Action action;
    private List<ProgrammingObjectType> allowedChildTypes = new ArrayList(); // The types of programming objects that can be children to this programming object, can be null
    private int drawColor = R.color.button_green;

    public Variable(String name, VariableType variableType, Object value) {
        super(ProgrammingObjectType.VARIABLE);

        this.name = name;
        this.variableType = variableType;
        this.value = value;
    }

    public Variable(String name, VariableType variableType, Object value, ProgrammingObject parent) {
        super(ProgrammingObjectType.VARIABLE, parent);

        this.name = name;
        this.variableType = variableType;
        this.value = value;
    }

    @Override
    public String toString() {
        String type = "";

        switch (variableType) {
            case NUMBER:
                type = "number";
                break;

            case STRING:
                type = "string";
                break;

            case BOOLEAN:
                type = "boolean";
                break;
        }

        return type + " '" + name + "': '" + value + "'";
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

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public ProgrammingObjectType getType() {
        return ProgrammingObjectType.VARIABLE;
    }

    @Override
    public void toScript(StringBuilder stringBuilder) {
        stringBuilder.append("var " + name);

        if(value != null) {
            if (variableType == VariableType.BOOLEAN || variableType == VariableType.NUMBER)
                stringBuilder.append(" = " + value + ";\n");
            else if(variableType == VariableType.STRING)
                stringBuilder.append(" = '" + value + "';\n");
        }
    }
}
