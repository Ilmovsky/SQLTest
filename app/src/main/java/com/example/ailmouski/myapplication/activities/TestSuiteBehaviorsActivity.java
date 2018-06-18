package com.example.ailmouski.myapplication.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ailmouski.myapplication.R;
import com.example.ailmouski.myapplication.TestResultAdapter;
import com.example.ailmouski.myapplication.ZeteticApplication;
import com.example.ailmouski.myapplication.tests.ResultNotifier;
import com.example.ailmouski.myapplication.tests.TestResult;
import com.example.ailmouski.myapplication.tests.TestSuiteRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TestSuiteBehaviorsActivity extends Activity implements ResultNotifier {

    private String TAG = this.getClass().getSimpleName();
    ListView resultsView;
    List<TestResult> results;
    View statsView;
    File testResults;

    public TestSuiteBehaviorsActivity() {
        results = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.main);
        testResults = new File(getApplication().getFilesDir(), "test-results.log");
        deleteTestResultsLog();
        onButtonClick(null);
    }

    public void onButtonClick(View view) {
        deleteTestResultsLog();
        results.clear();
        hideStats();
        findViewById(R.id.executeSuite).setEnabled(false);
        resultsView = findViewById(R.id.test_suite_results);
        ZeteticApplication.getInstance().setCurrentActivity(this);
        new TestSuiteRunner(this, getIntent().getBooleanExtra("isCreate", false)).execute(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void send(TestResult result) {

        results.add(result);
        Log.i(TAG, String.format("%s - success:%s", result.getName(), result.isSuccess()));
        HeaderViewListAdapter adapter = (HeaderViewListAdapter) resultsView.getAdapter();
        if (adapter == null) {
            statsView = View.inflate(this, R.layout.test_stats, null);
            resultsView.addHeaderView(statsView);
            hideStats();
            resultsView.setAdapter(new TestResultAdapter(ZeteticApplication.getInstance(), results));
        } else {
            ((ArrayAdapter<TestResult>) adapter.getWrappedAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void complete() {

        TextView stats = (TextView) statsView.findViewById(R.id.stats);
        int successCount = 0;
        List<String> failedTests = new ArrayList<String>();
        for (TestResult result : results) {
            if (result.isSuccess()) {
                successCount += 1;
            } else {
                failedTests.add(result.getName());
            }
        }
        String message = String.format("Passed: %d  Failed: %d", successCount, results.size() - successCount);
        deleteTestResultsLog();
        try {
            FileOutputStream resultStream = new FileOutputStream(testResults);
            resultStream.write(String.format("%s\n", message).getBytes());
            if (failedTests != null) {
                for (String test : failedTests) {
                    resultStream.write(test.getBytes());
                }
            }
            resultStream.flush();
            resultStream.close();
        } catch (Exception e) {
            Log.i(TAG, "Failed to write test suite results", e);
        }
        Log.i(TAG, message);
        stats.setText(message);
        stats.setVisibility(View.VISIBLE);
        findViewById(R.id.executeSuite).setEnabled(true);
    }

    private void deleteTestResultsLog() {
        if (testResults.exists()) {
            testResults.delete();
        }
    }

    private void hideStats() {
        if (statsView != null) {
            statsView.findViewById(R.id.stats).setVisibility(View.GONE);
        }
    }
}