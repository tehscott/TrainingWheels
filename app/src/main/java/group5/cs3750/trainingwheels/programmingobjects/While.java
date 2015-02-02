package group5.cs3750.trainingwheels.programmingobjects;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class While extends ProgrammingObject {
    private Variable conditionVariable; // The variable to check when looping
    private Object terminationValue; // The value the variable should be to terminate the loop
    private List<ProgrammingObjectType> allowedChildTypes = new ArrayList( // The types of programming objects that can be children to this programming object, can be null
            Arrays.asList(
                    ProgrammingObjectType.WHILE, ProgrammingObjectType.IF, ProgrammingObjectType.FOR, ProgrammingObjectType.PRINT,
                    ProgrammingObjectType.INT, ProgrammingObjectType.FUNCTION, ProgrammingObjectType.STRING)
    );
    private int drawColor = Color.RED;

    public While(int listPosition, Variable conditionVariable, Object terminationValue) {
        super(ProgrammingObjectType.WHILE, listPosition);

        this.conditionVariable = conditionVariable;
        this.terminationValue = terminationValue;
    }

    public While(int listPosition, Variable conditionVariable, Object terminationValue, int positionUnderParent, ProgrammingObject parent) {
        super(ProgrammingObjectType.WHILE, listPosition, positionUnderParent, parent);

        this.conditionVariable = conditionVariable;
        this.terminationValue = terminationValue;
    }

    @Override
    public String toString() {
        return "Loop until '" + conditionVariable.getName() + "' equals '" + terminationValue;
    }

    @Override
    public List<ProgrammingObjectType> getAllowedChildTypes() {
        return allowedChildTypes;
    }

    @Override
    public String getTypeName() {
        return "While";
    }

    @Override
    public int getDrawColor() {
        return drawColor;
    }

    public Variable getConditionVariable() {
        return conditionVariable;
    }

    public void setConditionVariable(Variable conditionVariable) {
        this.conditionVariable = conditionVariable;
    }

    public Object getTerminationValue() {
        return terminationValue;
    }

    public void setTerminationValue(Object terminationValue) {
        this.terminationValue = terminationValue;
    }
}
