package labs.dadm.l0501_threadsandasynctasks;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

// Displays a count using a ProgressBar and a TextView.
// The count is executed on background using a thread, and
// updates are notified to the UI via runOnUiThread().
public class ThreadRunOnUiActivity extends AppCompatActivity {

    // Maximum count value
    static private final int MAX_COUNT = 100;

    // Hold references to View objects
    ProgressBar progressBar;
    TextView tvProgress;
    Button bStart;
    Button bPause;
    Button bStop;

    // Hold references to the background Thread
    ThreadRunOnUiActivity.CountThread thread;

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

    // Handles the event to start the count.
    private void startCount() {
        // The count starts, so disable the start button and enable the other two
        bStart.setEnabled(false);
        bPause.setEnabled(true);
        bStop.setEnabled(true);

        // Create new background thread (cannot be reused once started)
        thread = new ThreadRunOnUiActivity.CountThread(this);
        // Run the background thread
        thread.start();
    }

    // Handles the event to pause/resume the count.
    private void pauseCount() {
        // Pause/Unpause the background thread
        thread.setPause(!thread.isPause());

        // Change the text of the button depending on the state of the background thread
        if (thread.isPause()) {
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
        thread.setStop();
        // Wait for the background thread to die
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        finishCount();
    }

    // Updates the ProgressBar and the TextView with the new value
    public void updateCount(int count) {
        progressBar.setProgress(count);
        tvProgress.setText(String.format(
                getResources().getString(R.string.progress), count));
    }

    // Sets the UI to its initial state
    public void finishCount() {
        // Display the Pause text
        bPause.setText(R.string.pause_button);
        // The count has ended, so enable the start button and disable the other two
        bStart.setEnabled(true);
        bPause.setEnabled(false);
        bStop.setEnabled(false);
    }

    // Performs the count in background, notifies the UI through a Message.
    private class CountThread extends Thread {
        // Current value of the count
        int currentProgress;
        // Pause the count
        private boolean pause;
        // Stop the count (ends the thread)
        private boolean stop;

        final WeakReference<ThreadRunOnUiActivity> reference;

        void setStop() {
            this.stop = true;
        }

        void setPause(boolean pause) {
            this.pause = pause;
        }

        boolean isPause() {
            return pause;
        }

        CountThread(ThreadRunOnUiActivity activity) {
            super();
            this.reference = new WeakReference<>(activity);
        }

        // Increases the count each 50ms until reaching the maximum count or the thread is stopped.
        @Override
        public void run() {
            // Starting new count, so do not pause nor stop the count
            pause = false;
            stop = false;

            // Start count from 0
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
                        // The Runnable is sent to the UI thread
                        if (reference.get() != null) {
                            runOnUiThread(() -> reference.get().updateCount(currentProgress));
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // The count has reached its end, so notify the main thread
            if (currentProgress == MAX_COUNT) {
                // The Runnable is sent to the UI thread
                // Reset the UI to its initial state
                if (reference.get() != null) {
                    runOnUiThread(() -> reference.get().finishCount());
                }
            }
        }
    }

    // Pauses the thread when the activity is going to be paused
    @Override
    protected void onPause() {
        // If the background thread is running then pause it
        if ((thread != null) && thread.isAlive() && !thread.isPause()) {
            pauseCount();
        }
        super.onPause();
    }

    // Stops the thread when the activity is going to be destroyed
    @Override
    protected void onDestroy() {
        // If the background thread is running then stop it
        if ((thread != null) && thread.isAlive()) {
            stopCount();
        }
        super.onDestroy();
    }
}