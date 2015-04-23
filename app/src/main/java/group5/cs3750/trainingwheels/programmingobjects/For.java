package group5.cs3750.trainingwheels.programmingobjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import group5.cs3750.trainingwheels.R;

public class For extends ProgrammingObject {
    private static int nextInt = 0;
    private Integer startingValue; // This is an object so that it can be null
    private Variable startingValueVariable; // either startingValue and startingValueVariable will be used
    private Integer endingValue; // This is an object so that it can be null
    private Variable endValueVariable; // either endingValue and endValueVariable will be used
    private String label;
    private ComparisonOperator endingValueComparisonOperator;
    private boolean countUp = true; // true by default, this will be 99% of cases

    public For() { setFields(); }

    public For(String label, ComparisonOperator endingValueComparisonOperator, boolean countUp) {
        super(ProgrammingObjectType.FOR);

        this.label = label;
        this.endingValueComparisonOperator = endingValueComparisonOperator;
        this.countUp = countUp;
        setFields();
    }

    public For(String label, ComparisonOperator endingValueComparisonOperator, boolean countUp, ProgrammingObject parent) {
        super(ProgrammingObjectType.FOR, parent);

        this.label = label;
        this.endingValueComparisonOperator = endingValueComparisonOperator;
        this.countUp = countUp;
        setFields();
    }

    private void setFields() {
        allowedChildTypes = new ArrayList<ProgrammingObjectType>( // The types of programming objects that can be children to this programming object, can be null
                Arrays.asList(
                        ProgrammingObjectType.WHILE, ProgrammingObjectType.IF, ProgrammingObjectType.FOR, ProgrammingObjectType.PRINT,
                        ProgrammingObjectType.FUNCTION, ProgrammingObjectType.VARIABLE)
        );

        drawColor = R.color.button_red;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        String varName = "i";
        stringBuilder.append("for(var ").append(varName);
        stringBuilder.append(" = ");

        if(startingValueVariable != null) {
            stringBuilder.append(startingValueVariable.getName());
        } else if(startingValue != null) {
            stringBuilder.append(startingValue);
        } else {
            stringBuilder.append(0);
        }

        stringBuilder.append("; ");
        stringBuilder.append(varName);
        stringBuilder.append(" ");
        stringBuilder.append(endingValueComparisonOperator.toString());
        stringBuilder.append(" ");

        if(endValueVariable != null) {
            stringBuilder.append(endValueVariable.getName());
        } else if(endingValue != null) {
            stringBuilder.append(endingValue);
        } else {
            stringBuilder.append(0);
        }

        stringBuilder.append("; ");
        stringBuilder.append(varName);

        if(countUp)
            stringBuilder.append("++").append(") {"); // for(var i = 0; i < 10; i++) {
        else
            stringBuilder.append("--").append(") {"); // for(var i = 0; i < 10; i--) {

        for (ProgrammingObject child : children) {
            child.toScript(stringBuilder);
        }

        return stringBuilder.toString();
    }

    @Override
    public void toScript(StringBuilder stringBuilder) {
        String varName = "i" + String.valueOf(For.nextInt++);
        stringBuilder.append("for(var ").append(varName);
        stringBuilder.append(" = ");

        if(startingValueVariable != null) {
            stringBuilder.append(startingValueVariable.getName());
        } else if(startingValue != null) {
            stringBuilder.append(startingValue);
        } else {
            stringBuilder.append(0);
        }

        stringBuilder.append("; ");
        stringBuilder.append(varName);
        stringBuilder.append(" ");
        stringBuilder.append(endingValueComparisonOperator.toString());
        stringBuilder.append(" ");

        if(endValueVariable != null) {
            stringBuilder.append(endValueVariable.getName());
        } else if(endingValue != null) {
            stringBuilder.append(endingValue);
        } else {
            stringBuilder.append(0);
        }

        stringBuilder.append("; ");
        stringBuilder.append(varName);

        if(countUp)
            stringBuilder.append("++").append(") {"); // for(var i = 0; i < 10; i++) {
        else
            stringBuilder.append("--").append(") {"); // for(var i = 0; i < 10; i--) {

        for (ProgrammingObject child : children) {
            child.toScript(stringBuilder);
        }

        stringBuilder.append("}\n");
    }

    @Override
    public List<ProgrammingObjectType> getAllowedChildTypes() {
        return allowedChildTypes;
    }

    @Override
    public String getTypeName() {
        return "For";
    }

    @Override
    public int getDrawColor() {
        return drawColor;
    }

    public Integer getStartingValue() {
        return startingValue;
    }

    public void setStartingValue(int startingValue) {
        this.startingValue = startingValue;
    }

    public Variable getStartingValueVariable() {
        return startingValueVariable;
    }

    public void setStartingValueVariable(Variable startingValueVariable) {
        this.startingValueVariable = startingValueVariable;
    }

    public Integer getEndingValue() {
        return endingValue;
    }

    public void setEndingValue(int endingValue) {
        this.endingValue = endingValue;
    }

    public ComparisonOperator getEndingValueComparisonOperator() {
        return endingValueComparisonOperator;
    }

    public void setEndingValueComparisonOperator(ComparisonOperator endingValueComparisonOperator) {
        this.endingValueComparisonOperator = endingValueComparisonOperator;
    }

    public Variable getEndValueVariable() {
        return endValueVariable;
    }

    public void setEndValueVariable(Variable endValueVariable) {
        this.endValueVariable = endValueVariable;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isCountUp() {
        return countUp;
    }

    public void setCountUp(boolean countUp) {
        this.countUp = countUp;
    }
}
