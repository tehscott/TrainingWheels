package group5.cs3750.trainingwheels.programmingobjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import group5.cs3750.trainingwheels.R;

public class For extends ProgrammingObject {
    private static int nextInt = 0;
    private int startingValue;
    private int endingValue;
    private ComparisonOperator endingValueComparisonOperator;

    public For(int startingValue, int endingValue, ComparisonOperator endingValueComparisonOperator) {
        super(ProgrammingObjectType.FOR);

        this.startingValue = startingValue;
        this.endingValue = endingValue;
        this.endingValueComparisonOperator = endingValueComparisonOperator;
        setFields();
    }

    public For(int startingValue, int endingValue, ComparisonOperator endingValueComparisonOperator, ProgrammingObject parent) {
        super(ProgrammingObjectType.FOR, parent);

        this.startingValue = startingValue;
        this.endingValue = endingValue;
        this.endingValueComparisonOperator = endingValueComparisonOperator;
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
        return startingValue + " to " + endingValue + ", " + getEndingValueComparisonOperator().toString();
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

    public int getStartingValue() {
        return startingValue;
    }

    public void setStartingValue(int startingValue) {
        this.startingValue = startingValue;
    }

    public int getEndingValue() {
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



    @Override
    public void toScript(StringBuilder stringBuilder) {
        Boolean down = false;
        if(getEndingValueComparisonOperator().toString().contentEquals("<")){
            down = true;
        }
        String varName = "i" + String.valueOf(For.nextInt++);
        stringBuilder.append("for(var ").append(varName).append(" = ").append(startingValue).append("; ")
                .append(varName).append(" ").append(endingValueComparisonOperator.toString()).append(endingValue).append("; ")
                .append(varName);
        if(getEndingValueComparisonOperator().toString().contentEquals("<")){
            stringBuilder.append("++").append("){");
        }else{
            stringBuilder.append("--").append("){");;
        }

        for (ProgrammingObject child : children) {
            child.toScript(stringBuilder);
        }

        stringBuilder.append("}\n");
    }
}
