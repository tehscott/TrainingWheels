package group5.cs3750.trainingwheels.programmingobjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import group5.cs3750.trainingwheels.R;

public class While extends ProgrammingObject {
    private Variable conditionVariable; // The variable to check when looping
    private Variable terminatingVariable;
    private Object terminationValue; // The value the variable should be to terminate the loop
    // TODO: Needs a ComparisonOperator
    private List<ProgrammingObjectType> allowedChildTypes = new ArrayList( // The types of programming objects that can be children to this programming object, can be null
            Arrays.asList(
                    ProgrammingObjectType.WHILE, ProgrammingObjectType.IF, ProgrammingObjectType.FOR, ProgrammingObjectType.PRINT,
                    ProgrammingObjectType.FUNCTION, ProgrammingObjectType.VARIABLE)
    );
    private int drawColor = R.color.button_orange;
    public While(){}
    public While(Variable conditionVariable, Object terminationValue) {
        super(ProgrammingObjectType.WHILE);

        this.conditionVariable = conditionVariable;
        this.terminationValue = terminationValue;
    }

    public While(Variable conditionVariable, Object terminationValue, ProgrammingObject parent) {
        super(ProgrammingObjectType.WHILE, parent);

        this.conditionVariable = conditionVariable;
        this.terminationValue = terminationValue;
    }
    public While(Variable conditionVariable, Variable terminatingVariable) {
        super(ProgrammingObjectType.WHILE);

        this.conditionVariable = conditionVariable;
        this.terminatingVariable = terminatingVariable;
    }

    @Override
    public String toString() {
        return "until '" + conditionVariable.getName() + "' = '" + terminationValue;
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

    public Variable getTerminatingVariable() {
        return terminatingVariable;
    }

    public void setTerminatingVariable(Variable terminatingVariable) {
        this.terminatingVariable = terminatingVariable;
    }

    @Override
    public void toScript(StringBuilder stringBuilder) {

//    stringBuilder.append("var ").append(conditionVariable.getName()).append(" = true;");
//    stringBuilder.append("var ").append(terminatingVariable.getName()).append(" = false;");

        stringBuilder.append("while(");
        stringBuilder.append(conditionVariable.getName());
        stringBuilder.append("){");
        for (ProgrammingObject child : children) {
            child.toScript(stringBuilder);
        }
        stringBuilder.append(conditionVariable.getName());
        stringBuilder.append(" = ");
        stringBuilder.append(terminatingVariable.getName()).append(";");
        stringBuilder.append("}");



    }
}
