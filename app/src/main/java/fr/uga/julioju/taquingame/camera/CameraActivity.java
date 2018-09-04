package fr.uga.julioju.taquingame.camera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.support.constraint.ConstraintLayout;

import android.support.v7.app.AppCompatActivity;

import fr.uga.julioju.taquingame.main.MainActivity;
import fr.uga.julioju.taquingame.share.CreateView;
import fr.uga.julioju.taquingame.share.DetectScreen;
import fr.uga.julioju.taquingame.taquin.TaquinActivity;

public class CameraActivity extends AppCompatActivity
        implements View.OnClickListener {

    public static final String EXTRA_MESSAGE =
        "fr.uga.julioju.taquingame.camera.DATA";

    private int smallestWidth = 0;

    /** Should be seen as the Constructor of this class */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.smallestWidth = DetectScreen.getSmallestWidth(this);
        ConstraintLayout layout = CreateView.createLayout(this);

        Button button = new Button(this);
        button.setOnClickListener(this);
        int buttonId = CreateView.createTextView(button, layout,
                "Click", smallestWidth, false);

        CreateView.centerAView(layout, buttonId);

    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode,
            Intent intentIncome) {
        String isEndOfApp = intentIncome
            .getStringExtra(TaquinActivity.EXTRA_MESSAGE_IS_END_OF_APP);

        Intent intentOutcome = new Intent(this, MainActivity.class);
        intentOutcome.putExtra(TaquinActivity.EXTRA_MESSAGE_IS_END_OF_APP,
                isEndOfApp);
        super.setResult(Activity.RESULT_OK, intentOutcome);
        super.finishAndRemoveTask();
    }

    @Override
    public void onClick(View view) {
        Intent intentIncome = super.getIntent();
        String gridLength = intentIncome
            .getStringExtra(MainActivity.EXTRA_MESSAGE);

        Intent intentOutcome = new Intent(this, TaquinActivity.class);
        intentOutcome.putExtra(EXTRA_MESSAGE, gridLength);
        super.startActivityForResult(intentOutcome, 0);
    }


}
