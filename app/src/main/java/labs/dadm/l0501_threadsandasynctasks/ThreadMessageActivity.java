/*
 * Copyright (c) 2016. David de Andrés and Juan Carlos Ruiz, DISCA - UPV, Development of apps for mobile devices.
 */

package labs.dadm.l0501_threadsandasynctasks;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

/*
 * Displays a count using a ProgressBar and a TextView.
 * The count is executed on background using a thread, and
 * updates are notified to the UI via a Message.
 */
public class ThreadMessageActivity extends AppCompatActivity {

    // Identifies a message that wants to update the count progress
    static private final int UPDATE_PROGRESS = 0;
    // Identifies a message notifying that the count has reached its end
    static private final int COUNT_FINISHED = 1;

    // Hold references to View objects
    ProgressBar progressBar;
    TextView tvProgress;
    Button bStart;
    Button bPause;
    Button bStop;

    // Hold references to the background Thread and the UI Handler
    CountThread thread;
    CountHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_problem);

        /*
         * Keep a reference to:
         *   the ProgressBar displaying the current progress of the count (init 0, max 100)
         *   the TextView displaying the progress of the count in text format (x/100)
         *   the Buttons to start, pause/resume and stop the count
         */
        progressBar = findViewById(R.id.pbProgress);
        tvProgress = findViewById(R.id.tvProgress);
        bStart = findViewById(R.id.bStart);
        bPause = findViewById(R.id.bPause);
        bStop = findViewById(R.id.bStop);

        // Set the initial value of the count to 0
        tvProgress.setText(String.format(getResources().getString(R.string.progress), 0));

        // Create the Handler associated to the UI (main) thread
        handler = new CountHandler(this);

    }

    /*
     * Handles the event to start the count.
     */
    public void startCount(View view) {

        // The count starts, so disable the start button and enable the other two
        bStart.setEnabled(false);
        bPause.setEnabled(true);
        bStop.setEnabled(true);

        // Create new background thread (cannot be reused once started)
        thread = new CountThread();
        // Run the background thread
        thread.start();
    }

    /*
     * Handles the event to pause/unpause the count.
     */
    public void pauseCount(View view) {
        pauseCount();
    }

    /*
     * Handles the event to pause/resume the count.
     */
    private void pauseCount() {

        // Pause/Resume the background thread
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


    /*
     * Handles the event to stop the count.
     */
    public void stopCount(View view) {
        stopCount();
    }

    /*
     * Handles the event to stop the count.
     */
    private void stopCount() {

        // Stop the background thread
        thread.setStop();
        // Wait for the background thread to die
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        resetUI();
    }

    /*
     * Sets the UI to its initial state
     */
    private void resetUI() {
        // Display the Pause text
        bPause.setText(R.string.pause_button);
        // The count has ended, so enable the start button and disable the other two
        bStart.setEnabled(true);
        bPause.setEnabled(false);
        bStop.setEnabled(false);
    }

    /*
     * Performs the count in background, notifies the UI through a Message.
     */
    private class CountThread extends Thread {

        // Pause the count
        private boolean pause;
        // Stop the count (ends the thread)
        private boolean stop;
        // Message to notify IU thread about any update
        Message message;

        void setStop() {
            this.stop = true;
        }

        void setPause(boolean pause) {
            this.pause = pause;
        }

        boolean isPause() {
            return pause;
        }

        /*
         * Increases the count each 50ms until reaching the maximum count or the thread is stopped.
         */
        @Override
        public void run() {

            // Starting new count, so do not pause nor stop the count
            pause = false;
            stop = false;

            // Current value of the count (init 0)
            int currentProgress = 0;
            // Maximum value of the count
            int maxProgress = progressBar.getMax();

            // Keep counting until the maximum threshold is reached or the count is requested to stop
            while ((currentProgress < maxProgress) && !stop) {
                try {
                    // Busy-wait for 50ms
                    Thread.sleep(50);

                    // Increase the count only when it is not paused
                    if (!pause) {
                        // Increase the count
                        currentProgress++;
                        // Message to notify the UI thread about the current progress of the count
                        // Includes the Handler, what the message is about, and an data object
                        message = Message.obtain(handler, UPDATE_PROGRESS, currentProgress);
                        // Send the message to the UI thread via the defined Handler
                        message.sendToTarget();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // The count has reached its end, so notify the main thread
            if (currentProgress == maxProgress) {
                // Message to notify the main thread that the count has reached its end
                // Includes the Handler and what the message is about
                message = Message.obtain(handler, COUNT_FINISHED);
                // Send the message to the UI thread via the defined Handler
                message.sendToTarget();
            }
        }
    }

    /*
     * Process messages associated to the UI (main) Thread.
     */
    static class CountHandler extends Handler {

        private final WeakReference<ThreadMessageActivity> reference;

        CountHandler(ThreadMessageActivity activity) {
            super();

            this.reference = new WeakReference<>(activity);
        }

        /*
         * Receives and processes a message.
         */
        @Override
        public void handleMessage(Message msg) {

            final ThreadMessageActivity activity = reference.get();

            // Determine what to do depending on the Message received
            switch (msg.what) {

                // Update the ProgressBar and the TextView with the new value
                case UPDATE_PROGRESS:
                    // Get progress from Message
                    int progress = (int) msg.obj;
                    // Update UI elements accordingly
                    activity.progressBar.setProgress(progress);
                    activity.tvProgress.setText(String.format(
                            activity.getResources().getString(R.string.progress), progress));
                    break;

                case COUNT_FINISHED:
                    activity.resetUI();
                    break;
            }
        }
    }

    /*
     * Pauses the thread when the activity is going to be paused
     */
    @Override
    protected void onPause() {
        // If the background thread is running then pause it
        if ((thread != null) && thread.isAlive() && !thread.isPause()) {
            pauseCount();
        }
        super.onPause();
    }

    /*
     * Stops the thread when the activity is going to be destroyed
     */
    @Override
    protected void onDestroy() {
        // If the background thread is running then stop it
        if ((thread != null) && thread.isAlive()) {
            stopCount();
        }
        super.onDestroy();
    }
}
