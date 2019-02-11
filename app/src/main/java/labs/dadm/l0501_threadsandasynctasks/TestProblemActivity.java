/*
 * Copyright (c) 2016. David de Andrés and Juan Carlos Ruiz, DISCA - UPV, Development of apps for mobile devices.
 */

package labs.dadm.l0501_threadsandasynctasks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Displays a count using a ProgressBar and a TextView.
 * This does not work as expected, as the main thread blocks the UI and
 * the progress is not displayed on the screen.
 * */
public class TestProblemActivity extends AppCompatActivity {

    // Hold references to View objects
    ProgressBar progressBar;
    TextView tvProgress;
    Button bStart;
    Button bPause;
    Button bStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_problem);

        /*
         * Keep a reference to:
         *   the ProgressBar displaying the current progress of the count (init 0, max 100)
         *   the TextView displaying the progress of the count in text format (x/100)
         *   the Buttons to start, pause/continue and stop the count
         * */
        progressBar = findViewById(R.id.pbProgress);
        tvProgress = findViewById(R.id.tvProgress);
        bStart = findViewById(R.id.bStart);
        bPause = findViewById(R.id.bPause);
        bStop = findViewById(R.id.bStop);

        // Set the initial value of the count to 0
        tvProgress.setText(String.format(getResources().getString(R.string.progress), 0));
    }

    /*
     * Handles the event to start the count.
     * */
    public void startCount(View view) {

        // The count starts, so disable the start button and enable the other two
        bStart.setEnabled(false);
        bPause.setEnabled(true);
        bStop.setEnabled(true);

        // Current value of the count
        int currentProgress = 0;
        // Maximum value of the count
        int maxProgress = progressBar.getMax();

        // Keep counting until the maximum threshold is reached
        while (currentProgress < maxProgress) {
            try {
                // Wait for 50ms
                Thread.sleep(50);
                // Increase the count
                currentProgress++;
                // Update the ProgressBar and the TextView with the new value
                progressBar.setProgress(currentProgress);
                tvProgress.setText(String.format(
                        getResources().getString(R.string.progress), currentProgress));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // The count has ended, so enable the start button and disable the other two
        bStart.setEnabled(true);
        bPause.setEnabled(false);
        bStop.setEnabled(false);
    }

    /*
     * Handles the event to pause the count.
     * */
    public void pauseCount(View view) {
        // This is not going to work
        Toast.makeText(TestProblemActivity.this, R.string.still_wont_work, Toast.LENGTH_SHORT).show();
    }

    /*
     * Handles the event to stop the count.
     * */
    public void stopCount(View view) {
        // This is not going to work
        Toast.makeText(TestProblemActivity.this, R.string.still_wont_work, Toast.LENGTH_SHORT).show();
    }
}
