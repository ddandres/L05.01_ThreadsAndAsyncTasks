/*
 * Copyright (c) 2016. David de Andrés and Juan Carlos Ruiz, DISCA - UPV, Development of apps for mobile devices.
 */

package labs.dadm.l0501_threadsandasynctasks;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

// THIS CLASS HAS BEEN DEPRECATED IN API LEVEL 30
// Displays a count using a ProgressBar and a TextView.
// The count is executed on background using an AsyncTask, and
// updates are notified to the UI via the available interface.
public class AsyncTaskActivity extends AppCompatActivity {

    // Maximum count value
    static private final int MAX_COUNT = 100;

    // Hold references to View objects
    ProgressBar progressBar;
    TextView tvProgress;
    Button bStart;
    Button bPause;
    Button bStop;

    // Hold references to the background Thread and the UI Handler
    CountAsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_problem);

        // Keep a reference to:
        // the ProgressBar displaying the current progress of the count (init 0, max 100)
        // the TextView displaying the progress of the count in text format (x/100)
        // the Buttons to start, pause/continue and stop the count
        progressBar = findViewById(R.id.pbProgress);
        tvProgress = findViewById(R.id.tvProgress);
        bStart = findViewById(R.id.bStart);
        bPause = findViewById(R.id.bPause);
        bStop = findViewById(R.id.bStop);

        findViewById(R.id.bStart).setOnClickListener(v -> startCount());
        findViewById(R.id.bPause).setOnClickListener(v -> pauseCount());
        findViewById(R.id.bStop).setOnClickListener(v -> stopCount());

        // Set the initial value of the count to 0
        tvProgress.setText(String.format(getResources().getString(R.string.progress), 0));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, R.string.deprecated, Toast.LENGTH_SHORT).show();
    }

    // Handles the event to start the count.
    private void startCount() {
        // The count starts, so disable the start button and enable the other two
        bStart.setEnabled(false);
        bPause.setEnabled(true);
        bStop.setEnabled(true);

        // Create new asynchronous task (cannot be reused)
        task = new CountAsyncTask(this);
        // Run the task
        task.execute(progressBar.getMax());
    }

    // Handles the event to pause/resume the count.
    public void pauseCount() {
        // Pause/Resume the background thread
        task.setPause(!task.isPause());

        // Change the text of the button depending on the state of the background thread
        if (task.isPause()) {
            // Thread is paused, so display Continue text
            bPause.setText(R.string.continue_button);
        } else {
            // Thread is running, so display Pause text
            bPause.setText(R.string.pause_button);
        }
    }

    // Handles the event to stop the count.
    private void stopCount() {
        // Stop the background thread
        task.setStop();

        resetUI();
    }

    // Sets the UI to its initial state
    private void resetUI() {
        // Display the Pause text
        bPause.setText(R.string.pause_button);
        // The count has ended, so enable the start button and disable the other two
        bStart.setEnabled(true);
        bPause.setEnabled(false);
        bStop.setEnabled(false);
    }

    // Performs the count in background, notifies the UI through the available interface.
    private static class CountAsyncTask extends AsyncTask<Integer, Integer, Void> {

        private final WeakReference<AsyncTaskActivity> activity;

        // Current value of the count
        private int currentProgress;

        // Pause the count
        private boolean pause;
        // Stop the count (ends the thread)
        private boolean stop;

        void setStop() {
            this.stop = true;
        }

        void setPause(boolean pause) {
            this.pause = pause;
        }

        boolean isPause() {
            return pause;
        }

        CountAsyncTask(AsyncTaskActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        // Increases the count each 50ms until reaching the maximum count or the thread is stopped.
        @Override
        protected Void doInBackground(Integer... params) {
            // Starting new count, so do not pause nor stop the count
            pause = false;
            stop = false;

            // Current value of the count (init 0)
            currentProgress = 0;

            // Keep counting until the maximum threshold is reached or the count is requested to stop
            while ((currentProgress < MAX_COUNT) && !stop) {
                try {
                    // Busy-wait for 50ms
                    Thread.sleep(50);

                    // Increase the count only when it is not paused
                    if (!pause) {
                        // Increase the count
                        currentProgress++;
                        // Notify the UI thread about the current progress of the count
                        publishProgress(currentProgress);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        // Update the ProgressBar and the TextView with the new value
        @Override
        protected void onProgressUpdate(Integer... values) {
            // Get progress from Message
            int progress = values[0];
            // Update UI elements accordingly
            this.activity.get().progressBar.setProgress(progress);
            this.activity.get().tvProgress.setText(String.format(
                    this.activity.get().getString(R.string.progress), progress));
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            // The count has reached its end, so notify the main thread
            if (currentProgress == MAX_COUNT) {
                this.activity.get().resetUI();
            }
        }
    }

    // Pauses the task when the activity is going to be paused
    @Override
    protected void onPause() {
        // If the background thread is running then pause it
        if ((task != null) && !task.isPause()) {
            pauseCount();
        }
        super.onPause();
    }

    // Stops the task when the activity is going to be destroyed
    @Override
    protected void onDestroy() {
        // If the background thread is running then stop it
        if (task != null) {
            stopCount();
        }
        super.onDestroy();
    }
}