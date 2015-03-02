package group5.cs3750.trainingwheels;

/**
 * Created by Scott on 2/19/2015.
 */
import android.app.Dialog;
import android.content.Context;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class CustomDialog {
    private Dialog dialog;
    private Button leftButton, rightButton;
    private TextView text;
    private LinearLayout contentLayout;

    public CustomDialog(Context context, boolean cancelable, String dialogText, int layoutResource, String leftButtonText, String rightButtonText) {
        dialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setCancelable(cancelable);

        text = (TextView) dialog.findViewById(R.id.custom_dialog_text);
        contentLayout = (LinearLayout) dialog.findViewById(R.id.custom_dialog_content);
        leftButton = (Button) dialog.findViewById(R.id.custom_dialog_left_button);
        rightButton = (Button) dialog.findViewById(R.id.custom_dialog_right_button);

        contentLayout.addView(View.inflate(context, layoutResource, null));

        leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        leftButton.setBackgroundDrawable(TrainingIDE.getBackgroundGradientDrawable(context.getResources(), R.color.button_red, 12, 0));

        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        rightButton.setBackgroundDrawable(TrainingIDE.getBackgroundGradientDrawable(context.getResources(), R.color.button_blue, 12, 0));

        if(dialogText != null)
            text.setText(dialogText);
        else
            text.setVisibility(View.GONE);

        if(leftButtonText != null)
            leftButton.setText(leftButtonText);
        else
            leftButton.setVisibility(View.GONE);

        if(rightButtonText != null)
            rightButton.setText(rightButtonText);
        else
            rightButton.setVisibility(View.GONE);
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public Button getLeftButton() {
        return leftButton;
    }

    public void setLeftButton(Button leftButton) {
        this.leftButton = leftButton;
    }

    public Button getRightButton() {
        return rightButton;
    }

    public void setRightButton(Button rightButton) {
        this.rightButton = rightButton;
    }

    public TextView getText() {
        return text;
    }

    public void setText(TextView text) {
        this.text = text;
    }

    public void setCancelable(boolean cancelable) {
        dialog.setCancelable(cancelable);
    }

    public LinearLayout getContentLayout() {
        return contentLayout;
    }

    public void setContentLayout(LinearLayout contentLayout) {
        this.contentLayout = contentLayout;
    }
}