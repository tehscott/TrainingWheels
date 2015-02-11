package group5.cs3750.trainingwheels.programmingobjects;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

/**
 * Created by Jeff on 07-Feb-15.
 */
public class Print extends ProgrammingObject {

  private String text;

  public Print(String string) {

    text = string;
    setButtonDrawable(new ColorDrawable(Color.YELLOW));
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
