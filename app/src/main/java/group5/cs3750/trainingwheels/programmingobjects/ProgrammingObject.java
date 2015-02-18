package group5.cs3750.trainingwheels.programmingobjects;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import group5.cs3750.trainingwheels.R;

public abstract class ProgrammingObject implements Serializable{
  public static enum ProgrammingObjectType {
    WHILE, IF, FOR, PRINT, INT, FUNCTION, STRING, VARIABLE
  }

  public static enum ComparisonOperator {
    EQUAL("=="),
    DOES_NOT_EQUAL("!="),
    LESS_THAN("<"),
    GREATER_THAN(">"),
    LESS_THAN_OR_EQUAL("<="),
    GREATER_THAN_OR_EQUAL(">=");

    private final String operator;

    ComparisonOperator(String operator) {
      this.operator = operator;
    }

    public static ComparisonOperator fromString(String text) {
      if (text != null) {
        for (ComparisonOperator c : ComparisonOperator.values()) {
          if (text.equalsIgnoreCase(c.operator)) {
            return c;
          }
        }
      }
      throw new IllegalArgumentException("No constant with text " + text + " found");
    }

    @Override
    public String toString() {
      return operator;
    }
  }

  private ProgrammingObjectType type;
  private int listPosition = -1; // position in the main list, starting at 0, -1 means unassigned
  private int positionUnderParent = -1; // position underneath its parent (if it has a parent, -1 otherwise)
  private ProgrammingObject parent; // Can be null, meaning this IS the parent
  protected List<ProgrammingObjectType> allowedChildTypes; // The types of programming objects that can be children to this programming object, can be null
  protected ArrayList<ProgrammingObject> children = new ArrayList<ProgrammingObject>(); // Children of this programming object, cannot be null, but can be of 0 length
  private Drawable buttonDrawable; // This should be a copy of the drawable assigned to the button corresponding to this object

  // Drawing vars
  private Rect currentDrawnLocation; // Location of the object drawn on the canvas. Can be null, but probably never will be
  protected int drawColor = R.color.button_green; // Color to draw the object. The getter for this should be overridden by sub classes

  public ProgrammingObject() {

  }

  public ProgrammingObject(ProgrammingObjectType type, int listPosition) {
    this.type = type;
    this.listPosition = listPosition;
  }

  public ProgrammingObject(ProgrammingObjectType type, int listPosition, int positionUnderParent, ProgrammingObject parent) {
    this.type = type;
    this.listPosition = listPosition;
    this.positionUnderParent = positionUnderParent;
    this.parent = parent;
  }

  public ProgrammingObjectType getType() {
    return type;
  }

  public void setType(ProgrammingObjectType type) {
    this.type = type;
  }

  public int getListPosition() {
    return listPosition;
  }

  public void setListPosition(int listPosition) {
    this.listPosition = listPosition;
  }

  public int getPositionUnderParent() {
    return positionUnderParent;
  }

  public void setPositionUnderParent(int positionUnderParent) {
    this.positionUnderParent = positionUnderParent;
  }

  public ProgrammingObject getParent() {
    return parent;
  }

  public void setParent(ProgrammingObject parent) {
    this.parent = parent;
  }

  // This method should be overridden by a subclass that is a parent
  public List<ProgrammingObjectType> getAllowedChildTypes() {
    return allowedChildTypes;
  }

  public ArrayList<ProgrammingObject> getChildren() {
    return children;
  }

  public void setChildren(ArrayList<ProgrammingObject> children) {
    this.children = children;
  }

  public Rect getCurrentDrawnLocation() {
    return currentDrawnLocation;
  }

  public void setCurrentDrawnLocation(Rect currentDrawnLocation) {
    this.currentDrawnLocation = currentDrawnLocation;
  }

  public int getDrawColor() {
    return drawColor;
  }

  public void setDrawColor(int drawColor) {
    this.drawColor = drawColor;
  }

  /**
   * Adds a child ProgrammingObject to the end of the list of children
   * This is just a synonym for appendChild
   *
   * @param child the child ProgrammingObject to add.
   */
  public void addChild(ProgrammingObject child) {
    appendChild(child);
  }

  /**
   * Adds a child ProgrammingObject to the end of the list of children
   *
   * @param child the child ProgrammingObject to add.
   */
  public void appendChild(ProgrammingObject child) {
    children.add(child);
  }

  /**
   * Inserts a child at the position specified and pushes whatever is currently
   * in that position down (as well as everything after that position)
   *
   * @param index the index at which to insert the object.
   * @param child the child ProgrammingObject to insert.
   */
  public void insertChild(int index, ProgrammingObject child) {
    children.add(index, child);
  }

  public String getTypeName() {
    return "ERROR: Unimplemented for this object type.";
  }

  public Drawable getButtonDrawable() {
    return buttonDrawable;
  }

  public void setButtonDrawable(Drawable buttonDrawable) {
    this.buttonDrawable = buttonDrawable;
  }

  public void toScript(StringBuilder stringBuilder) {

  }
}