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
import android.support.constraint.ConstraintSet;

import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import fr.uga.julioju.taquingame.share.DetectScreen;
import fr.uga.julioju.taquingame.taquin.TaquinActivity;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    public static final String EXTRA_MESSAGE =
        "fr.uga.julioju.taquingame.main.NUMBER_OF_SQUARES";

    private ArrayList<RadioButton> radioButtonArray;

    private int smallestWidth = 0;

    private int createTextView(Context context,
            ConstraintLayout layout) {
        String string = "Choose dimension of the puzzle";
        TextView textView = new TextView(context);
        int textViewId = View.generateViewId();
        textView.setId(textViewId);
        ConstraintLayout.LayoutParams layoutParamsWrap =
            new ConstraintLayout.LayoutParams(
                    ConstraintSet.WRAP_CONTENT,
                    ConstraintSet.WRAP_CONTENT);
        textView.setLayoutParams(layoutParamsWrap);
        textView.setText(string);
        if (this.smallestWidth >= 600) {
            layoutParamsWrap.setMargins(0, 0, 0, 22);
            textView.setTextSize(45);
        } else {
            layoutParamsWrap.setMargins(0, 0, 0, 12);
            textView.setTextSize(25);
        }
        layout.addView(textView);
        return textViewId;
    }

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
            if (this.smallestWidth >= 600) {
                radioButton.setTextSize(40);
            }
            radioButton.setOnClickListener(this);
            radioGroup.addView(radioButton);
            radioButtonArray.add(radioButton);
        }
        layout.addView(radioGroup);


        // Create TextView
        int textViewId = this.createTextView(context, layout);

        // ConstraintSet should be set after layout.addView(radioButton);
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);
        set.connect(radioGroupId, ConstraintSet.LEFT, ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT);
        set.connect(radioGroupId, ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                ConstraintSet.TOP);
        set.connect(radioGroupId, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT);
        set.connect(radioGroupId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM);
        set.connect(textViewId, ConstraintSet.BOTTOM, radioGroupId,
                ConstraintSet.TOP);
        set.connect(textViewId, ConstraintSet.LEFT, ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT);
        set.connect(textViewId, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT);
        set.applyTo(layout);

    }

    /** Should be seen as the Constructor of this class */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.smallestWidth = DetectScreen.getSmallestWidth(this);

        ConstraintLayout layout;

        layout = new ConstraintLayout(this);
        layout.setId(View.generateViewId());
        layout.setLayoutParams(new ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT));
        super.setContentView(layout);

        this.createRadioGroup(this, layout);

    }

    @Override
    public void onClick(View view) {
        RadioButton radioButton = (RadioButton) view;
        String message = String.valueOf(
                this.radioButtonArray.indexOf(radioButton) + 2);
        Intent intent = new Intent(this, TaquinActivity.class);
        intent.putExtra(EXTRA_MESSAGE, message);
        super.startActivity(intent);
    }

}
