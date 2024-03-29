package group5.cs3750.trainingwheels.programmingobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import group5.cs3750.trainingwheels.R;

public class While extends ProgrammingObject {
    public static enum WhileConditionType implements Serializable {
        TRUE, VARIABLE, CUSTOM_EXPRESSION
    }

    public static enum WhileTerminatingValueType implements Serializable {
        TRUE, FALSE, VARIABLE, CUSTOM_VALUE
    }

    private WhileConditionType conditionType = WhileConditionType.VARIABLE; // default to variable
    private Variable conditionVariable;
    private String customConditionExpression;
    private ComparisonOperator comparisonOperator = ComparisonOperator.EQUAL; // default to equal
    private WhileTerminatingValueType terminatingValueType = WhileTerminatingValueType.TRUE; // default to true
    private Variable terminatingVariable;
    private String customTerminatingValue;





    public While() { setFields(); }

    // Constructor for the TRUE condition type
    public While(WhileConditionType conditionType) {
        super(ProgrammingObjectType.WHILE);

        this.conditionType = conditionType;
        setFields();
    }

    // Constructor for the CUSTOM_EXPRESSION condition type
    public While(WhileConditionType conditionType, String customConditionExpression) {
        super(ProgrammingObjectType.WHILE);

        this.conditionType = conditionType;
        this.customConditionExpression = customConditionExpression;
        setFields();
    }

    // Constructor for the VARIABLE condition type
    public While(WhileConditionType conditionType, Variable conditionVariable, ComparisonOperator comparisonOperator,
                 WhileTerminatingValueType terminatingValueType, Variable terminatingVariable, String customTerminatingValue) {
        super(ProgrammingObjectType.WHILE);

        this.conditionType = conditionType;
        this.conditionVariable = conditionVariable;
        this.comparisonOperator = comparisonOperator;
        this.terminatingValueType = terminatingValueType;
        this.terminatingVariable = terminatingVariable;
        this.customTerminatingValue = customTerminatingValue;
        setFields();
    }

    //Jamie messing around with constructors
    public While(Variable var1, ComparisonOperator cOperator){}
    private void setFields() {
        allowedChildTypes = new ArrayList( // The types of programming objects that can be children to this programming object, can be null
                Arrays.asList(
                        ProgrammingObjectType.WHILE, ProgrammingObjectType.IF, ProgrammingObjectType.FOR, ProgrammingObjectType.PRINT,
                        ProgrammingObjectType.FUNCTION, ProgrammingObjectType.VARIABLE)
        );

        drawColor = R.color.button_orange;
    }

    @Override
    public String toString() {
        if(conditionType == WhileConditionType.TRUE) {
            return "loop forever";
        } else if(conditionType == WhileConditionType.VARIABLE) {
            String text = "loop while var '" + conditionVariable.getName() + "' " + comparisonOperator.toString() + " ";

            if(terminatingValueType == WhileTerminatingValueType.TRUE) {
                text += "'true'";
            } else if(terminatingValueType == WhileTerminatingValueType.FALSE) {
                text += "'false'";
            } else if(terminatingValueType == WhileTerminatingValueType.VARIABLE) {
                text += "var '" + terminatingVariable.getName() + "'";
            } if(terminatingValueType == WhileTerminatingValueType.CUSTOM_VALUE) {
                text += "var '" + customTerminatingValue + "'";
            }

            return text;
        } else if(conditionType == WhileConditionType.CUSTOM_EXPRESSION) {
            return "loop while " + customConditionExpression;
        }

        return "";
    }

    @Override
    public List<ProgrammingObjectType> getAllowedChildTypes() {
        return allowedChildTypes;
    }

    @Override
    public String getTypeName() {
        return "While";
    }

    @Override
    public int getDrawColor() {
        return drawColor;
    }

    @Override
    public void toScript(StringBuilder stringBuilder) {



        /*
            Ok, now I think I see what you are doing.
            while (conditionType comparisonOperator terminatingValue){
                do Something
           }

            so all we need to do is incorporate the basecase or break statement.
         */

        /*
            we could do something like this:
            while (variable conditionOperator variable){
                do something
                terminatingVariable event to break the while (either iterative incrementation/break/var = false/etc...)
            }

         */

        if(conditionType == WhileConditionType.TRUE) {
            //This needs to break at some point
            stringBuilder.append("while(true) {\n");

        } else if(conditionType == WhileConditionType.VARIABLE) {
            stringBuilder.append("while(");
            stringBuilder.append(conditionVariable.getName());

            //This wont always be coupled to a comparison operator
            stringBuilder.append(comparisonOperator.toString());

             //I was thinking that the terminating value was the basecase.
            //this would have to be set inside the while loop.

            if(terminatingValueType == WhileTerminatingValueType.TRUE) {
                stringBuilder.append("true) {\n");
            } else if(terminatingValueType == WhileTerminatingValueType.FALSE) {
                stringBuilder.append("false) {\n");


            } else if(terminatingValueType == WhileTerminatingValueType.VARIABLE) {
                stringBuilder.append(terminatingVariable.getName() + ") {\n");

            } if(terminatingValueType == WhileTerminatingValueType.CUSTOM_VALUE) {
                stringBuilder.append(customTerminatingValue + ") {\n");
            }
        } else if(conditionType == WhileConditionType.CUSTOM_EXPRESSION) {
            stringBuilder.append("while(" + customConditionExpression + ") {\n");
        }

        for (ProgrammingObject child : children) {
            child.toScript(stringBuilder);
        }

        //I think that we should be able to set a variable to something down here:
        //For example
        /*
        while(variable){
            do something
            variable = false;
        }
        or
        while (variable < someOtherVariable){
            do something
            variable = variable + 1;
        }
        or
        while(true){
            if(something)
                break;
           }

           or any one of the million other scenarios we could do : -)
         */



        stringBuilder.append("}\n");
    }

    public WhileConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(WhileConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public Variable getConditionVariable() {
        return conditionVariable;
    }

    public void setConditionVariable(Variable conditionVariable) {
        this.conditionVariable = conditionVariable;
    }

    public String getCustomConditionExpression() {
        return customConditionExpression;
    }

    public void setCustomConditionExpression(String customConditionExpression) {
        this.customConditionExpression = customConditionExpression;
    }

    public ComparisonOperator getComparisonOperator() {
        return comparisonOperator;
    }

    public void setComparisonOperator(ComparisonOperator comparisonOperator) {
        this.comparisonOperator = comparisonOperator;
    }

    public WhileTerminatingValueType getTerminatingValueType() {
        return terminatingValueType;
    }

    public void setTerminatingValueType(WhileTerminatingValueType terminatingValueType) {
        this.terminatingValueType = terminatingValueType;
    }

    public Variable getTerminatingVariable() {
        return terminatingVariable;
    }

    public void setTerminatingVariable(Variable terminatingVariable) {
        this.terminatingVariable = terminatingVariable;
    }

    public String getCustomTerminatingValue() {
        return customTerminatingValue;
    }

    public void setCustomTerminatingValue(String customTerminatingValue) {
        this.customTerminatingValue = customTerminatingValue;
    }
}
