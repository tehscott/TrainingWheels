package group5.cs3750.trainingwheels.programmingobjects;

import java.util.ArrayList;
import java.util.List;

import group5.cs3750.trainingwheels.R;

/**
 * Created by Jeff on 07-Feb-15.
 */
public class Print extends ProgrammingObject {
    private String text;
    private List<ProgrammingObjectType> allowedChildTypes = new ArrayList(); // The types of programming objects that can be children to this programming object, can be null

    public Print() { setFields(); }

    public Print(String string) {
        super(ProgrammingObjectType.PRINT);
        text = string;
        setFields();
    }

    public Print(ProgrammingObject parent) {
        super(ProgrammingObjectType.PRINT, parent);
        setFields();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private void setFields() {
        allowedChildTypes = new ArrayList(); // The types of programming objects that can be children to this programming object, can be null

        drawColor = R.color.button_purple;
    }

    @Override
    public void toScript(StringBuilder stringBuilder) {
        stringBuilder.append("document.getElementById(\"{field}\").innerHTML += \"");
        stringBuilder.append(text + "<br>");
        stringBuilder.append("\";\n");
    }

    @Override
    public String toString() {
        return "'" + text + "'";
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
