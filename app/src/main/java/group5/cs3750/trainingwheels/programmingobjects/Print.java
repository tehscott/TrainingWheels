package group5.cs3750.trainingwheels.programmingobjects;

import android.graphics.Color;

/**
 * Created by Jeff on 07-Feb-15.
 */
public class Print extends ProgrammingObject {

  private String text;

  public Print(String string) {

    text = string;
    setDrawColor(Color.YELLOW);
  }

  public Print(int listPosition) {
    super(ProgrammingObjectType.PRINT, listPosition);
  }

  public Print(int listPosition, int positionUnderParent, ProgrammingObject parent) {
    super(ProgrammingObjectType.PRINT, listPosition, positionUnderParent, parent);
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public void toScript(StringBuilder stringBuilder) {
    stringBuilder.append("document.getElementById(\"{field}\").innerHTML += \"");
    stringBuilder.append(text);
    stringBuilder.append("\";\n");
  }
}
