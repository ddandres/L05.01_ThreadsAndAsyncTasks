/*
 * Copyright (c) 2016. David de Andrés and Juan Carlos Ruiz, DISCA - UPV, Development of apps for mobile devices.
 */

package labs.dadm.l0501_threadsandasynctasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
        switch (view.getId()) {
            // Activity showing the actual problem
            case R.id.bProblem:
                intent = new Intent(DashboardActivity.this, TestProblemActivity.class);
                break;

            // Solution using Thread and Message
            case R.id.bThreadmessage:
                intent = new Intent(DashboardActivity.this, ThreadMessageActivity.class);
                break;

            // Solution using Thread and Runnable
            case R.id.bThreadRunnable:
                intent = new Intent(DashboardActivity.this, ThreadRunnableActivity.class);
                break;

            // Solution using AsyncTask
            case R.id.bAsyncTask:
                intent = new Intent(DashboardActivity.this, AsyncTaskActivity.class);
                break;
        }
        startActivity(intent);
    }
}
