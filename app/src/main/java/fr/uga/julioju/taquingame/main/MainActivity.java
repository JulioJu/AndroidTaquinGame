package fr.uga.julioju.taquingame.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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

import fr.uga.julioju.taquingame.picture.PictureActivity;
import fr.uga.julioju.taquingame.taquin.TaquinActivity;
import fr.uga.julioju.taquingame.util.CreateView;
import fr.uga.julioju.taquingame.util.DetectScreen;

/** Choose number of squares the game should be */
public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    public static final String EXTRA_MESSAGE_GRID_LENGTH =
        "fr.uga.julioju.taquingame.main.NUMBER_OF_SQUARES";

    private static final int IS_END_OF_APP_REQUEST = 27;

    private ArrayList<RadioButton> radioButtonArray;

    private int smallestWidth = 0;

    /** Create a RadioGroup in center of the PARENT View */
    @SuppressLint("SetTextI18n")
    private void createRadioGroup(Context context,
            ConstraintLayout layout) {

        int smallestWidth = DetectScreen.getSmallestWidth(this);

        int orientation = super.getResources().getConfiguration().orientation;

        int numberOfButtons;
        if (smallestWidth >= 600) {
            numberOfButtons = 9;
        }
        else {
            numberOfButtons = 7;
        }
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
                "Choose dimension\nof the puzzle", null, smallestWidth, true,
                false);

        // ConstraintSet should be set after layout.addView(radioButton);
        CreateView.centerAView(layout, radioGroupId);
        CreateView.viewCenteredInTopOfOtherView(layout, titleId,
                radioGroupId,
                orientation == Configuration.ORIENTATION_LANDSCAPE);
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
        // Check which request we're responding to
        if (requestCode == MainActivity.IS_END_OF_APP_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                boolean isEndOfApp = intentIncome.getBooleanExtra(TaquinActivity
                        .EXTRA_MESSAGE_IS_END_OF_APP, true);
                android.util.Log.d("isEndOfApp", "" + isEndOfApp);
                if (isEndOfApp) {
                    super.finishAndRemoveTask();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        RadioButton radioButton = (RadioButton) view;
        int gridLength = this.radioButtonArray.indexOf(radioButton) + 2;
        Intent intent = new Intent(this, PictureActivity.class);
            intent.putExtra(EXTRA_MESSAGE_GRID_LENGTH, gridLength);
        super.startActivityForResult(intent,
                MainActivity.IS_END_OF_APP_REQUEST);
    }

}
