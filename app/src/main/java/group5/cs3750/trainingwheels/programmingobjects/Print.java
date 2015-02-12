package group5.cs3750.trainingwheels.programmingobjects;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import java.util.ArrayList;
import java.util.List;

import group5.cs3750.trainingwheels.R;

/**
 * Created by Jeff on 07-Feb-15.
 */
public class Print extends ProgrammingObject {
    private String text;
    private List<ProgrammingObjectType> allowedChildTypes = new ArrayList(); // The types of programming objects that can be children to this programming object, can be null
    private int drawColor = R.color.button_purple;

    public Print(String string) {
        super(ProgrammingObjectType.PRINT, 0);
        text = string;
        //setButtonDrawable(new ColorDrawable(Color.YELLOW));
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
        stringBuilder.append(text + "<br>");
        stringBuilder.append("\";\n");
    }

    @Override
    public String toString() {
        return "Print";
    }

    @Override
    public List<ProgrammingObjectType> getAllowedChildTypes() {
        return allowedChildTypes;
    }

    @Override
    public String getTypeName() {
        return "Print";
    }

    @Override
    public int getDrawColor() {
        return drawColor;
    }
}
