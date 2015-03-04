package group5.cs3750.trainingwheels.programmingobjects;

import java.util.ArrayList;
import java.util.List;

import group5.cs3750.trainingwheels.R;

/**
 * Created by Jeff on 07-Feb-15.
 */
public class Print extends ProgrammingObject {
    public static enum PrintType {
        TEXT, VARIABLE
    }

    private PrintType printType = PrintType.TEXT;
    private String text;
    private Variable variable;
    private List<ProgrammingObjectType> allowedChildTypes = new ArrayList(); // The types of programming objects that can be children to this programming object, can be null

    public Print() { setFields(); }

    public Print(String text) {
        super(ProgrammingObjectType.PRINT);

        this.printType = PrintType.TEXT;
        this.text = text;
        setFields();
    }

    public Print(Variable variable) {
        super(ProgrammingObjectType.PRINT);

        this.printType = PrintType.VARIABLE;
        this.variable = variable;
        setFields();
    }

    public Print(String text, ProgrammingObject parent) {
        super(ProgrammingObjectType.PRINT, parent);

        this.printType = PrintType.TEXT;
        this.text = text;
        setFields();
    }

    public Print(Variable variable, ProgrammingObject parent) {
        super(ProgrammingObjectType.PRINT, parent);

        this.printType = PrintType.VARIABLE;
        this.variable = variable;
        setFields();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PrintType getPrintType() {
        return printType;
    }

    public void setPrintType(PrintType printType) {
        this.printType = printType;
    }

    public Variable getVariable() {
        return variable;
    }

    public void setVariable(Variable variable) {
        this.variable = variable;
    }

    private void setFields() {
        allowedChildTypes = new ArrayList(); // The types of programming objects that can be children to this programming object, can be null

        drawColor = R.color.button_purple;
    }

    @Override
    public void toScript(StringBuilder stringBuilder) {
        if(printType == PrintType.TEXT) {
            stringBuilder.append("document.getElementById(\"{field}\").innerHTML += \"" + text + "<br>\";\n");
        } else if(printType == PrintType.VARIABLE) {
            stringBuilder.append("document.getElementById(\"{field}\").innerHTML += " + variable.getName() + "+ \"<br>\";\n");
        }
    }

    @Override
    public String toString() {
        if(printType == PrintType.TEXT) {
            return "print '" + text + "'";
        } else if(printType == PrintType.VARIABLE) {
            return "print var '" + variable.getName() + "'";
        }

        return "";
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
