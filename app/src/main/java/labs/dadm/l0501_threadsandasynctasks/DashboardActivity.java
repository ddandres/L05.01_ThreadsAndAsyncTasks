/*
 * Copyright (c) 2016. David de Andrés and Juan Carlos Ruiz, DISCA - UPV, Development of apps for mobile devices.
 */

package labs.dadm.l0501_threadsandasynctasks;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/*
 * Displays a set of Buttons that give access to test problem of dealing with
 * background tasks and updating the UI from them.
 * */
public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    /*
     * Starts the different activities of the application
     * */
    public void launchActivity(View view) {

        Intent intent = null;

        // Determine what to do depending on the Button clicked
        final int buttonClicked = view.getId();
        if (buttonClicked == R.id.bProblem) {
            // Activity showing the actual problem
            intent = new Intent(DashboardActivity.this, TestProblemActivity.class);
        } else if (buttonClicked == R.id.bThreadmessage) {
            // Solution using Thread and Message
            intent = new Intent(DashboardActivity.this, ThreadMessageActivity.class);
        } else if (buttonClicked == R.id.bThreadRunnable) {
            // Solution using Thread and Runnable
            intent = new Intent(DashboardActivity.this, ThreadRunnableActivity.class);
        } else if (buttonClicked == R.id.bThreadRunOnUi) {
            // Solution using Thread and Runnable
            intent = new Intent(DashboardActivity.this, ThreadRunOnUiActivity.class);
        } else if (buttonClicked == R.id.bAsyncTask) {
            // Solution using AsyncTask
            intent = new Intent(DashboardActivity.this, AsyncTaskActivity.class);
        }
        startActivity(intent);
    }
}
