package group5.cs3750.trainingwheels.programmingobjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import group5.cs3750.trainingwheels.R;

public class If extends ProgrammingObject {
    private Variable conditionLeftSide; // Left side of the conditional statement, always a variable
    private Object conditionRightSide; // Right side of the conditional statement, can be a manually-entered value or a Variable
    private Variable.VariableType conditionRightSideType;
    private ComparisonOperator comparisonOperator;
    private String expression;

    public If(Variable conditionLeftSide, Object conditionRightSide, Variable.VariableType conditionRightSideType, ComparisonOperator comparisonOperator) {
        super(ProgrammingObjectType.IF);

        this.conditionLeftSide = conditionLeftSide;
        this.conditionRightSide = conditionRightSide;
        this.conditionRightSideType = conditionRightSideType;
        this.comparisonOperator = comparisonOperator;

        setFields();
    }

    public If(Variable conditionLeftSide, Object conditionRightSide, Variable.VariableType conditionRightSideType, ComparisonOperator comparisonOperator, ProgrammingObject parent) {
        super(ProgrammingObjectType.IF, parent);

        this.conditionLeftSide = conditionLeftSide;
        this.conditionRightSide = conditionRightSide;
        this.conditionRightSideType = conditionRightSideType;
        this.comparisonOperator = comparisonOperator;

        setFields();
    }

    private void setFields() {
        allowedChildTypes = new ArrayList<ProgrammingObjectType>( // The types of programming objects that can be children to this programming object, can be null
                Arrays.asList(
                        ProgrammingObjectType.WHILE, ProgrammingObjectType.IF, ProgrammingObjectType.FOR, ProgrammingObjectType.PRINT,
                        ProgrammingObjectType.INT, ProgrammingObjectType.FUNCTION, ProgrammingObjectType.STRING)
        );

        drawColor = R.color.button_blue;
        //addChild(new Print("Test <br>"));
    }

    @Override
    public String toString() {
        return "If ...";
    }

    @Override
    public List<ProgrammingObjectType> getAllowedChildTypes() {
        return allowedChildTypes;
    }

    @Override
    public String getTypeName() {
        return "If";
    }

    @Override
    public int getDrawColor() {
        return drawColor;
    }

    public Variable getConditionLeftSide() {
        return conditionLeftSide;
    }

    public void setConditionLeftSide(Variable conditionLeftSide) {
        this.conditionLeftSide = conditionLeftSide;
    }

    public Object getConditionRightSide() {
        return conditionRightSide;
    }

    public void setConditionRightSide(Object conditionRightSide) {
        this.conditionRightSide = conditionRightSide;
    }

    public Variable.VariableType getConditionRightSideType() {
        return conditionRightSideType;
    }

    public void setConditionRightSideType(Variable.VariableType conditionRightSideType) {
        this.conditionRightSideType = conditionRightSideType;
    }

    public ComparisonOperator getComparisonOperator() {
        return comparisonOperator;
    }

    public void setComparisonOperator(ComparisonOperator comparisonOperator) {
        this.comparisonOperator = comparisonOperator;
    }

    @Override
    public void toScript(StringBuilder stringBuilder) {
        stringBuilder.append("if(" + getExpression() + ") {\n");

        for (ProgrammingObject child : children) {
            child.toScript(stringBuilder);
        }

        stringBuilder.append("}\n");
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }
}
