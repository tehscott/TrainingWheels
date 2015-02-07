package group5.cs3750.trainingwheels.programmingobjects;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import group5.cs3750.trainingwheels.R;

public class For extends ProgrammingObject {
    private int startingValue;
    private int endingValue;
    private ComparisonOperator endingValueComparisonOperator;
    private List<ProgrammingObjectType> allowedChildTypes = new ArrayList( // The types of programming objects that can be children to this programming object, can be null
            Arrays.asList(
                    ProgrammingObjectType.WHILE, ProgrammingObjectType.IF, ProgrammingObjectType.FOR, ProgrammingObjectType.PRINT,
                    ProgrammingObjectType.INT, ProgrammingObjectType.FUNCTION, ProgrammingObjectType.STRING)
    );
    private int drawColor = R.color.button_red;

    public For(int position, int startingValue, int endingValue, ComparisonOperator endingValueComparisonOperator) {
        super(ProgrammingObjectType.FOR, position);

        this.startingValue = startingValue;
        this.endingValue = endingValue;
        this.endingValueComparisonOperator = endingValueComparisonOperator;
    }

    public For(int position, int startingValue, int endingValue, ComparisonOperator endingValueComparisonOperator, int positionUnderParent, ProgrammingObject parent) {
        super(ProgrammingObjectType.FOR, position, positionUnderParent, parent);

        this.startingValue = startingValue;
        this.endingValue = endingValue;
        this.endingValueComparisonOperator = endingValueComparisonOperator;
    }

    @Override
    public String toString() {
        return "Loop from " + startingValue + " to " + endingValue;
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
}
