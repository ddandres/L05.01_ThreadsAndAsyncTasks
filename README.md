# L05.01_ThreadsAndAsyncTasks
Lecture 05 - 01 Thread and AsyncTask, DISCA - UPV, Development of apps for mobile devices.

The DashboardActivity gives access to different activities exemplifiying how to execute tasks in background and communicate results to the user interface (UI):
- TestProblemActivity: It shows the problem to be solved. A ProgressBar should increase its progress each 50ms until paused, stopped, or the maximum progress is reached. A TextView should also display that progress as a text. The problem here is that the main thread is blocked (wait for 50ms) so no update is visible until the maximum progress is reached.
- ThreadMessageActivity: The work is executed by a Thread in background, so that the UI is not blocked. The main thread creates a custom Handler to receive and process incoming Message from the background thread (notifications to update the UI with new information).
- ThreadRunnableActivity: The work is executed by a Thread in background, so that the UI is not blocked. The main thread creates a default Handler to receive and process incoming Runnable from the background thread (the code to update the UI with new information).
- AsynTaskActivity: The work is executed by an AsynTask in background, so that the UI is not blocked. The AsyncTask overrides the required methods for the main thread to execute the code to update the UI with new information.
