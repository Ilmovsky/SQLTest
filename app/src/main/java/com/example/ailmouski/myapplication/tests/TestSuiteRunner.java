package com.example.ailmouski.myapplication.tests;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;

import com.example.ailmouski.myapplication.ZeteticApplication;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class TestSuiteRunner extends AsyncTask<ResultNotifier, TestResult, Void> {

    String TAG = getClass().getSimpleName();
    private ResultNotifier notifier;
    private Activity activity;
    private boolean isCreate;

    public TestSuiteRunner(Activity activity, boolean isCreate) {
        this.activity = activity;
        this.isCreate = isCreate;
        if (this.activity != null) {
            this.activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

    }

    @Override
    protected Void doInBackground(ResultNotifier... resultNotifiers) {
        this.notifier = resultNotifiers[0];
        Log.i(ZeteticApplication.TAG, String.format("Running test suite on %s platform", Build.CPU_ABI));
        runSuite(isCreate);
        return null;
    }

    @Override
    protected void onProgressUpdate(TestResult... values) {
        notifier.send(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        notifier.complete();
        if (this.activity != null) {
            this.activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void runSuite(boolean isCreate) {

        SQLiteDatabase.loadLibs(ZeteticApplication.getInstance());
        for (SQLCipherTest test : getTestsToRun(isCreate)) {
            try {
                Log.i(ZeteticApplication.TAG, "Running test:" + test.getName());
                TestResult result = test.run();
                publishProgress(result);

            } catch (Throwable e) {
                Log.i(ZeteticApplication.TAG, e.toString());
                publishProgress(new TestResult(test.getName(), false, e.toString()));
            }
        }
    }

    private List<SQLCipherTest> getTestsToRun(boolean isCreate) {
        List<SQLCipherTest> tests = new ArrayList<>();
        if (isCreate) {
            tests.add(new AttachDatabaseTest());
        } else {
            tests.add(new MUTF8ToUTF8WithNullMigrationTest());
        }
        return tests;
    }
}
