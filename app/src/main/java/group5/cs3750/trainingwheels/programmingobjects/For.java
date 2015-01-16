package group5.cs3750.trainingwheels.programmingobjects;

public class For extends ProgrammingObject {
    private int startingValue;
    private int endingValue;
    private ComparisonOperator endingValueComparisonOperator;

    public For(int position, Placement placement, int startingValue, int endingValue, ComparisonOperator endingValueComparisonOperator) {
        super(ProgrammingObjectType.FOR, position, placement);

        this.startingValue = startingValue;
        this.endingValue = endingValue;
        this.endingValueComparisonOperator = endingValueComparisonOperator;
    }

    @Override
    public String toString() {
        return "Loop from " + startingValue + " to " + endingValue;
    }
}
