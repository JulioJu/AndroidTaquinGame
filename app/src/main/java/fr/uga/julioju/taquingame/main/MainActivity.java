package fr.uga.julioju.taquingame.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import android.support.constraint.ConstraintLayout;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import fr.uga.julioju.taquingame.camera.CameraActivity;
import fr.uga.julioju.taquingame.share.CreateView;
import fr.uga.julioju.taquingame.share.DetectScreen;
import fr.uga.julioju.taquingame.taquin.TaquinActivity;

/** Choose number of squares the game should be */
public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    public static final String EXTRA_MESSAGE =
        "fr.uga.julioju.taquingame.main.NUMBER_OF_SQUARES";

    private ArrayList<RadioButton> radioButtonArray;

    private int smallestWidth = 0;

    /** Create a RadioGroup in center of the PARENT View */
    @SuppressLint("SetTextI18n")
    private void createRadioGroup(Context context,
            ConstraintLayout layout) {

        int numberOfButtons = 9;
        this.radioButtonArray = new ArrayList<>(numberOfButtons);

        RadioGroup radioGroup = new RadioGroup(context);
        int radioGroupId = View.generateViewId();
        radioGroup.setId(radioGroupId);
        ConstraintLayout.LayoutParams radioGroup_LayoutParams =
            new ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        radioGroup.setLayoutParams(radioGroup_LayoutParams);
        radioGroup.setOrientation(LinearLayout.VERTICAL);

        // radioGroup.setGravity(Gravity.START);

        for (int index = 0 ; index < numberOfButtons ; index++) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setId(View.generateViewId());
            ViewGroup.LayoutParams radio_LayoutParams = new ViewGroup
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            radioButton.setLayoutParams(radio_LayoutParams);
            radioButton.setText(String.format("%s X %s squares",
                        String.valueOf(index + 2), String.valueOf(index + 2)));
            // Toast.makeText(this, String.valueOf(this.smallestWidth),
            //         Toast.LENGTH_SHORT).show();
            CreateView.setTextSize(radioButton, this.smallestWidth);
            radioButton.setOnClickListener(this);
            radioGroup.addView(radioButton);
            radioButtonArray.add(radioButton);
        }

        layout.addView(radioGroup);

        // Create title
        int titleId = CreateView.createTextView(new TextView(this), layout,
                "Choose dimension of the puzzle", smallestWidth, true);

        // ConstraintSet should be set after layout.addView(radioButton);
        CreateView.centerAView(layout, radioGroupId);
        CreateView.viewCenteredInTopOfOtherView(layout, titleId,
                radioGroupId);
    }

    /** Should be seen as the Constructor of this class */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.smallestWidth = DetectScreen.getSmallestWidth(this);
        ConstraintLayout layout = CreateView.createLayout(this);

        this.createRadioGroup(this, layout);

    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode,
            Intent intentIncome) {
        boolean isEndOfApp = Boolean.parseBoolean(intentIncome
            .getStringExtra(TaquinActivity.EXTRA_MESSAGE_IS_END_OF_APP));
        android.util.Log.d("isEndOfApp", "" + isEndOfApp);
        if (isEndOfApp) {
            super.finishAndRemoveTask();
        }
    }

    @Override
    public void onClick(View view) {
        RadioButton radioButton = (RadioButton) view;
        String message = String.valueOf(
                this.radioButtonArray.indexOf(radioButton) + 2);
        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        super.startActivityForResult(intent, 0);
    }

}
