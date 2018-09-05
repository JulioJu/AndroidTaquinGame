package fr.uga.julioju.taquingame.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.support.constraint.ConstraintLayout;

import android.support.v7.app.AppCompatActivity;

import fr.uga.julioju.taquingame.share.CreateView;
import fr.uga.julioju.taquingame.share.DetectScreen;
import fr.uga.julioju.taquingame.taquin.TaquinActivity;

public class CameraActivity extends AppCompatActivity
        implements View.OnClickListener {

    /** Should be seen as the Constructor of this class */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        int smallestWidth = DetectScreen.getSmallestWidth(this);
        ConstraintLayout layout = CreateView.createLayout(this);

        Button button = new Button(this);
        button.setOnClickListener(this);
        int buttonId = CreateView.createTextView(button, layout,
                "Click", smallestWidth, false);

        CreateView.centerAView(layout, buttonId);

    }

    @Override
    public void onClick(View view) {
        Intent intentOutcome = new Intent(this, TaquinActivity.class);
        // Third activity called returns its result to the first activity
        // To well understand this flag:
        // https://gist.github.com/mcelotti/cc1fc8b8bc1224c2f145
        intentOutcome.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        // To forward Intent parameter through chains of Activities:
        // https://stackoverflow.com/a/12905952
        intentOutcome.putExtras(super.getIntent());
        super.startActivity(intentOutcome);
        super.finishAndRemoveTask();
    }

}
