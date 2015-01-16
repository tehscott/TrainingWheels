package group5.cs3750.trainingwheels.programmingobjects;

public class ProgrammingObject {
    public static enum ProgrammingObjectType {
        WHILE, IF, FOR, PRINT, INT, FUNCTION, STRING
    }

    public static enum Placement {
        FIRST, LAST
    }

    public static enum ComparisonOperator {
        EQUAL, DOES_NOT_EQUAL, LESS_THAN, GREATER_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN_OR_EQUAL
    }

    private ProgrammingObjectType type;
    private int position; // position in the list, starting at 0
    private Placement placement; // FIRST or LAST, is this the first or last of the pair
    private ProgrammingObject partner; // ProgrammingObject that makes the other half of the pair

    public ProgrammingObject(ProgrammingObjectType type, int position, Placement placement) {
        this.type = type;
        this.position = position;
        this.placement = placement;
    }

    public ProgrammingObjectType getType() {
        return type;
    }

    public void setType(ProgrammingObjectType type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Placement getPlacement() {
        return placement;
    }

    public void setPlacement(Placement placement) {
        this.placement = placement;
    }

    public ProgrammingObject getPartner() {
        return partner;
    }

    public void setPartner(ProgrammingObject partner) {
        this.partner = partner;
    }
}
