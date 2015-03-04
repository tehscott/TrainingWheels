package group5.cs3750.trainingwheels.programmingobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import group5.cs3750.trainingwheels.R;

public class Variable extends ProgrammingObject {
    public static enum VariableType implements Serializable {
        NUMBER, STRING, BOOLEAN
    }

    public static enum VariableActionType implements Serializable {
        CREATE, SET, INCREMENT
    }

    private String name;
    private VariableType variableType = VariableType.STRING;
    private VariableActionType variableActionType = VariableActionType.CREATE;
    private Object value;
    private double incrementValue = 0;

    public Variable() { setFields(); }

    public Variable(String name, VariableType variableType, VariableActionType variableActionType, Object value) {
        super(ProgrammingObjectType.VARIABLE);

        this.name = name;
        this.variableType = variableType;
        this.variableActionType = variableActionType;
        this.value = value;

        setFields();
    }

    public Variable(String name, VariableType variableType, VariableActionType variableActionType, Object value, ProgrammingObject parent) {
        super(ProgrammingObjectType.VARIABLE, parent);

        this.name = name;
        this.variableType = variableType;
        this.variableActionType = variableActionType;
        this.value = value;

        setFields();
    }

    private void setFields() {
        allowedChildTypes = new ArrayList(); // The types of programming objects that can be children to this programming object, can be null

        drawColor = R.color.button_green;
    }

    @Override
    public String toString() {
        String text = "";

        if(variableActionType == VariableActionType.CREATE) {
            text = "create ";

            switch (variableType) {
                case NUMBER:
                    text += "number";
                    break;

                case STRING:
                    text += "string";
                    break;

                case BOOLEAN:
                    text += "boolean";
                    break;
            }

            text += " '" + name + "': '" + value + "'";
        } else if(variableActionType == VariableActionType.SET) {
            text = "set '" + name + "': '" + value + "'";
        } else if(variableActionType == VariableActionType.INCREMENT) {
            text = "increment '" + name + "' " + (((Boolean) value) ? "up" : "down") + " by " + incrementValue;
        }

        return text;
    }

    @Override
    public void toScript(StringBuilder stringBuilder) {
        if(variableActionType == VariableActionType.CREATE) {
            stringBuilder.append("var " + name);

            if(value != null) {
                if (variableType == VariableType.BOOLEAN || variableType == VariableType.NUMBER)
                    stringBuilder.append(" = " + value + ";\n");
                else if(variableType == VariableType.STRING)
                    stringBuilder.append(" = '" + value + "';\n");
            }
        } else if(variableActionType == VariableActionType.SET) {
            stringBuilder.append(name);

            if(value != null) {
                if (variableType == VariableType.BOOLEAN || variableType == VariableType.NUMBER)
                    stringBuilder.append(" = " + value + ";\n");
                else if(variableType == VariableType.STRING)
                    stringBuilder.append(" = '" + value + "';\n");
            }
        } else if(variableActionType == VariableActionType.INCREMENT) {
            stringBuilder.append(name);

            if ((Boolean) value) // increment up
                stringBuilder.append(" += " + incrementValue + ";\n");
            else if(variableType == VariableType.STRING)
                stringBuilder.append(" -= '" + incrementValue + "';\n");
        }
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

    public VariableActionType getVariableActionType() {
        return variableActionType;
    }

    public void setVariableActionType(VariableActionType variableActionType) {
        this.variableActionType = variableActionType;
    }

    public double getIncrementValue() {
        return incrementValue;
    }

    public void setIncrementValue(double incrementValue) {
        this.incrementValue = incrementValue;
    }

    @Override
    public ProgrammingObjectType getType() {
        return ProgrammingObjectType.VARIABLE;
    }
}
